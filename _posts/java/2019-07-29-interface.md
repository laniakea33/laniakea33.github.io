---
title: "[Java] Interface"
categories:
    - Java
---
★추상메소드만을 가진 클래스를 인터페이스라고 한다.

★class키워드 대신 interface키워드로 정의하고, 추상클래스와 마찬가지로 인스턴스를 생성할 수 없다.

★클래스로 구현할 때는 implements키워드를 사용하며 추상클래스와는 달리 다중상속이 가능하다.

★Java 8버전에서는 default로 함수를 정의할 수 있으나 interface의 기본적인 기능은 아니다.

{% highlight java %}
public interface onClickListener {
    public void onClick(Button b);
}
{% endhighlight %}