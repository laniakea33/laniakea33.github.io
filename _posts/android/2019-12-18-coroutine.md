---
title: "Coroutine"
categories:
    - Android
---
나중에 정리할 때 참고할 것 : https://tv.naver.com/v/15354002/list/629240


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

* CoroutineScope에서 부터 모든 코루틴은 시작한다. launch, sync등의 함수는 모두 CorouinenScore의 확장 함수

★GlobalScope내의 코드가 코루틴에서 수행되는데 비동기적으로 수행되므로 딜레이후 가장 마지막에 로그가 출력된다.
GlobalScope는 코루틴이 실행 되는 구간을 대충 의미. launch{...} 내부에서 또 다시 launch로 다른 코루틴을 실행할 수 있음.

★launch(), async(), withContext() : 코루틴을 실행한다. launch는 리턴 값이 없고, async는 Deffered<T>객체를 반환하도록 한다.
withContext()는 async와 같으나 await()을 호출할 필요가 없다. 즉 무조건 결과가 리턴될 때 까지 기다린다.

* launch vs async
공통점 : 새로운 코루틴을 실행함. suspend fun이 아님. CoroutineScope 내에서 실행. 언제든 Exception전파
차이점 : launch()는 결과값이 필요하지 않은 경우 사용.
async()는 결과 값을 리턴 받아야 하는 경우 사용. 이 때 await()를 호출 할 때 까지 suspend되지 않는다. async()를 연달아 실행시키면 async블럭 밖에서는 계속 진행이 되고, async블럭 내부 구문은 또 따로 진행이 된다. 
그러다가 await()가 호출되면 결과값이 return될 때까지 기다림. 이를 활용 해 두 가지 async()로 두 가지 API를 동시에 각각 호출하여 한번에 반환하는 함수를 만들 수 있다.
await()호출시 까지 Exception전파하지 않는다.

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
 
1. GlobalScope : Application의 생명주기를 따른다. 그러나 대부분의 경우 사용하면 안됨. 
컨텍스트의 생명주기에 맞춰 캔슬할 수가 없기 때문. 코루틴에서 Consumer-Producer패턴을 구현한 채널이라는 객체가 있음. 이걸 사용할 때는 사용해도 괜찮음.
만약 액티비티 실행 중에 데이터를 불러오는 작업을 하다가, 중간에 액티비티를
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

* CoroutineContext : 4가지 Element를 가지고 있음
Job : 코루틴의 라이프사이클 관리. New -> Active -> Completing/Cancelling -> Completed/Cancelled
Job은 계층구조를 형성한다. Parent Job이 취소가 될 때 Child Job들도 취소를 시키기 위함. 예외도 마찬가지로 Child에서 Parent로 전파됨.
전파하고 싶지 않은 경우 Superviser job을 사용. 코루틴 내의 블로킹 작업은 isActive로 코루틴의 상태를 체크하여 수행할 것. 

CoroutineDispatcher : 스레딩
코루틴의 스레드 형태를 어떻게 가질 지 정의. 일반적으로 IO와 Main스레드가 있음. Default와 IO는 동일한 스레드 풀을 공유한다.
Unconfined : caller 스레드를 그대로 사용함. 일반적으로 사용하지 않음.
suspend fun내 에서 withContext(Dispatchers) 함수로 주로 사용. 레트로핏에서 래핑해 줘서 Main에서도 안전하게 사용할 수 있음
jetpack의 viewModel(viewModelScope), activity, fragment(lifecycleScope)에는 scope가 정의되어 있기 때문에 쉽게 갖다 쓸 수 있음.

CoroutineExceptionHandler : 예외 처리 핸들러

CoroutineName : 네이밍
새로운 코루틴은 부모의 CoroutineContext를 상속한다. CoroutineContext는 그 자체로 Element를 가지고 있고, 이 Element에 해당하는 Key를 갖고있다.
이 Key는 Job, CoroutineDispatcher등이다. coroutineContext[Job]과 같은 오퍼레이터를 사용해 참조할 수 있음. 

* Coroutine의 원리
suspend fun을 자바 바이트 코드로 디컴파일 해 보면 Continuation이라는 파라메터 하나가 추가되어 있음을 확인할 수 있다.
Continuation은 인터페이스인데 CoroutineContext객체 하나, resumeWith이라는 함수 하나로 구성되어 있다.
suspend fun이 호출되면 함수의 실행 state를 Continuation객체가 State Machine으로써 관리 하게 된다. 
Continuation객체는 함수 내부의 suspend함수 호출부에 label을 붙이고, 0부터 차례대로 실행하며, 파라메터를 아래로 계승해 나간다.
즉 0번 함수 호출시 파라메터가 1개였다면, 1번 함수의 파라메터는 기존의 1개의 파라메터 + 0번 함수의 결과 1개를 더해 2개가 되는 것.
0번 함수의 결과는 Continuation객체가 받아 뒀다가 1번 함수를 실행할 때 전달된다. StateMachine은 Callback과 달리
상태를 저장할 하나의 객체만을 사용한다는 장점이 있다. 따라서 콜백 헬 등 콜백의 단점에서 탈피할 수 있게 된다.

* 코루틴의 장점
1. 구조화된 동시성 처리 : RxJava와 비슷함. 메모리릭이 발생할 수 있을 때 경고를 하지만, 코루틴은 스코프를 통해 새로운 스코프를 실행하는 것을 방지
필요할 떄 스코프 내의 모든 코루틴을 캔슬한다.
2. 성능(동시성) : 싱글 스레드에서 여러 코루틴을 동시에 실행 할 수 있음
3. 계층 구조를 따라 취소가 자동으로 전파 됨.
4. 비동기 코드를 콜백 대신 순차적인 코드로 작성할 수 있음. Jetpack에서 다양한 형태로 코루틴을 지원함

* 코루틴과 LiveData응용
액티비티, 뷰 등은 생명주기가 있기 때문에 LiveData에 데이터를 담고 구독하는 식으로 주로 구현한다. 여기서 LiveData Builder를 사용하여 emit안에 suspend fun을 넣어 호출하면
LiveData를 갱신해 줄 수 있다. 오퍼레이터에도 사용하면 간단해 짐.