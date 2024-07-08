---
title: "Coroutine"
categories:
    - Android
---
이 포스트는 [코틀린 동시성 프로그래밍](http://www.kyobobook.co.kr/product/detailViewKor.laf?mallGb=KOR&ejkGb=KOR&barcode=9791161754222) 도서의 내용을 토대로 작성되었습니다.

# 용어 정리

### 동시성과 병렬성

#### 동시성
입력이 같다는 가정하에 항상 같은 결과를 반환하지만 그 내부적 실행순서에는 가변성을 허용하는 것. 1코어 2스레드 환경에서 2가지 알고리즘을 각 스레드에서 실행할 경우 코어는 2개의 프로세스를 빠르게 번갈아가며 스케줄링하여 실행한다. 하나의 시점에 1개의 알고리즘만 실행된다.

#### 병렬성
하나의 시점에 정확히 2개의 알고리즘이 실행되고 있는것. 최소한 2코어 2스레드가 필요하다. 코어개념을 컴퓨터 단위로 확장시키면 분산 컴퓨팅이 된다. 병렬성이 있으면 동시성도 있으나, 동시성이 있을 때 병렬성이 꼭 있지는 않다.

### CPU바운드, IO바운드

#### CPU바운드

어떤 알고리즘의 수행속도가 CPU연산속도에 달려있으면 `CPU바운드`라고 한다. CPU바운드 알고리즘의 경우 장치의 CPU코어수에 맞게 스레드 갯수를 조절해야 컨텍스트 스위칭에 따른 오버헤드를 줄일 수 있다.

#### IO바운드

어떤 알고리즘의 수행속도가 IO장치의 속도에 달려있으면 `IO바운드`라고 한다. IO바운드 알고리즘은 주로 대기를 하는게 일이므로 스레드가 많을수록 유리하다고 볼 수 있다.

#### 레이스 컨디션

동시성 코드가 일정한 순서로 완료되어야 정상작동하는 코드에서, 완료 순서가 일정하지 않은 상태를 말함.

#### 원자성 위반

여러 실행흐름이 동시에 하나의 데이터를 사용할 때 서로 간섭될수 있는 상태를 말한다. thread 2개가 같은 정수 데이터를 동시에 1씩 올렸을 때, 최종적으로 2가 아닌 1만 올라갈 수 있는 상태이다.

# 코루틴(Coroutine)

경량 스레드(Light weight thread)라고도 이야기 한다. 스레드는 아니지만 비동기 프로그래밍이 가능하도록 한 실행 흐름으로, 스레드 내부에서 실행되지만 스레드와 결합되지 않아 프레임워크가 다른 스레드로 옮겨 코루틴을 실행할 수 있다.

Cooperation Routine을 줄인 말인데, Routine은 function을 말한다. 즉 여러 함수들이 협력한다는 의미. 하나의 루틴이 실행 중 일시중단 상태에 들어가면 이 루틴이 종료되지 않았음에도 다른 루틴이 thread를 대신 사용할 수 있다. 즉 여러 함수들이 서로 양보해가며 실행된다. 루틴이 중단되더라도 thread는 중단되지 않기 때문이다(Non-Blocking).

메인 스레드를 블로킹하지 않고, 비동기적인 프로그래밍이 가능해진다.

## Suspend 함수

suspend 함수는 코루틴 빌더를 통해 만들어진 코루틴 스코프나, 다른 suspend 함수 내에서만 실행할 수 있다. Coroutine Scope내에서 기본적으로 순차 실행된다.

- delay(Long) : 일정 시간만큼 해당 코루틴 스코프의 실행을 일시정지 시킨다.
- measureTimeMillis {...} : block내의 함수들을 실행한 후 총 소모시간을 반환하는 함수, 코루틴 실행시간 측정에 쓸만함.
- withTimeout(Long) {...} : 설정된 millisecond뒤에 cancel되는 suspend함수, 취소되면 TimeoutCancellationException이 발생한다.
- withTimeoutOrNull(Long) {...} : 설정된 millisecond뒤에 cancel되며 null을 반환하는 suspend함수. 엘비스 연산자를 통해 타임아웃 예외처리가 가능하다.

## Coroutine Builder

코루틴을 만드는 함수다. Coroutine Scope를 만들며, 모든 코루틴은 이 Coroutine Scope로부터 시작한다. launch, async등의 함수는 모두 CorouinenScope의 확장 함수이다.

### runBlocking {}

기본적으로 현재 스레드에서 실행되며 해당 코드블록의 수행이 끝날 때까지 스레드의 진행을 막는다. runBlocking {}이 다른 coroutine builder내에서 호출된다면 해당 coroutine builder의 진행도 막힌다. 블록 내의 리시버는 CoroutineScope를 상속한 BlockingCoroutine 객체이다.

### launch {}

runBlocking처럼 Coroutine Scope를 만들지만, 가능한 다른 코루틴 코드를 같이 실행시키는 코루틴 빌더. launch는 블록 내의 함수를 즉시 실행하는게 아니라, 해당 코루틴이 스레드를 다 사용할 때 까지 큐에 들어가서 기다렸다가 실행된다. 해당 블록이 suspend되면 다른 코루틴이 실행된다. Coroutine Scope 내에서만 호출할 수 있다. 결과를 반환하지 않는다. 다시말해 에러가 아닌 이상 결과를 신경쓸 필요가 없는 경우 사용한다.(fire and forget 시나리오). Job 객체를 리턴한다.

~~~kotlin
fun launchCoroutineBuilder() {
    runBlocking {
        log("launch 실행 전")
        launch {    //  실행큐에 들어감 1
            log("launch1 실행")
        }
        launch {    //  실행큐에 들어감 2
            log("launch2 실행")
            delay(1000L)
        }
        delay(500L) //  suspend 함수인 delay()가 호출되는 순간 runBlocking은 500초동안 일시정지 (suspend)되고 큐에 들어가있던 launch()가 실행된다.
        log("launch 실행 후")
    }
}
~~~

launch를 자동으로 시작하지 못하게 하려면 `start = CoroutineStart.LAZY`를 사용해야 한다.

#### Job

launch {...} 의 리턴 타입이다. 이 객체를 통해 해당 코루틴 빌더의 동작을 컨트롤 할 수 있다.

**join()**

일반적으로 launch는 실행 큐에 들어간 후 스레드가 한가할때까지 기다리는데, join()함수를 사용하면 즉시 실행되며 runBlokcing{}처럼 이 launch{}가 종료될때까지 스레드를 점유한다.

**cancel()**

실행중인 launch를 종료시킨다. 기본적으로 cancel이 호출된 시점 후, 즉 job이 `cancelling`상태가 된 후 첫 suspend함수(delay()등) 호출에서 CancellationException가 발생하여 종료되는 것이다. suspend 함수가 없다면 `yield()`나 `ensureActive()`를 호출하여 중단점을 설정하거나 launch{} 내부에서 `isActive`값을 통해 명시적으로 종료처리 할 수 있다.

`try catch finally`로 전통적인 에러 및 후처리가 가능하다. 자원할당 해제와 같은 코드를 finally에서 사용할 때 같이쓰면 좋을 것 같다.

`cancelling`상태에서 새로운 coroutine scope를 시작하거나 suspend 함수를 실행할 수 없다. `isActive`를 조회하여 상태 체크 후 suspend 함수를 호출해야 하며 `cancelling` 상태에서 반드시 suspend 함수 호출이 필요한 경우, 예를 들어 `try catch finally` 구문의 finally 블록에서 DB롤백과 같은 작업이 필요한경우는 withContext(NonCancellable){}를 사용해야 한다.

`cancel()`과 `join()`을 연속적으로 호출하면 취소처리가 끝날 때 까지 job이 스레드를 점유하므로 코드 블록 실행을 방어할 수 있다. 둘을 합친 `cancelAndJoin()`도 있음.

Job도 Coroutine Scope처럼 계층구조를 형성한다. 부모 Job이 캔슬되면 자식 Job도 함께 캔슬되며, 마찬가지로 예외도 Child에서 Parent로 전파되어 Job을 최초 생성한 곳까지 전파된다. 전파하고 싶지 않은 경우 `Superviser job`을 사용해야 한다.

#### Job의 라이프사이클

`New` -> `Active` -> `Completing/Cancelling` -> `Completed/Cancelled`(최종상태라고도 부름)의 생명주기를 가진다. 라이프사이클은 역행하지 않는다.

1. `New` : launch {}또는 Job()호출시의 상태이다.
2. `Active` : `New`상태에서 `start()` 또는 `join()`을 호출하면 Job이 실행되면서 `Active`상태가 된다. start()는 코루틴을 일시중단시키지 않고, join()은 코루틴 실행을 중단시키는데, 이 때문에 start()는 굳이 suspend 함수나 코루틴에서 호출할 필요가 없다.
3. `Cancelling` : `cancel()`이 호출되면 `Cancelling` 상태가 된다.
4. `Cancelled` : 취소가 완료되거나 예외로 인해 실패한 Job()은 `Cancelled` 상태가 된다. 이 상태가 되면 `getCancellationException()`을 통해 취소 정보를 얻을 수 있다. 취소와 예외로 인한 실패를 구분하기 위해 `CEH` 또는 `invokeOnCompletion()`을 활용할 수 있다. `invokeOnCompletion()`은 `Cancelled`또는 `Completed`상태가 됐을 때 호출된다.
5. `Completed` : 실행이 완료, 취소, 예외로 인한 종료된 상태를 모두 포괄.

### asyc {}

launch{}와 같이 thread 사용이 가능해지면 실행된다. `Deferred<T>`객체를 반환하며 실행이 끝난 후의 결과를 `await()`함수를 통해 반환받을 수 있게 해 준다. 즉 결과가 필요할 땐 async{}, 필요없을 땐 launch{}를 호출하면 된다. 두 가지 `async()`로 두 가지 API를 동시에 각각 호출하여 한번에 반환하는 함수를 만들 수 있다.

~~~ kotlin
suspend fun getRandom1(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

suspend fun getRandom2(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

runBlocking {
    val elapsedTime = measureTimeMillis {
        val value1 = async { getRandom1() }
        val value2 = async { getRandom2() }

        //  .await()를 호출하는 순간 async블록 실행이 끝났는지 확인 후, 안끝났므면 끝날때까지 suspend됐다가 결과를 return한다. 다시 말해 동시성을 보장한다.
        println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}")
    }
    println(elapsedTime)
}
~~~

#### Deferred

Job의 확장이며 결과값을 갖는다. 다른 언어의 Future/Promise의 Kotlin 구현체. `async{}`호출 또는 `CompletableDeferred()`의 호출로 생성할 수 있다.

Job과 달리 Deferred는 예외를 자동으로 전파되지 않으며 예외 상황에서 await()를 호출하면 그제서야 예외가 전파된다. join()을 통해 시작시키면 자동으로 전파 안되게 할 수 있으나, 이후 결과에 접근하면 예외를 반환한다. try-catch문으로 await()호출부를 감싸거나, CEH를 사용하거나 getCancellationException()을 이용해 안전하게 예외를 가져올 수 있다.

#### Repository 구현시의 async {...} vs suspend 함수

**async 함수를 사용한 Repository 구현**
~~~kotlin
interface ProfileServiceRepository {
    fun asyncFetchByName(name: String): Deferred<Profile>
    fun asyncFetchById(id: Long): Deferred<Profile>
}

class ProfileServiceClient : ProfileServiceRepository {
    override fun asyncFetchByName(name: String) = GlobalScope.async {
        Profile(1, name, 28)
    }

    override fun asyncFetchById(id: Long) = GlobalScope.async {
        Profile(id, "Susan", 28)
    }
}
~~~

**suspend 함수를 사용한 Repository 구현**
~~~kotlin
interface ProfileServiceRepository {
    suspend fun fetchByName(name: String): Profile
    suspend fun fetchById(id: Long): Profile
}

class ProfileServiceClient : ProfileServiceRepository {
    override suspend fun fetchByName(name: String): Profile {
        return Profile(1, name, 28)
    }

    override suspend fun fetchById(id: Long): Profile {
        return Profile(id, "Susan", 28)
    }
}
~~~

- async대신 suspend 함수를 사용하면 구현 클래스에 Job과 관련된 코드가 엮이는 것을 방지할 수 있다. 따라서 인터페이스나 추상 클래스를 정의할 때는 suspend 함수를 사용한다.
- 만약 함수 내부에서 async를 호출하여 비동기로 작동한다면 repository client가 동시성을 고려하고, await()을 호출할 수 있도록 함수이름을 잘 지어줘야 한다.

### withContext(CoroutineContext) {}

상위 코루틴과는 다른 Context에서 실행할 필요가 있을 때 사용한다. runBlocking{}처럼 해당 코드블록의 수행이 끝날 때까지 스레드의 진행을 막는다. withContext()는 async와 같이 값을 반환하지만 await()을 호출할 필요가 없다. 즉 무조건 결과가 리턴될 때 까지 기다린다.

## Scope Builder

코루틴 빌더말고 스코프 빌더를 통해 스코프를 만들 수도 있다.

### coroutineScope {}

코루틴 스코프 내에서 하위 코루틴 스코프를 만든다. runBlocking{}과 비슷하게 생겼지만, coroutineScope는 현재 thread를 block하지 않으며, `suspend 함수`이므로 호출한 쪽이 일시정지한다.

코루틴은 계층적이라, 상위 코루틴은 하위 코루틴이 모두 종료될 때 까지 기다렸다가 종료되며 특정 코루틴에서 에러가 발생하면 상위, 하위 코루틴이 모두 취소된다.

~~~kotlin
fun scopeBuilder() {
    runBlocking {
        doOneTwoThree() //  suspend fun이므로 다 끝날 때 까지 runBlocking 블록은 기다림
        println("5!")   //  맨 마지막에 호출된다.
    }
}

suspend fun doOneTwoThree() = coroutineScope {
    launch {
        println("launch1")
        delay(1000L)
        println("3!")
    }

    launch {
        println("launch2")
        println("1!")
    }

    launch {
        println("launch3")
        delay(500L)
        println("2!")
    }
    println("4!")
}
~~~

## Coroutine Context

코루틴 스코프가 가지고 있는 해당 코루틴에 대한 정보. `Job`, `Dispatcher`, `CoroutineExceptionHandler(CEH)`, `CoroutineName` 등 4가지 Element를 가지고 있음. `+` 연산자를 통해 결합하고, `minusKey()` 함수를 통해 일부 요소를 제거한다.

### SupervisorJob

Coroutine Context의 구성요소. 스코프 내의 에러 전파를 계층 구조의 아래쪽으로만 전파하도록 한다. 그래서 하위 코루틴에서 에러처리를 해야 한다. 참고로 SupervisorScope는 CoroutineScope(SupervisorJob())과 같다.

### Dispatcher

여러가지 조건을 기반으로 코루틴을 수행시킬 스레드를 지정하고, 분산하는 오케스트레이터다. launch, async, withContext 등의 코루틴 빌더에 설정할 수 있다. suspend function내에서 withContext(Dispatchers) 함수로 주로 사용. `retrofit`에서는 래핑해 주기때문에 메인스레드에서도 안전하게 사용할 수 있음.

~~~kotlin
launch(Dispatchers.Default) {...}
~~~

1. Main : 메인 스레드, UI작업
2. Default :  코어 수에 비례하는 스레드 풀에서 수행한다. CPU 바운드 작업용이다. 디폴트값임.
3. IO :  코어 수 보다 훨씬 많은 스레드를 가지는 스레드 풀. IO 바운드 작업은 CPU를 덜 소모하기 때문이다.
4. Unconfined :  어디에도 속하지 않는다. 처음에는 부모의 스레드에서 시작하지만 suspention point가 오면 스레드가 바뀜. 예측할 수 없다. 일반적으로 사용하지 않음.
5. newSingleThreadContext : 항상 새로운 스레드를 만든다. 단일 스레드에서 실행시킬 때 사용한다.
6. newFixedThreadPoolContext() : 스레드풀의 스레드 갯수를 지정한다. 스레드 부하분산을 런타임이 알아서 한다.

### CoroutineExceptionHandler
예외 처리 핸들러. 해당 스코프 내에서의 에러를 핸들링한다. (단 runBlocking의 coroutineContext에서는 사용할 수 없다.)
~~~ kotlin
val ceh = CoroutineExceptionHandler { coroutineContext, exception ->
    println("Something happend: $exception")
}

val scope = CoroutineScope(Dispatchers.IO)

val job = scope.launch (ceh) {
    launch { printRandom1() }
    launch { printRandom2() }
}

job.join()
~~~

### CoroutineName
네이밍

### NonCancellable

코루틴은 Job.cancel()을 호출해 취소중 상태가 됐을 때 이 Job은 일시중지할 수 없도록 설계되어 있다. 그러나 job내부에서 자원할당 해제등의 일시중단 작업이 필수적일수가 있는데, 이럴 때는 해당 함수를 withContext(NonCancellable) {...} 블럭 내에서 실행시켜야 한다. 이 블럭은 해당 코루틴의 취소여부와 상관없이 동작의 완료를 보장한다.

## GlobalScope

소속 스코프가 없이 전역적으로 사용 가능한 코루틴 스코프이다. 어떤 계층에도 속하지 않고, Application의 생명주기를 따르기 때문에 Activity 등 개별 Context에 맞춰 캔슬해야 하는 안드로이드 환경에서는 보통 GlobalScope대신 CoroutineScope(CoroutineContext)를 쓴다.

# Flow

코루틴에서 사용 가능한 비동기 스트림(RxJava의 Cold Observable과 유사하다.). flow 빌더를 사용해 블럭을 연다.
~~~kotlin
flow {
    emit(1)
}.collect()

flowOf(1,2,3,4,5).collect()

listOf(1,2,3,4,5).asFlow().collect()
~~~
- collect()를 호출해야 데이터 발행을 시작한다.
- withTimeoutOrNull()를 통해 시간 제한 및 취소가 가능하다.

<br>

## Operator

플로우의 연산자. RxJava와 유사하게 데이터 플로우의 연산이 가능하다. collect, reduce, fold, toList, toSet 등은 플로우를 끝내는 연산자라 종단 연산자라고 부른다.

- Flow.map('변환식')
- Flow.filter('조건식')
- Flow.filterNot('조건식')
- Flow.transform('변환 함수')
- Flow.take('collect문에서 받을 갯수 제한')
- Flow.takeWhile('collect문에서 받을 조건 제한식. 제한식에서 false를 반환할 때 까지 결과를 받는다.')
- Flow.drop('collect문에서 버릴 갯수 제한. N개를 버린 이후부터 결과를 받는다.')
- Flow.dropWhile('collect문에서 버릴 조건 제한식. 제한식에서 false를 반환할 때 까지 결과를 버린다.')
- var result = Flow.reduce('계산식')
- var result = Flow.fold('초기값', '계산식')
- var result = Flow.count('조건식')

Flow의 데이터 발행은 기본적으로 코루틴 스코프 내에서 실행되며, flow scope 내에서 함부로 Dispatcher를 사용해 스레드를 전환할 수 없다. RxJava의 subscribeOn과 같이 `flowOn(Dispatcher)`을 통해, 해당 플로우의 실행 스레드를 미리 지정해야 한다.

## 플로우 버퍼링

Flow 발행자와 결과 수신자가 처리속도가 다른 경우, 특히 수신자의 처리 속도가 느린 경우 수신 처리가 끝나야 다음 데이터를 발행하므로 전체 처리속도가 느려지게 된다. 또한 스레드를 다르게 한다고 해도, 백프레셔로 인한 메모리 부족현상이 발생할 수 있다. 이런 경우 버퍼링 정책을 설정해주는것이 좋다.

- Flow.buffer() : 버퍼를 추가해서 발행자가 수신 처리를 기다리지 않도록 한다.
- Flow.conflate() : 수신자의 처리가 다 끝나면, 그 이후의 값만 받는다. 그 사이의 값들은 다 버림
- Flow.collectLatest() : 수신자의 처리 와중에 새 값이 발행되면 기존 처리중이던 블록의 수행을 중단하고, 새로 처리를 시작한다.

## 플로우 결합하기

- zip : 여러 플로우를 결합하는데, 여러 플로우가 모두 같이 발행돼야 수신자에게 이벤트가 들어온다.
- combine : RxJava의 combineLatest와 같다.

## 플로우 플래트닝

- flatMapConcat : 데이터 발행 순서에 맞춰서 동기적으로 플래트닝한다.
- flatMapMerge : 발행되는 족족 비동기적으로 플래트닝한다.
- flatMapLatest : 다음 데이터가 발행되면 기존에 진행중이던 플래트닝은 캔슬한다.

## Flow 에러처리 : catch()

flow문 전체를 try-catch문으로 감싸면 발행자, 수신자에서 발생한 모든 예외를 처리할 수 있다. 하지만 이는 예외 투명성을 위반하므로 Flow에서는 `Flow.catch()` 연산자를 통해 예외처리할 것을 권장한다. catch()연산자는 업스트림의 예외만을 처리하는데 이걸 catch투명성이라 한다.

## Flow 완료처리 : onCompletion()

Flow문 전체를 try-catch-finally로 감싸 완료처리를 쉽게 할 수 있으나 에러처리와 마찬가지로 `onCompletion() 연산자`를 이용하는게 낫다. 예외가 발생했는지 여부를 람다의 리시버로 받을 수 있기 때문.

## onEach()

RxJava의 doOnNext와 같은 목적으로 사용할 수 있다.

만약 onEach() 이후에 collect()가 호출되면, flow가 속한 코드 블럭의 collect()이후의 코드는 모든 Flow의 발행이 끝나고, 각 onEach()가 모두 수행될 때 까지 일시정지 하게된다.
이럴 때 collect()대신 launchIn()을 사용하면 데이터 발행과 onEach가 현재의 스코프와는 다른 코루틴에서 실행되므로 같은 코드블럭 내의 다음 코드들이 곧바로 수행된다.

~~~kotlin
fun onEach() {
    fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }
    runBlocking<Unit> { //  이 코루틴이 취소되면 launchIn으로 실행한 플로우도 같이 취소된다.
        events()
            .onEach { event -> println("Event: $event") }
            .collect()
//            .launchIn(this) //  별도의 코루틴에서 플로우를 런칭한다.
        println("Done") //  collect()를 통해 플로우를 실행했으면 Done은 맨 마지막에 찍힌다.
    }
}
~~~
 
<br>

# Channel

## 일시중단 Iterator, Sequence

### Iterator
Kotlin에서 지원하는 코루틴을 활용해 값을 하나씩 생성하여 반환하는 컬렉션. Iterator.next()를 호출하여 값을 반환할 때 까지 suspend된다. 또한 hasNext()를 호출하면 Iterator는 다음 값을 생성하며 true를 반환하거나, 다음 값이 없으면 false를 반환한다.

~~~kotlin
val iterator = iterator {
        yield("안녕")
        yield("안녕2")
        yield("안녕3")
    }

println("${iterator.next()}")
println("${iterator.next()}")
println("${iterator.next()}")
~~~

### Sequence
Iterator와 다르게 index가 있어서 특정 위치의 데이터를 가져올 수 있다. 한번에 여러개 가져오는 것도 가능하다. 또한 상태를 저장하지 않고, 실행된 후에는 초기화 되는데, 조회할 때 마다 처음부터 다시 yield()한다는 말임. Iterator와 마찬가지로 일단 한번 값을 한번 생성하면 다음값을 요청받을 때 까지 suspend 된다.

~~~ kotlin
val f = sequence<Int> { //  sequence로 구현한 피보나치 수열
        yield(1)
        var current = 1
        var next = 1
        while (true) {
            yield(next)
            var tmp = next + current
            current = next
            next = tmp
        }
    }

val indexed = f.take(50).withIndex()

for ((i, value) in indexed) {
    println("$i : $value")
}
~~~

## Channel

동시성 코드(코루틴)간에 안전하게 통신을 할 수 있도록 해주는 일종의 파이프라인. 여러 코루틴이 공동 작업을 해야할 때 사용할 수 있다.

★ 새로운 코루틴은 부모의 CoroutineContext를 상속한다. CoroutineContext는 그 자체로 Element를 가지고 있고, Job, CoroutineDispatcher등의 Key를 통해 참조할 수 있음.
채널을 통해 데이터를 send(), receive()한다. (또는 trySend, tryReceive). 이 때 send(), receive()는 suspend 함수이다. send()가 먼저 호출되면 receive()가 호출될 때 일시정지, receive()가 먼저 호출되면 send()될 때 까지 일시정지한다. 때문에 두 함수가 같은 코루틴 스코프에서 호출되면 둘중 하나가 호출되는 순간 코루틴이 정지하기 때문에 영원히 블럭이 끝나지 않는다.

### 채널 버퍼링

채널에 버퍼사이즈를 설정할 수 있다. 이 버퍼에 여유가 있는한 send를 호출하더라도 코루틴을 일시정지시키지 않는다. 오버플로우시의 정책은 아래처럼 설정할 수 있다.

Channel(10) : 버퍼사이즈 10 설정(ArrayChannel), 디폴트 0(=Channel.RENDEZVOUS)
- Channel.RENDEZVOUS : 즉시 일시정지(RendezvousChannel)
- UNLIMITED : 무제한(LinkedListChannel)
- CONFLATED : 오래된 값이 지워짐, 즉 송신자는 일시중단 되지 않고 계속해서 값을 갱신하게 된다.(ConflatedChannel)
- BUFFERED : 버퍼사이즈가 64

### 오버플로우 정책
- BufferOverflow.SUSPEND - 잠이 들었다 깨어납니다.
- DROP_OLDEST - 예전 데이터를 지웁니다.
- DROP_LATEST - 새 데이터를 지웁니다.

★ receive()를 여러번 반복호출하는 대신 for-in문을 통해 끝날때 까지 알아서 receive()하도록 만들 수 있다. close()가 호출되면 채널이 닫히고 for-in문은 종료된다. for-in문은 무한 반복이므로 이 케이스에서 close()는 꼭 호출해 줘야 한다.

### SendChannel
send전용 채널

- isClosedForSend : 데이터 보내기전에 닫혔는지 확인
- isFull : 데이터 보내기전에 버퍼가 꽉 찼는지 확인
- send() : 데이터 전송. 이미 채널이 닫힌 경우 ClosedChannelException 발생.
- offer() : 코루틴을 일시중단 하지 않으면서 데이터를 전송한다.

### ReceiveChannel
receive전용 채널

- isClosedForReceive : 데이터 수신전에 닫혔는지 확인.
- isEmpty : 수신할 것이 있는지 확인.
- receive() : 데이터 받기. 이미 채널이 닫힌 경우. ClosedReceiveChannelException 발생.

### Producer
일시중단 Iterator와 Sequence는 값 생성도중 일시중단 할 수 없다는 단점이 있다. 이 것을 가능케 하는 게 Producer. 특정 CoroutineContext에서 값을 생성한다. 값 생성이 언제든 일시중단 될 수 있으므로 값 수신도 일시중단 연산에서 해야 한다. 내부적으로 Channel을 사용함.

★ produce {...} : 채널을 직접 구현하지 않고 producer-consumer 패턴을 통해 간접적으로 각 코루틴에서 send와 receive하게 해 주는 확장함수. ProducerCoroutine을 만들어 데이터를 제공한다.

★ ProducerCoroutine은 ProducerScope를 상속받고, ProducerScope는 CoroutineScope와 SendChannel(send만 할 수 있는 채널)을 상속받는다.

★ consumeEach {...} : 채널에서 반복적으로 데이터를 받는다.

~~~kotlin
private val producer = GlobalScope.produce<Int>(Dispatchers.Default) {
    send(1)
    var current = 1
    var next = 1
    while (true) {
        send(next)
        var tmp = next + current
        current = next
        next = tmp
    }
}

fun coroutineTest() {
    runBlocking {
        println("runBlocking 시작")
        
        producer.consumeEach {
            println(it)
        }
        
        println("runBlocking 끝")
    }
}
~~~

### Pan-in, Pan-out

#### Pan-in
채널 소비자 코루틴수 > 생산자 코루틴수, 채널을 동시에 구독한다. 생산자의 send를 통해 생산된 데이터를 소비자들이 돌아가면서 하나씩 받는다.

#### Pan-out
채널 소비자 코루틴수 < 생산자 코루틴수. 소비자 코루틴은 각 생산자 코루틴에게 공정하게 생산의 기회를 준다.

### select {...}
블럭 내에서 먼저 끝난거만(먼저 호출된 콜백만) 실행시킨다. 각 타입의 객체에 대해 다음 콜백을 사용할 수 있다.
- Channel - onJoin()
- Deferred - onAwait()
- SendChannel - onSend()
- ReceiveChannel - onReceive(), onReceiveCatching()
- delay - onTimeout()

<br>

# 공유 객체 문제
여러 스레드에서 코루틴들이 동시에 수행될 때, 같은 자원에 동시 접근, 수정 문제가 존재한다. 이 문제는 데이터에의 동시접근을 막음으로써 해결해야 하는데, 원자성 위반 문제를 해결하는 방법은 상황에 따라 달라질 수 있다.

<br>

## 스레드 한정
공유 데이터에 접근하는 스레드를 하나만 사용하는 것. 모든 코루틴을 단일 스레드환경에서 작동시킨다. 

<br>

## Actor
스레드 한정의 개념과 채널을 사용해 Actor를 만들 수 있다. Actor의 단일스레드가 독점적으로 자료를 가지고, 다른 스레드와 공유하지 않는다. 다른 스레드들은 channel을 통해 actor에게 수정을 요청한다. 동시에 수정요청을 받더라도 Actor의 데이터 수정은 원자적이므로 안전하다.

~~~kotlin
//  액터를 수행할 수 있는 유일한 thread다.
private val context = newSingleThreadContext("counter")
private var counter = 0
private val notifications = Channel<Int>(Channel.CONFLATED) //  counter 갱신시전송할 채널

private val actor = GlobalScope.actor<Action>(context) {
    for (msg in channel) {
        when (msg) {
           Action.INC -> counter++
           Action.RESET -> counter = 0
        }
        notifications.send(counter)
    }
}

suspend fun increment() = actor.send(Action.INC)

suspend fun reset() = actor.send(Action.RESET)

fun getNotificationChannel(): ReceiveChannel<Int> = notifications

enum class Action {
    INC, RESET
}
~~~

<br>

## Mutex(Mutual Exclusion, 상호 배제)
하나의 코드블록(임계영역)이 여러 코루틴에서 실행되는 상황에서, 한 번에 한 코루틴만 해당 코드블록을 실행하도록 동기화 하는 것을 말한다. 자바의 synchronized 키워드와 원리가 같다. 블록되지 않는다. 한번에 하나의 코루틴만 lock을 보유하고, 나머지 잠금을 시도하는 코루틴은 일시중단 상태가 된다.

Mutex.withLock {...} 블록을 통해 임계영역을 구현한다. 잠금에 대한 상세 제어가 필요할 땐 lock(), unlock(), isLocked, tryLock()을 사용해도 된다. 여기서 lock()만 suspend 함수이다.

~~~kotlin
suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100 // 시작할 코루틴의 갯수
    val k = 1000 // 코루틴 내에서 반복할 횟수
    val elapsed = measureTimeMillis {
        coroutineScope { // scope for coroutines
            repeat(n) {
                launch {
                    repeat(k) { action() }
                }
            }
        }
    }
    println("$elapsed ms동안 ${n * k}개의 액션을 수행했습니다.")
}

