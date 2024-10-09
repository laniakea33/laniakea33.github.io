---
title: "[JetPack] ViewModel"
categories:
    - Android
---
* [공식 문서 링크](https://developer.android.com/topic/libraries/architecture/viewmodel)

* ViewModel : 액티비티, 프레그먼트 등의 UI state를 생명주기에 맞춰 관리한다. 화면 방향 변경 등의 스테이트 체인지에도
    새로 쿼리를 하거나 하지않고 데이터를 유지하도록 할 수 있다. 즉 액티비티, 프레그먼트에서 UI데이터를 분리한 것.
    MVVM의 VM의 용도로 사용할 수는 있는데 기본적으로 전혀 다른 개념. 이건 AAC의 ViewModel

* ViewModel클래스를 상속하여 구현한다.

* 클래스 구현 예시
{% highlight kotlin %}
class MyViewModel : ViewModel() {
        private val users: MutableLiveData<List<User>> by lazy {
            MutableLiveData().also {
                loadUsers()
            }
        }

        fun getUsers(): LiveData<List<User>> {
            return users
        }

        private fun loadUsers() {
            // Do an asynchronous operation to fetch users.
        }
    }
{% endhighlight %}

* 이후 액티비티에서 위 라이브데이터를 observe하여 UI를 업데이트 한다.
{% highlight kotlin %}
val model = ViewModelProviders.of(this)[MyViewModel::class.java]
model.getUsers().observe(this, Observer<List<User>>{ users ->
    // update UI
})
{% endhighlight %}

* 액티비티가 재생성되면 동일한 viewmodel인스턴스를 받게 된다.

* 액티비티가 완전히 종료되면 프레임워크가 ViewModel의 onCleared() 호출한다. 오버라이드하여 리소스를 정리하는데 사용할 수 있다.
    
* ViewModel의 스코프는 ViewModelProvider에 전달된 Lifecycle로 인해 결정된다.
    액티비티의 경우 onDestroy()까지, 프레그먼트는 분리될때 까지 남는다.
    
* 한 액티비티 내부의 두 프레그먼트에서 하나의 ViewModel을 공유할 수 있다.
    이 때 Lifecycle에는 홀더 액티비티 객체를 넣어주면 된다.
    
* Room와 LiveData를 함께 사용하며 CursorLoader와 같이 사용할 수 있다.

* 코루틴을 지원한다. 

* 참조 : [아키텍처 구성요소와 함께 Kotlin 코루틴 사용][https://developer.android.com/topic/libraries/architecture/coroutines?hl=ko]