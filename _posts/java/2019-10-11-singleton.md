---
title: "[Java] MultiThread환경에서의 SingleTon사용"
categories:
    - Java
---
★일반적으로 SingleTon객체는 프로그램 실행과정에서 단 한번만 생성하여 계속 사용하기 위해 구현한다. 이러한 싱글톤 객체는 다음과 같이 단순하게 구현할 수 있다.

public class SingleTon {
    private static SingleTon instance;

    private SingleTon() {}

    public static SingleTon getInstance() {
        if (instance == null) instance = new SingleTon();
        return instance;
    }
}

★위 코드는 싱글스레드 환경에서는 거침없이 동작한다. 하지만 멀티스레스 환경에서 여러 스레드가 동시에 getInstance()를 호출한다면 instance가 여럿 생성될 위험이 있다. 싱글톤의 개념자체가 무너지게 되므로 이를 개선하기 위한 여러 시도들이 있었다.

★synchronized : 가장 단순하게는 synchronized키워드를 사용하는 것이다. 이 키워드는 여러 스레드가 이 메소드에 동시에 접근할 수 없도록 하는 것이다.

public class SingleTon {
    private static SingleTon instance;

    private SingleTon() {}

    public static synchronized SingleTon getInstance() {
        if (instance == null) instance = new SingleTon();
        return instance;
    }
}

★이 방법은 thread safe하지만 getInstance()가 호출될 때마다 메소드 영역을 lock, unlock하는 과정을 거치게 되고, 이는 많은 비용을 발생시킨다.

★Double Checked Locking : 위 방식을 개선시키기 위해 일단 한번 instance를 체크한 후 instance가 현재 null인 상태라면 동기화 하는 방식이다.

class SingleTon {
    private static SingleTon instance;

    private SingleTon() {}

    public static SingleTon getInstance() {
        if (instance == null) {
        	synchronized (SingleTon.class) {
				if (instance == null) {
					instance = new SingleTon();
				}
			}
        };
        return instance;
    }
}

★getInstance()에서 synchronized키워드를 제거하고, 일단 한번 instance가 생성되면 클래스 영역을 동기화하지 않기 때문에 결론적으로 오버헤드가 줄어들게 된다. 하지만 이 방법도 완전하지가 않다. 1번 스레드가 instance생성을 완료하기 전에 메모리 공간을 할당한다면 2번 스레드는 instance가 null이 아닌 것으로 판단하여 사용하는 경우 문제가 생길 수 있다.

★Enum : 싱글톤을 class가 아닌 Enum으로 정의하는 것이다. 런타임에 인스턴스가 생성되지 않음을 보장하는 Enum의 특성을 이용하는 방법으로 아래처럼 사용할 수 있다.

public class Main {

	public static void main(String[] args) {
		SingleTon.INSTANCE.printA();
		SingleTon.INSTANCE.printB();
	}
}

enum SingleTon {
	INSTANCE;
    public void printA() {
    	System.out.println(INSTANCE + "A");
    }
    public void printB() {
    	System.out.println(INSTANCE + "B");
    }
}

★이 방법은 클래스 로딩시점에서 인스턴스의 초기화를 진행한다. 로딩시점에서 초기화를 하므로 여러 thread가 끼어들 일이 없다. 또한 직렬화가 자동으로 지원된다는 점에서 좋은 평가를 받고 있는 방법으로 보인다.

★LazyHolder : 현재까지 가장 완벽하다고 평가 받고 있는 방법이다. 클래스를 처음 사용하는 시점으로 초기화를 미루는 것이다(Lazy Initialization).

class SingleTon {
	private SingleTon() {}

	public static SingleTon getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	private static class LazyHolder {
		private static final SingleTon INSTANCE = new SingleTon();
	}
}

★SingleTon 클래스는 LazyHolder클래스의 변수를 멤버로 갖고있지 않기 때문에 SingleTon클래스 로딩시 LazyHolder클래스는 초기화 되지 않는다. LazyHolder클래스는 SingleTon.getInstance()가 호출 될 때 로딩되며 초기화가 진행된다. Class를 로딩하는 시점은 thread safe하기 때문에 간단하게 SingleTon객체를 사용할 수 있다.