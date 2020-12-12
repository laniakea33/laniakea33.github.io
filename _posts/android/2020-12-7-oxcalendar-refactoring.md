---
title: "OX캘린더 MVVM 리팩토링(with RxJava2, Databinding, Hilt)"
categories:
    - Android
---
안드로이드 플랫폼은 예로부터 완벽 구현이 어려운 MVC 아키텍처 모델을 탈피하려는 시도가 계속되고있다. MVP에서 MVVM과 MVI까지
많은 시도와 발전이 거듭되고 있고, 구글에서 공식적으로 안드로이드 플랫폼에서 MVVM를 응용한 아키텍처 모델을 장려하며 구현에 도움이 되는 수 많은 라이브러리들을
출시함으로써 어느정도 대세가 되었다. 이제 아키텍처를 공부하지 않으면 새로 출시되는 이러한 기술들을 이해할 수 없어졌고, 반대로 이러한 아키텍처를 공부한다면
새로 출시 되는 기술들이 어떤 흐름속에서 태어난 것인지 알게 됨으로써 안드로이드 생태계의 패러다임과 유행, 앞으로의 방향성을 짐작할 수 있게 되리라 생각한다.
이러한 패러다임을 손으로 느껴보기 위한 공부차원에서 토이프로젝트이자 스파게티 덩어리인 OX캘린더의 소스를 리팩토링해 보고자 한다.

현재의 코드는 대충 아래와 같다.

Jetpack Room을 활용해 내부 DB인 SQLite를 사용한 앱이기 때문에 dao객체를 앱 시작시 하나 만들어 App클래스의 companion object로 가지고있어
전역적으로 사용할 수 있도록 했다.
{% highlight kotlin %}
class App: Application() {

    companion object {
        lateinit var dao: RoomDao
    }

    override fun onCreate() {
        super.onCreate()
        dao = RoomDB.getInstance(applicationContext).dao()
        ...
    }
}
{% endhighlight %}

CalendarFragment는 한 장의 달력을 보여주는 Fragment이며 App에서 dao객체를 참조해 쿼리를 실행한다.
스레딩 및 비동기 이벤트 처리는 RxJava를 사용하고 있고, DB에서 데이터를 가져오는 데 성공하면 비즈니스 로직을 통해 가공 후
RecyclerView에 데이터를 세팅한다. 

{% highlight kotlin %}
class CalendarFragment() : Fragment(), DateViewAdapter.OnDateClickListener {

    private lateinit var records: MutableList<Record>
    ...

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        searchRecords(info.id, firstDate, lastDate, year, month)
    }

    private fun searchRecords(infoId: Int, firstDate: Long, lastDate: Long, year: Int, month: Int) {
        //  App클래스의 Companion object에서 dao를 참조.
        App.dao.searchRecords(infoId, firstDate, lastDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    this.records = it.toMutableList()
                    val dateNumbs = getDateNumList(year, month)
                    recyclerView.let { view ->
                        setData(...)
                    }
                }.also { compositeDisposable.add(it) }
    }
    
    ...
}
{% endhighlight %}

Fragment가 View이자 Controller의 역할을 하고 있고, 스테이트의 관리가 데이터 유지를 위해 Fragment의
전역변수(records)로써 되고있는데 사실 스테이트라고 하기도 쫌 그렇다.

UI에 세팅하기 위해 가공이 필요한 데이터도 state라고 하나..?

아무튼 이렇게 Fragment의 여러곳에서 이 records를 참조하고, 변경하고, 요리조리 들쑤시다보니 언제 어디서 어떻게 변경되었는지 추적하기가
점점 어려워졌고 뷰 로직과 뒤엉켜 오작동 하는경우가 허다했으며 코드는 점점 길어져만 갔다.
그래서 이걸 정리하는 김에 구글에서 제시하는 아키텍처 표준모델을 따라 MVVM 리팩토링을 해보기로 했다. 

