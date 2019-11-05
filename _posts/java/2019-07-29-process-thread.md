---
title: "[Java] Process, Thread"
categories:
    - Java
---
★Process : 메모리에 적재되어 CPU가 처리하고 있는 하나의 프로그램(현재 동작중인 프로그램), 여러개가 한번에 동작하면 멀티프로세스라고 한다.
★Thread : 하나의 프로세스 내에 존재하는 독립된 작업단위, main메소드가 시작하면 기본적으로 하나의 스레드로 동작하며 여러개의 스레드가 동시에 동작할 때는 사실 동시에 동작하는 게 아니라 모노태스킹의 전환을 엄청나게 빠르게 해서 동시에 진행되는 것 처럼 보이는 것이다. 참고로 가비지 컬렉터도 하나의 스레드로 실행됨.

★Thread 생성방법
1. Thread클래스를 상속받아 run()을 오버라이드, start()메소드로 가동
2. Runnable인터페이스를 구현하여 run()을 정의하고, new Thread로 빈 스레드 객체를 만들어 해당 Runnable인터페이스를 탑재한 후 start()메소드로 실행, 상속은 하나밖에 못받는 것이 규칙이므로 보통 인터페이스를 구현하는 방법을 사용한다.

★데몬 스레드 : 스레드 객체의 setDaemon(true)메소드를 호출하면 이 스레드를 실행시킨 스레드가 종료될때 함께 종료된다. 이런 스레드를 데몬스레드라고 함.

★스레드 동기화 : 여러 스레드에서 같은 변수에 동시에 접근하는 경우 문제가 발생한다.
synchronized 키워드로 블럭을 지정해 주면 해당 블럭에는 한 스레드만 들어갈 수 있다. 
코드 블럭 방법과 함수synchronized 두가지 방법이 있다.
{% highlight java %}
//	코드 블럭 방법
public void doSomething() {
	synchronized(this) {
		...
	}
}

//	함수 synchronized방법
public synchronized void doSomething() {
	...
}
{% endhighlight %}

★스레드 함수들
1. join() : 서브스레드 실행시 종료할때 까지 메인스레드를 정지시킴
2. interrupt() : 한쪽 스레드 종료
3. wait() : 대기
4. sleep() : 재움
5. notify() : 깨움
6. yield() : 동기화 블럭에 한 서브스레드가 실행 종료시 나머지 서브스레드가 계속 대기하는 상황에서 대기하는 서브스레드를 진입시켜줌

★wait()와 notify()함수는 항상 syncronized영역 안에 있어야 한다.