val mutex = Mutex()
var counter = 0

runBlocking {
    withContext(Dispatchers.Default) {
        massiveRun {
            mutex.withLock {
                counter++
            }
        }
    }
    println("Counter = $counter")
}
~~~

<br>

## Volatile 변수
volatile 변수는 동시 수정문제는 해결할 수 없다. 다만 가시성 문제는 해결할 수 있어서 스레드간 동기화된 데이터 공유필요시 사용할 수 간편하게 사용할 수 있다.

JVM에서 각 스레드는 메모리에 저장된 실제 데이터의 사본을 캐싱하여 가지고있는데, 1번 스레드에서 실제 데이터를 변경해봐야 2번 스레드에서 캐시갱신이 안되면 결국 2번 스레드는 실제 데이터를 볼 수 없게 된다(가시성 문제). @Volatile이 붙은 변수는 즉시 변경사항에 대한 가시성을 확보할 수 있게 해준다. 반면에 동시에 같은 값을 읽어 각자 수정이 여전히 가능하기 때문에 원자성 문제에 대한 해결책이 아니다.

JVM의 기능이기 때문에 다른 플랫폼에서는 사용할 수 없다.

변수가 현재값과 상관없이 변경되거나, volatile변수와 의존관계인 다른 변수가 없을 때 유용하게 사용할 수 있다.

