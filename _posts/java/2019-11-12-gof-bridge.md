---
title: "[Java] Gof 디자인 패턴 - 브릿지(Bridge)"
categories:
    - Java
---
★브릿지 패턴 : 기능부분과 구현부분을 분리한다.

★기능을 사용하는 클래스과, 그 기능을 직접 구현하는 클래스을 분리하여 둘 사이의 결합도를 낮추는 패턴이다. 기능 클래스가 그 기능을 구현한 클래스를 멤버로 갖고있으면서 그 기능을 위임하며 이 때 기능의 구현 방식이 추가되거나 변경되면 기능을 사용하는 클래스는 그냥 두고 구현 클래스변경하면 된다. 반대로 구현의 내용과는 상관없이 기능 클래스의 내용을 바꿀수도 있다. 즉 기능 부분과 구현 부분이 독립적인 확장이 가능해 진다.

★구현부분이 확장의 소지가 다분한 경우에 사용한다.

★예를 들어 어떤 문장을 선택하면 한국어, 영어로 원하는 횟수만큼 읽어주는 앱이 있다고 해보자. 여기서 읽어주는 부분이 구현부분, 원하는 횟수만큼 그 구현을 실행하는 부분을 기능 부분이라고 할 수 있다.

★Test
1. 먼저 구현부분을 만든다. 1을 입력받으면 '안녕하세요', 2는 '배고파요', 3은 '잠이와요'를 출력하는 데, 한국어를 출력하는 클래스와 영어를 출력하는 클래스를 같은 인터페이스를 상속하도록 구현한다. 
{% highlight java %}
public interface ISpeaker {
	void speak(int num);
	void end();
}
{% endhighlight %}

{% highlight java %}
public class KoreanSpeaker implements ISpeaker {

	@Override
	public void speak(int num) {
		switch(num) {
		case 1:
			System.out.println("안녕하세요");
			break;
		case 2:
			System.out.println("배고파요");
			break;
		case 3:
			System.out.println("잠이와요");
			break;
			default:
				break;
		}
	}

	@Override
	public void end() {
		System.out.println("끝");
	}
}
{% endhighlight %}

{% highlight java %}
public class EnglishSpeaker implements ISpeaker {

	@Override
	public void speak(int num) {
		switch(num) {
		case 1:
			System.out.println("Hello");
			break;
		case 2:
			System.out.println("Hungry");
			break;
		case 3:
			System.out.println("Sleepy");
			break;
			default:
				break;
		}
	}

	@Override
	public void end() {
		System.out.println("End");
	}
}
{% endhighlight %}

2. 이제 위 클래스들의 최상위 인터페이스인 ISpeaker를 멤버로 가진 기능 클래스 Repeater를 만든다. 최상위 기능 클래스가 가진 이 구현 클래스의 인터페이스가 기능 부분과 구현 부분을 잇는 다리(Bridge)역할을 하는 것이다. 기능 클래스는 요청받은 기능을 직접 구현하지 않고 ISpeaker로 위임(Delegate)하도록 한다. Repeater2와 Repeater3은 Repeater를 상속하여 각각 두번, 세번 출력하고 end()를 호출하는 클래스이다. 되게 의미없는 방법이지만 패턴에 어떻게든 끼워맞추려고 기능을 다양화 한 것이다.
{% highlight java %}
public class Repeater {

	protected ISpeaker speaker;

	public Repeater(ISpeaker speaker) {
		this.speaker = speaker;
	}
	
	public void speak(int num) {
		speaker.speak(num);
	}
	
	public void end() {
		speaker.end();
	}
	
	public void repeat(int num) {
		speak(num);
		end();
	};
}
{% endhighlight %}

{% highlight java %}
public class Repeater2 extends Repeater {

	public Repeater2(ISpeaker speaker) {
		super(speaker);
	}

	@Override
	public void repeat(int num) {
		speak(num);
		speak(num);
		end();
	}
}
{% endhighlight %}

{% highlight java %}
public class Repeater3 extends Repeater {

	public Repeater3(ISpeaker speaker) {
		super(speaker);
	}

	@Override
	public void repeat(int num) {
		speak(num);
		speak(num);
		speak(num);
		end();
	}
}
{% endhighlight %}

3. 직접 사용한다.
{% highlight java %}
public static void main(String[] args) {
	Repeater repeater = new Repeater2(new KoreanSpeaker());
	repeater.repeat(3);
}
{% endhighlight %}

★어댑터 패턴과 굉장히 비슷하다는데... 정확히는 모르겠지만 기능 클래스가 받은 요청을 구현 클래스에 위임한다는 점에서 비슷하다고 하는 것 같다. 어댑터 패턴도 클라이언트가 어댑터에 요청하면 어댑터가 어댑티가 알아먹을 수 있도록 요청을 바꿔서 위임 요청한다. 브릿지 패턴도 클라이언트가 기능 클래스에 요청하면 기능 클래스가 구현 클래스에 요청을 위임하도록 되어있다.