- [앱 아키텍처 가이드 공식문서](https://developer.android.com/jetpack/guide?hl=ko)

아키텍처라는게 굉장히 추상적인 개념이고, 필수사항이란 것이 없이 권장사항이기도 하고, 그 구현방식도 정답이 있는 것이 아니기 때문에
맞고 틀림을 판단하는 연습도 중요하지만 원칙을 세우고 유지시키는 연습이 더 중요한 것 같다.
그래서 아래와 같이 핵심적인 원칙을 세워보았다.

핵심원칙은 아래와 같다.
1. Repository : 데이터 IO
2. ViewModel : Repository로부터 Output된 Data를 가공하여 state업데이트, 유저 액션을 수신하여 Repository로 전달
3. View : ViewModel로부터 state업데이트를 구독하여 뷰 갱신 및 유저 액션을 ViewModel로 전달.
4. Repository -> ViewModel -> View의 단방향 참조(View는 ViewModel을 안다. ViewModel은 Repository를 알고 View는 모른다. Repository는 아무것도 모르고 해달라는 거만 해준다.)
5. RxJava를 이용해 이벤트를 전달. 
6. DataBinding을 사용해 View와 데이터를 바인딩함.
7. ViewModel은 스테이트 관리를 위한 데이터 스트림을 LiveData로써 가짐.
8. 각 계층의 의존성은 Hilt를 사용해 주입함.

하위계층부터 상위계층순서로 만들어 보자.

- AppModule : Hilt와 Dagger를 활용해 의존성 주입을 하기 위한 Module. 
중간중간 Dagger와 Hilt가 섞여있는데 처음에는 Dagger로 했다가 Hilt로 마이그레이션이 덜 끝나서 그럼.

{% highlight kotlin %}
@InstallIn(ApplicationComponent::class) //  이 모듈은 앱 수명주기를 따르게 된다.
@Module
class AppModule {

    //  Repository는 인터페이스이며 RepositoryImpl이 구현하고 있음
    //  만약 DataSource가 추가되면 여기서 간단히 수정하면 될 것 같다.
    @Singleton  //  이 객체가 앱의 수명주기를 따르게 한다
    @Provides
    fun repository(dao: RoomDao): Repository {
        return RepositoryImpl(dao)
    }

    @Singleton  //  이 객체가 앱의 수명주기를 따르게 한다
    @Provides
    fun dao(@ApplicationContext context: Context): RoomDao {
        return RoomDB.getInstance(context).dao()
    }
}
{% endhighlight %}

- Repository : 확장성을 위한 인터페이스

{% highlight kotlin %}
interface Repository {
    
    //  이런저런 쿼리 파라미터를 받고, 출력데이터를 Single에 담아 반환하도록 규약을 정함.
    fun searchRecords(infoId: Int, firstDate: Long, lastDate: Long): Single<List<Record>>
    ...
}
{% endhighlight %}

- RepositoryImpl : Repository인터페이스를 Room DB를 활용할 수 있도록 구현한 클래스

{% highlight kotlin %}
@Singleton
class RepositoryImpl(val dao: RoomDao) : Repository {

    //  RxJava의 io Scheduler에서 입출력하고, main thread에서 반환하는 함수 정의
    override fun searchRecords(infoId: Int, firstDate: Long, lastDate: Long): Single<List<Record>> {
        return dao.searchRecords(infoId, firstDate, lastDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
    
    ...
}
{% endhighlight %}

- BaseViewModel : 기본적인 공통 기능을 가진 ViewModel객체를 정의함. AAC의 ViewModel을 상속하는데 앱에서 화면 회전 등을
지원할 생각이 없기 때문에 사실 그럴 필요는 없는 듯. 암튼 핵심적인 기능은 CompositeDisposable을 가지고, dispose하는 것.

{% highlight kotlin %}
abstract class BaseViewModel: ViewModel() {

    protected val compositeDisposable = CompositeDisposable()

    fun onDestroy() {
        compositeDisposable.dispose()
    }

}
{% endhighlight %}

- BaseViewModelFragment : 기본적인 공통 기능을 가진 Fragment를 정의함. BaseViewModel과 CompositeDisposable를 갖고
BaseViewModel과 자신의 CompositeDisposable을 dispose하는 것이 목적.

{% highlight kotlin %}
abstract class BaseViewModelFragment: Fragment() {

    protected val compositeDisposable = CompositeDisposable()
    abstract val viewModel: BaseViewModel

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
        viewModel.onDestroy()
    }

}
{% endhighlight %}

- CalendarViewModel : Repository를 주입받는 ViewModel. 사용자 action을 전달받아 Repository에 데이터를 요청하고,
응답을 Single객체로 받는다. 그 후 데이터 가공을 통해 State를 업데이트 한다. 사용자가 달력의 한 날짜를 탭하면
onDateClicked()가 호출되며 해당하는 Model객체를 받고 날짜(date)에 해당하는 데이터를 MutableLiveData인
recordDateItemClicked를 통해 발행한다. CalendarFragment는 이 LiveData를 옵저빙하다가 데이터가 발행되면
기록을 추가하는 Activity를 실행하도록 한다. 이 ViewModel은 Fragment의 생명주기동안 유지된다.

{% highlight kotlin %}
@FragmentScoped
class CalendarViewModel @Inject constructor(val repository: Repository): BaseViewModel() {

    ...
    val recordDateListState = MutableLiveData<MutableList<RecordDateModel>>()
    val recordDateItemClicked = MutableLiveData<Long>()
    ...

    fun searchRecordDateModel(year: Int, month: Int, info: Info) {
        ...
        compositeDisposable += repository.searchRecords(info.id, firstDateLong, lastDateLong)
                .subscribe { records ->
                    val recordDateModelList = someLogic(records) //  길어서 생략함
                    recordDateListState.value = recordDateModelList
                }
    }
    
    fun onDateClicked(model: RecordDateModel) {
        recordDateItemClicked.value = model.record.date
    }
}
{% endhighlight %}

- CalendarFragment : 달력의 한 페이지를 그리는 Fragment. recordDateItemClicked등 ViewModel의 state를
옵저빙한다. Activity를 열고 Result를 받는 등의 역할을 한다.

{% highlight kotlin %}
@AndroidEntryPoint
class CalendarFragment : BaseViewModelFragment() {

    @Inject
    override lateinit var viewModel: CalendarViewModel
    private lateinit var adapter: CalendarAdapter
    ...
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            ...
            adapter = CalendarAdapter(requireContext(), viewModel)
            binding.recyclerView.adapter = adapter
            
            //  Record의 업데이트 작업이 완료되면 recordDateItemUpdated에서 데이터 발행
            //  데이터가 발행이 되면 adapter.notifyItemChanged() 호출하여 갱신
            viewModel.recordDateItemUpdated.observe(this.viewLifecycleOwner, Observer {
                adapter.notifyItemChanged(it.first)
            })
            
            //  달력의 날짜 탭을 감지하면 recordDateItemClicked에서 데이터 발행.
            //  옵저빙하여 액티비티 호출
            viewModel.recordDateItemClicked.observe(this.viewLifecycleOwner, Observer {
                goRecordActivity(it)
            })
    }
}
{% endhighlight %}

- CalendarFragment의 xml레이아웃 파일. viewModel을 variable로 가지고 있다. recordDateListState를
RecyclerView에 바인딩 해 뒀다.

{% highlight xml %}
<layout>
    <data>
        <variable
            name="viewModel"
            type="com.dh.oxcalendar.ui.main.CalendarViewModel"
             />
    </data>

    <androidx.recyclerview.widget.RecyclerView
        app:layoutManager="androidx.recyclerview.widget.GridLayoutManager"
        app:spanCount="7"
        app:listItem="@{viewModel.recordDateListState}"
        />
</layout>
{% endhighlight %}

- 바인딩 어댑터를 통해 listItem attribute에 설정된 리스트를 RecyclerView의 submitList를 호출하여 설정해 줌.
이 함수는 데이터의 변경이 있는 뷰홀더만 업데이트 해 줌.

{% highlight kotlin %}
@BindingAdapter("listItem")
fun <T> applyListItem(view: RecyclerView, list: List<T>?) {
    list?.let {
        if (view.adapter is CalendarAdapter) {
            (view.adapter as CalendarAdapter).submitList(it as List<RecordDateModel>)
        }
        ...
    }
}
{% endhighlight %}

- CalendarAdapter : Adapter에 ViewModel객체를 전달해 줘 ViewHolder가 가진 List중 position에 해당하는
데이터 모델 객체를 뷰 홀더와 바인딩 시킴. 

{% highlight kotlin %}
class CalendarAdapter(
        val context: Context,
        val viewModel: CalendarViewModel
): ListAdapter<RecordDateModel, CalendarAdapter.CalendarViewHolder>(ItemDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_calendar_list_item,
                parent,
                false))
    }
    
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(position)
    }
    
    inner class CalendarViewHolder(val binding: LayoutCalendarListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    
        fun bind(position : Int) {
            viewModel.recordDateListState.value?.get(position)?.let {
                binding.model = it
            }
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }
    ...
    
}
{% endhighlight %}

- ViewHolder의 Xml 레이아웃 파일. UI model은 RecordDateModel객체이고,
 action은 CalendarViewModel에 정의되어 있음.

{% highlight xml %}
<layout>
    <data>
        <variable
            name="model"
            type="com.dh.oxcalendar.model.RecordDateModel" />
        <variable
            name="viewModel"
            type="com.dh.oxcalendar.ui.main.CalendarViewModel" />
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout
        ...
        android:background="@{model.backgroundResourceId}"
        android:onClick="@{() -> viewModel.onDateClicked(model)}"
        android:visibility="@{model.visibility}"
        ...
        >

        <TextView
            ...
            android:text="@{model.dateString}"
            android:textColor="@{model.dateTextColorString}"
            />

        <TextView
            ...
            android:text="@{model.memoText}"
            android:background="@{model.oxImageResourceId}"
            ...
            />

        <TextView
            ...
            android:text="@{model.bottomText}"
            ...
            />
    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>
{% endhighlight %}

추후 시도해 볼 것
1. Xml레이아웃 + DataBinding -> Compose