<br>

## 원자적(Thread-safe한) 데이터 구조
AtomicInteger과 같은 클래스들이 있다.

<br>

# 테스트
동시성 코드를 테스트할 때는 각 코드의 처리속도에 대한 가정을 버리고 모든 경우의 수를 테스트해야 하며, 각 단위 테스트 이상의 전체적인 기능테스트가 꼭 필요하다.

<br>

# Coroutine의 원리

## State Machine
suspend 함수를 자바 바이트 코드로 디컴파일 해 보면 Continuation이라는 파라미터 하나가 추가되어 있음을 확인할 수 있다. Continuation은 interface인데 CoroutineContext객체 하나, resumeWith이라는 함수 하나로 구성되어 있다.
~~~kotlin
public interface Continuation<in T> {
    public val context: CoroutineContext
    public fun resumeWith(result: Result<T>)
}
~~~
suspend 함수가 호출되면 함수는 State Machine으로 변환된다. 실행 state를 Continuation객체가 관리 하며 state를 기반으로 함수의 어떤 부분을 실행할지를 결정하고, 실행시킨다.

Continuation객체는 suspend 함수의 시작점과 내부의 또 다른 suspend 함수 호출부에 0, 1, 2, ... 순서대로 label을 붙이고, 0부터 차례대로 실행한다. 

