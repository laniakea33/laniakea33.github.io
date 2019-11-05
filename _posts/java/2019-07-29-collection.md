---
title: "[Java] Collection"
categories:
    - Java
---
★Collection 프레임워크 : 무한한 데이터들을 저장하기 위해 사용하는 정형화된 형식 클래스들의 모음, 다형성을 활용하면서 다양한 자료구조를 사용할 수 있도록 자바가 만들어 놓음

★Collection interface를 상속한 interface들
1. Set : 비순차적, 중복데이터 허용안함
2. Map : 비순차적, 중복데이터 허용함
3. List : 순차적, 중복데이터 허용함

★HashSet<br>
HashSet은 Set인터페이스를 구현한 클래스임, <>안에는 Set에 넣을 객체의 클래스 타입을 정해준다.
클래스타입을 정해주지 않으면 여러가지 클래스 객체가 무작위로 들어갈수 있으므로 문제(ClassCastException)가 발생할 수 있다.
이런 방법은 모든 Collection객체에 사용하며, 이렇게 자료형을 <>안에 클래스 타입을 정해주는 방법을 제네릭(Generic)이라고 함

★AutoBoxing<br>
Set인터페이스를 사용할 때 <>안에 사용할 클래스 타입을 지정해줘야 하는데 기본자료형은 클래스가 아니라서 클래스 타입으로 지정할 수가 없다. 그래서 이런 기본자료형을 포장한 클래스들(Integer, Long 등등)이 존재하는데 이런 방식을 AutoBoxing이라고 한다.

★Iterator<br>
Set인터페이스는 순서가 없으므로 사용할 때 순서를 정해서 사용할 수 있게 Iterator객체를 만들어 가져옴. Iterator는 처음에 -1번째 인덱스를 가리키고 있으며 hasNext()함수는 다음 자료가 존재하면 true, 현재 마지막 자료의 인덱스를 가리키고 있으면 false를 환함. next()는 다음자료의 값을 리턴한다. iterator대신 foreach문으로도 사용 가능하다. foreach문은 내부에서 자동적으로 Iterator를 사용하기 때문.
{% highlight java %}
Iterator<Object> it = hs.iterator();
while(it.hasNext()) {
	System.out.println(it.next());
}
{% endhighlight %}

★Set은 기본적으로 중복데이터를 허용하지 않으나 HashSet은 객체의 HashCode값으로 복수의 객체를 비교한다. 그래서 만약 객체의 멤버변수의 값이 모두 같아서 우리가 보기엔 완전히 같은 객체일지라도 이 HashCode값이 다른 경우에는 서로 다른 객체로 취급해 중복저장되는 일이 발생한다. 이런경우 Object클래스의 hashCode를 override해서 멤버변수만을 사용해 HashCode를 계산하도록 해주면 Set인터페이스가 같은객체로 취급해 중복저장이 발생하지 않는다. 만약 hashCode가 같은 객체라면 equals()까지 호출해서 같은지 다른지를 한번 더 판단한다. 그러니까 같은 객체가 되려면 hashCode와 equals()에 정의된 조건까지 만족시켜야 한다. equals와 hashCode는 항상 함께 변경해야 한다는 그런 말.

★List : 배열과 같이 값을 나열한 자료구조이다. 배열과는 다르게 처음에 크기를 정해주지 않아도 된다. ArrayList, vector, LinkedList 등이 있다.

★Map : Key-Value쌍을 저장한다. 여기서 Value는 중복될수 있으나 Key는 중복될 수 없고, 이 Key를 사용해 매핑된 Value를 가져오는 식으로 사용한다.

★이외에도 Stack이나 Deque같은 Collection들이 있으나 잘 안쓰는듯 하다.