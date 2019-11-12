---
title: "[Java] Gof 디자인 패턴 - 싱글톤(Singleton)"
categories:
    - Java
---
★싱글톤 패턴 : 객체를 메모리에 단 하나만 올리도록 할 때 사용된다. 

★그 누가 됐건 덕현아 하고 부르면 내가 대답하는거랑 같이 모든 클래스에서 이 하나의 인스턴스를 공유하는 모양이 된다.

★Dao처럼 굳이 여러개를 사용할 필요가 없거나 환경설정처럼 공유하지않으면 문제가 되는 경우에 유용하게 사용할 수 있다.

★예를들어 스마트폰에 스피커 기능을 탑재했고, 볼륨을 늘렸다 줄였다 하는 것을 동적으로 구현하고 싶다. 이 때 스피커와 연결된 클래스는 모든 클래스에서 단 하나만 사용해야 한다. 간단하게 만들 수 있을 것 같지만 사실 여기에는 숨은 고뇌가 있다.

★일반적으로 SingleTon객체는 프로그램 실행과정에서 단 한번만 생성하여 계속 사용하기 위해 구현한다. 이러한 싱글톤 객체는 다음과 같이 단순하게 구현할 수 있다.
{% highlight java %}
public class SystemSpeaker {
    private static SystemSpeaker instance;
	private int volume;

    private SystemSpeaker() {}

    public static SystemSpeaker getInstance() {
        if (instance == null) instance = new SystemSpeaker();
        return instance;
    }
}
{% endhighlight %}

★위 코드는 싱글스레드 환경(단 하나의 스레드만 사용하는 환경)에서는 거침없이 동작한다. 하지만 멀티스레드 환경에서 여러 스레드가 동시에 getInstance()로 진입해 instance가 null이라고 판단한다면 instance가 여럿 생성될 위험이 있다. 싱글톤의 개념자체가 무너지게 되므로 이를 개선하기 위한 여러 시도들이 있었다.

★synchronized : 가장 단순하게는 synchronized키워드를 사용하는 것이다. 이 키워드는 여러 스레드가 이 메소드에 동시에 접근할 수 없도록 하는 것이다.
{% highlight java %}
public class SystemSpeaker {
    private static SystemSpeaker instance;
	private int volume;

    private SystemSpeaker() {}

    public static synchronized SystemSpeaker getInstance() {
        if (instance == null) instance = new SystemSpeaker();
        return instance;
    }
}
{% endhighlight %}

★이 방법은 thread safe하지만 getInstance()가 호출될 때마다 메소드 영역을 lock(먼저 메소드로 진입한 스레드가 문을 잠그는 것), unlock하는 과정을 거치게 되고, 이는 많은 비용을 발생시킨다.

★Double Checked Locking : 위 방식을 개선시키기 위해 일단 한번 instance를 체크한 후 instance가 현재 null인 상태라면 동기화 하여 다시 null을 체크하는 방식이다.
{% highlight java %}
class SystemSpeaker {
    private static SystemSpeaker instance;
	private int volume;

    private SystemSpeaker() {}

    public static SystemSpeaker getInstance() {
        if (instance == null) {
        	synchronized (SystemSpeaker.class) {
				if (instance == null) {
					instance = new SystemSpeaker();
				}
			}
        };
        return instance;
    }
}
{% endhighlight %}

★getInstance()에서 synchronized키워드를 제거하고, 일단 한번 instance가 생성되면 클래스 영역을 동기화하지 않기 때문에 결론적으로 오버헤드가 줄어들게 된다. 하지만 이 방법도 완전하지가 않다. 1번 스레드가 instance생성을 완료하진 않았고 메모리 공간을 할당하는 단계까지만 갔는데 2번 스레드는 instance가 null이 아닌 것으로 판단하여 사용하는 경우 문제가 생길 수 있다. 듣기로는 자주 일어나는 일이 아니라고 하지만 0.1%의 확률도 내가 걸리면 100%이므로...

★Enum : 싱글톤을 class가 아닌 Enum으로 정의하는 것이다. 런타임에 인스턴스가 생성되지 않음을 보장하는 Enum의 특성을 이용하는 방법으로 아래처럼 사용할 수 있다.
{% highlight java %}
public class Main {

	public static void main(String[] args) {
		SystemSpeaker.INSTANCE.printA();
		SystemSpeaker.INSTANCE.printB();
	}
}
{% endhighlight %}

{% highlight java %}
public enum SystemSpeaker {
	INSTANCE;
	
	private int volumn;

	public int getVolumn() {
		return volumn;
	}

	public void setVolumn(int volumn) {
		this.volumn = volumn;
	}
}
{% endhighlight %}

★이 방법은 클래스 로딩시점에서 인스턴스의 초기화를 진행한다. 로딩시점에서 초기화를 하므로 여러 thread가 끼어들 일이 없다. 또한 직렬화가 자동으로 지원된다는 점에서 좋은 평가를 받고 있는 방법으로 보인다.

★LazyHolder : 현재까지 가장 완벽하다고 평가 받고 있는 방법이다. 클래스를 처음 사용하는 시점으로 초기화를 미루는 것이다(Lazy Initialization).
{% highlight java %}
class SystemSpeaker {
	private SystemSpeaker() {}

	public static SystemSpeaker getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	private static class LazyHolder {
		private static final SystemSpeaker INSTANCE = new SystemSpeaker();
	}
}
{% endhighlight %}

★SingleTon 클래스는 LazyHolder클래스의 변수를 멤버로 갖고있지 않기 때문에 SingleTon클래스 로딩시 LazyHolder클래스는 초기화 되지 않는다. LazyHolder클래스는 SingleTon.getInstance()가 호출 될 때 로딩되며 초기화가 진행된다. Class를 로딩하는 시점은 thread safe하기 때문에 간단하게 SingleTon객체를 사용할 수 있다.

★Test
1. 싱글톤 객체를 LazyHandler방법에 따라 만든다.
{% highlight java %}
public class SystemSpeaker {
	private int volume;
	
	private SystemSpeaker() {
		volume = 5;
	}
	
	public static SystemSpeaker getInstance() {
		return LazyHolder.INSTANCE;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	private static class LazyHolder {
		private static final SystemSpeaker INSTANCE = new SystemSpeaker();
	}
}
{% endhighlight %}

2. 사용해 본다. 아래 코드의 getVolumn()은 모두 같은 인스턴스의 메소드를 호출한 것과 같다.
{% highlight java %}
public static void main(String[] args) {
		SystemSpeaker speaker1 = SystemSpeaker.getInstance();
		SystemSpeaker speaker2 = SystemSpeaker.getInstance();
		
		//	5,5
		System.out.println(speaker1.getVolume());
		System.out.println(speaker2.getVolume());
		
		speaker1.setVolume(11);
		//	11,11
		System.out.println(speaker1.getVolume());
		System.out.println(speaker2.getVolume());
		
		speaker2.setVolume(22);
		//	22,22
		System.out.println(speaker1.getVolume());
		System.out.println(speaker2.getVolume());
}
{% endhighlight %}