Continuation객체를 ContinuationImpl 익명 클래스로 구체화 하여 각 label에 해당하는 suspend 함수의 결과값을 저장하고, 다음 단계의 label로 넘어갈 때 넘긴다.

각 label의 내부 suspend 함수는 동작이 끝나면 인자로 전달받은 ContinuationImpl의 내부 콜백 함수인 invokeSuspend()를 호출하게 되는데, 이 때 외부의 suspend 함수를 다시 실행한다.

내부 suspend 함수는 결과가 반환되면 결과를 return하고, 일시정지 되면 COROUTINE_SUSPENDED를 반환한다. 상위 suspend 함수는 내부 suspend 함수의 리턴값이 COROUTINE_SUSPENDED이면 일시중지된다.

<br>

## Context 전환
코루틴은 CoroutineContext 설정에 따라 일시정지 후 재시작될 때 기존과는 다른 스레드에서 실행될 수 있다. CoroutineContext도 Continuation객체에 저장이 되어 state machine에 의해 유지된다.

### ContinuationInterceptor 인터페이스
CoroutineContext의 Element중 하나이다.
~~~kotlin
public interface ContinuationInterceptor : CoroutineContext.Element {
    public fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T>
}
~~~
Continuation객체를 받아 스레드 조절처리가 래핑된 다른 Continuation를 리턴하는 interceptContinuation()를 정의하기 위한 인터페이스이다.

