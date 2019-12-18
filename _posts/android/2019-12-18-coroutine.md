---
title: "Coroutine"
categories:
    - Android
---
★app.gradle 디펜던시에 다음을 추가
{% highlight java %}
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.1'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.0'
{% endhighlight %}

★Light weight thread, 스레드는 아니지만 비동기 프로그래밍이 가능하도록 한 것.

★Cooperation Routine을 줄임. Routine은 function을 말한다. 즉 여러 함수들이 협력한다는 의미, 여러 함수들이 번갈아가며 실행되어
비동기적인 프로그래밍이 가능해진다.

{% highlight kotlin %}
var num = 0
Log.d("dhlog", "start")
num = 111
Log.d("dhlog", "$num")
GlobalScope.launch{
    delay(5000)
    Log.d("dhlog", "코루틴에서 $num")
}
num = 222
Log.d("dhlog", "$num")
num = 333
Log.d("dhlog", "$num")
Log.d("dhlog", "end")
{% endhighlight %}

★GlobalScope내의 코드가 코루틴에서 수행되는데 비동기적으로 수행되므로 딜레이후 가장 마지막에 로그가 출력된다.
GlobalScope는 코루틴이 실행 되는 구간을 대충 의미. launch{...} 내부에서 또 다시 launch로 다른 코루틴을 실행할 수 있음.

★launch(), async(), withContext() : 코루틴을 실행한다. launch는 리턴 값이 없고, async는 Deffered<T>객체를 반환하도록 한다.
withContext()는 async와 같으나 await()을 호출할 필요가 없다. 즉 무조건 결과가 리턴될 때 까지 기다린다.

★Deffered<T>클래스는 await() 메소드를 가짐. 이 메소드는 작업이 완료될 때 까지 기다렸다가 T타입의 객체를 리턴.

{% highlight kotlin %}
GlobalScope.launch {
    launch {
        Log.d("dhlog", "log at launch at launch")
    }
    val num = async {
        val int = 10
        Log.d("dhlog", "log at async at launch = $int")
        return@async int
    }.await()   //  await()이 걸리면 async문이 return할 때 까지 바깥 코루틴이 일시정지함.
    Log.d("dhlog", "num = $num")
}
{% endhighlight %}

★코루틴안에서 일반적인 메소드는 호출 못함. suspend(잠시 멈춤)과 resume(다시시작)이 일어날 수 있기 때문.
코루틴에서 실행할 수 있는 메소드를 만드려면 메소드 정의시 suspend를 붙이면 됨.
suspend수식어가 붙은 함수가 실행되면 함수가 끝날 때 까지 스레드를 blocking하지 않고 suspend시킨다. suspending은
blocking에 비해 코스트가 낮다.

`근데 왜 잘 되세요????`

★코루틴이 실행될 스레드를 지정할 수 있다. launch() 메소드의 인자로 스레드 타입넣어 준다.

★스레드 타입 : Dispatchers.XXX
1. Dispatchers.Main : 메인 스레드, UI작업
2. Dispatchers.IO : 저장소나 네트워크에서 읽는 입출력
3. Dispatchers.Default : 그 외의 작업들

★CoroutineScope, GlobalScope
 
1. GlobalScope : Application의 생명주기를 따른다. 만약 액티비티 실행 중에 데이터를 불러오는 작업을 하다가, 중간에 액티비티를
종료하는 경우에는 더 이상 데이터 로딩을 백그라운드에서 계속할 필요가 없다. 이 경우 GlobalScope에서 실행하는 것은
적절하지 않다. 이 경우에는 CoroutineScope을 활용한다. 다만 장시간동안 동작해야 할 thread가 필요하다면 매번 생성하는
Coroutine보다 효율적이다. 이러한 GlobalScope를 사용하더라도 안드로이드 환경에서는 백그라운드 잡이 또 필요하여
WorkManager를 활용해야 한다.

2. CoroutineScope : CoroutineContext(Dispatchers.MAIN, IO, DEFAULT)를 통해 원하는 방식으로 생명주기를 관리할 수 있다.
Android환경에서 이를 사용하기 위해 CoroutineScope을 상속받고, 이를 생명주기에 맞춰 사용하며 Activity가 destroy될 때
Job을 종료하는 것을 추천하고 있다. Job은 CoroutineScope의 동작을 제어하는 역할을 한다.
{% highlight kotlin %}
class MyActivity : AppCompatActivity(), CoroutineScope {

  // Job을 등록할 수 있도록 초기화
  lateinit var job: Job

  // 기본 Main Thread 정의와 job을 함께 초기화
  override val coroutineContext: CoroutineContext
      get() = Dispatchers.Main + job

  override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      job = Job()
  }

  // 작업 중이던 모든 job을 종 children을 종료 처리
  override fun onDestroy() {
      super.onDestroy()
      job.cancel() // Cancel job on activity destroy. After destroy all children jobs will be cancelled automatically
  }
}
{% endhighlight %}

★DIspatchers : 코루틴의 스레드 형태를 어떻게 가질 지 정의. 일반적으로 IO와 Main스레드가 있음.