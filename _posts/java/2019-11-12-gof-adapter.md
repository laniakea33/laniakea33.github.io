---
title: "[Java] Gof 디자인 패턴 - 어댑터(Adapter)"
categories:
    - Java
---
★어댑터 패턴 : 호환되지 않는 두 인터페이스를 연결한다. 

★어떤 것을 입력하는 쪽과 입력받는 쪽이 서로 호환이 안될 때, 가운데서 이를 이어주는 것을 어댑터라고 하고, 어댑터에게 변환된 결과를 받는 쪽을 어댑티(Adaptee)라고 한다. 즉 클라이언트가 요청 - 어댑터가 변환 - 어댑티에게 도달 순서로 입력이 전달된다.

★어댑터는 어댑티와 같은 인터페이스를 공유하게 되고, 클라이언트는 자신과 어댑티 사이에 어댑터가 있는지 알지 못해도 된다.

★어댑터를 구현하는 방법은 `클래스 어댑터`와 `객체 어댑터` 두 종류가 있다. 클래스 어댑터는 타겟(변환의 대상)과 어댑티를 다중상속하나, 자바에서 다중상속은 불가능하기 때문에 타겟만을 상속하는 `객체 어댑터 패턴`을 사용한다.

★예를 들어 영어만을 사용하는 인공지능 대화 봇에 한국어를 적용하는 신형 코드를 적용시켜 보자.
1. Target : 한국어 입력
2. Adapter : ITranslater(한국어 입력을 영어입력으로 변환)
3. Adaptee : EnglishBot(영어 봇)

★이런 형식으로 EnglishBot을 KoreanBot에 연결해서 사용할 수 있다

★Test
1. 기존에 존재하는 코드인 Adaptee
{% highlight java %}
public class EnglishBot {
	public static String talk(String msg) {
		if (msg.equals("hello")) {
			return "Nice to meet you";
		} else if (msg.equals("I'm hungry")) {
			return "I don't care";
		} else {
			throw new RuntimeException("Not implemented");
		}
	}
}
{% endhighlight %}

2. 변환의 대상이 되는 입력을 입력하는 코드
{% highlight java %}
public static void main(String[] args) {
	ITranslater translater = new Translater();
	System.out.println(translater.talk("안녕"));
	System.out.println(translater.talk("배고파"));
}
{% endhighlight %}

3. 입력과 어댑티를 잇는 Adapter
{% highlight java %}
public class Translater implements ITranslater{

	@Override
	public String talk(String str) {
		if (str.equals("안녕")) {
			str = "hello";
		} else if (str.equals("배고파")) {
			str = "I'm hungry";
		}
		
		String result;
		if ((result = EnglishBot.talk(str)).equals("Nice to meet you")) {
			return "반가워";
		} else if (result.equals("I don't care")) {
			return "어쩌라고";
		} else {
			return result;
		}
	}
}
{% endhighlight %}

★새로운 메시지나 언어를 더 추가하더라도 기존의 어댑티와 main함수에는 어떤 수정도 없이, 어댑터만 수정하도록 되었다. main함수에서의 입력과 어댑티가 호환되지 않음에도 어댑터가 이를 연결해 주기 때문이다.