### CoroutineDispatcher
ContinuationInterceptor를 구현한 추상 구현체이다.
~~~kotlin
public abstract class CoroutineDispatcher :
    AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
        public open fun isDispatchNeeded(context: CoroutineContext): Boolean = true
        public abstract fun dispatch(context: CoroutineContext, block: Runnable)
        public final override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
            DispatchedContinuation(this, continuation)
    }
~~~

인터페이스의 interceptContinuation()는 DispatchedContinuation을 반환하도록 오버라이드 되었다. DispatchedContinuation은 `CoroutineDispatcher`와 `Continuation`으로 구성되며 내부의 resume(), resumeWithException()에서 Dispatcher를 사용한다.

또한 dispatch()는 실제로 함수 실행 스레드를 바꾸는 역할을 하는 함수인데, 실행 내용을 Runnable객체(를 구현한 DispatchedTask 객체를 상속한 DispatchedContinuation객체)로 받는다. 각 Dispatcher(Default, IO, Main)는 CoroutineDispatcher를 상속하여 특성에 맞게 dispatch()를 실행시킨다.

DispatchedTask의 run()에서 DispatchedContinuation(상속관계이므로 결국 자기자신)의 resume과 resumeWithException을 트리거한다.

<br>

## 예외 처리
코루틴 내부에서 예외가 발생할 때 마다 CEH의 handleCoroutineException()함수가 실행된다. 이 함수는 CoroutineContext에서 CEH를 찾아서 있으면 handleException()을 호출하고, 없으면 예상 선언 함수 handleCoroutineExceptionImpl()를 호출하여 플랫폼별 로직을 실행시킨다. JVM 플랫폼에서는 현재 스레드로 예외를 전파한다.