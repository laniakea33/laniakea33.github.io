---
title: "[Java] Gof 디자인 패턴 - 빌더(Builder)"
categories:
    - Java
---
★빌더 패턴 : 수많은 변수를 가진 또는 생성이 복잡한 인스턴스의 생성을 간략하게 만든다.

★객체를 만들 때 객체가 가져야 하는 수많은 변수를 기본값으로 채워넣고, 꼭 필요한 부분만을 직접 설정할 수 있도록 한다.

★매번 new 키워드로 인스턴스를 생성하는 것이 까다로운 경우에 사용한다.

★예를들어 메이플 스토리 캐릭터를 만든다고 하자. 기본적으로 남자, 까까머리, 검, 반팔티, 청반바지, 빨간장화로 세팅되어 있다고 할때, 무기와 신발만 몽둥이와 노란장화로 바꿔서 생성하고 싶다. 이 때 캐릭터의 무기만을 직접 설정하고, 나머지는 기본값으로 생성하면 모든 속성들을 일일이 직접 설정하지 않고도 쉽게 만들 수 있다.

★Test
1. 생성할 객체를 정의한다.
{% highlight java %}
public class Charactor {
	private String gender;
	private String hair;
	private String weapon;
	private String top;
	private String bottoms;
	private String shoes;
	
	public Charactor(String gender, String hair, String weapon, String top, String bottoms, String shoes) {
		this.gender = gender;
		this.hair = hair;
		this.weapon = weapon;
		this.top = top;
		this.bottoms = bottoms;
		this.shoes = shoes;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	...
}
{% endhighlight %}

2. 객체를 생성할 빌더 클래스를 작성한다. 이 때 메소드 실행 후 자기 자신을 반환하는 체이닝(chaining)을 이용하면 가독성을 향상시킬 수 있다.
{% highlight java %}
public class CharactorBuilder {
	private Charactor charactor;

	public CharactorBuilder() {
		charactor = new Charactor("남자", "까까머리", "검", "반팔티", "청반바지", "빨간장화");
	}

	public Charactor build() {
		return charactor;
	}
	
	public CharactorBuilder setGender(String gender) {
		charactor.setGender(gender);
		return this;
	}

	public CharactorBuilder setHair(String hair) {
		charactor.setHair(hair);
		return this;
	}
	...
}
{% endhighlight %}

3. 만들어본다.
{% highlight java %}
public static void main(String[] args) {
	Charactor charactor = new CharactorBuilder()
		.setWeapon("몽둥이")
		.setShoes("노란장화")
		.build();
}
{% endhighlight %}