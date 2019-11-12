---
title: "[Java] Gof 디자인 패턴 - 추상팩토리(Abstract Factory)"
categories:
    - Java
---
★추상 팩토리 패턴 : 객체의 생성을 가상화 한다. 

★카테고리별로 세트화 해서 사용해야 하는 객체들을 통채로 묶어서 생성하는 팩토리 클래스를 각 카테고리별로 하나씩 만들고, 이 팩토리 클래스들도 전부 묶어서 하나의 팩토리 클래스에서 조건에 따라 생성하게 한다. 여기서 핵심은 사용처(Factoy의 인스턴스를 사용하는 곳)는 이러한 조건에 따른 프로세스를 알 필요가 없도록 하는 것이다.

★객체들을 조건에 따라 일관성있게 만들 필요가 있을 때 사용한다.

★예를 들어 갤럭시나 아이폰을 만들 때 스크린과 카메라를 모두 자사 제품을 써야한다고 하자. 이 때 부품(Screen, Camera)을 생산하는 팩토리 클래스들을 생성하고, 삼성팩토리에서는 삼성부품만을, 애플팩토리에서는 애플부품만을 생산 하는 것이다. 조건에 따라 삼성폰이나 아이폰을 생성하므로 어떤 팩토리 클래스를 사용할지를 전체적으로 총괄해주는 폰 팩토리 클래스에서 각 팩토리 클래스를 생성해주도록 한다.

★Test
1. 각 부품을 인터페이스로 정의한다.
카메라
{% highlight java %}
public interface ICamera {
	void takePhoto();
}
{% endhighlight %}
스크린
{% highlight java %}
public interface IScreen {
	void show();
}
{% endhighlight %}

2. 각 팩토리들의 인터페이스를 설계한다.
{% highlight java %}
public interface IPhoneFactory {
	public IScreen createScreen();
	public ICamera createCamera();
}
{% endhighlight %}

3. 팩토리를 구현한다.
{% highlight java %}
public class Factory {
	public static IPhoneFactory getInstance(String brand) {
		if (brand.equals("갤럭시")) {
			return new SamsungFactory();
		} else if (brand.equals("애플")) {
			return new AppleFactory();
		} else {
			throw new RuntimeException("브랜드 네임이 이상하다.");
		}
	}
}

class SamsungFactory implements IPhoneFactory {
	@Override
	public IScreen createScreen() {
		return new SamsungScreen();
	}
	@Override
	public ICamera createCamera() {
		return new SamsungCamera();
	}
}

class AppleFactory implements IPhoneFactory {
	@Override
	public IScreen createScreen() {
		return new AppleScreen();
	}
	@Override
	public ICamera createCamera() {
		return new AppleCamera();
	}
}

class SamsungScreen implements IScreen {
	@Override
	public void show() {
		System.out.println("삼성 스크린 반짝");
	}
}

class SamsungCamera implements ICamera {
	@Override
	public void takePhoto() {
		System.out.println("삼성 카메라 찰칵");
	}
}

class AppleScreen implements IScreen {
	@Override
	public void show() {
		System.out.println("애플 스크린 반짝");
	}
}

class AppleCamera implements ICamera {
	@Override
	public void takePhoto() {
		System.out.println("애플 카메라 찰칵");
	}
}
{% endhighlight %}

4. 사용한다.
{% highlight java %}
public static void main(String[] args) {
		IPhoneFactory factory = Factory.getInstance("애플");
		IScreen screen = factory.createScreen();
		ICamera camera = factory.createCamera();
		
		screen.show();
		camera.takePhoto();
	}
{% endhighlight %}

★'애플'이라는 조건을 넣어주었을 때 팩토리는 내부적으로 애플팩토리를 만들고, 애플팩토리는 애플스크린과 애플카메라를 만들도록 되었다. main함수에서는 그런 프로세스를 알 필요가 없이 만들어진 스크린과 카메라를 사용하면 된다.