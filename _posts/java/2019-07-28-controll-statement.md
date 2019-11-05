---
title: "[Java] 조건문, 반복문"
categories:
    - Java
---
★if문
{% highlight java %}
if (조건문) {
	System.out.println("첫번째 조건에 ㅇㅋ");
} else if (조건문) {
	System.out.println("첫번째 조건에는 ㄴㄴ후 두번째 조건에는 ㅇㅋ");
} else {
	System.out.println("첫번째 조건에 ㄴㄴ후 두번째 조건에도 ㄴㄴ");
}
{% endhighlight %}

★switch~case문
{% highlight java %}
switch(기준이 될 변수) {
	case 0:
		doSomething0();
		break;
	case 1:
		doSomething1();
		break;
	case 2:
		doSomething2();
		break;
	default:
		doSomething3();
		break;
}
{% endhighlight %}
위에서 부터 차례로 비교해서 내려온다. 기준이 될 변수가 0에 부합한다면 ':' 이후의 함수들을 실행하는데, case문이 끝나면 멈추는게 아니라 break가 나올때 까지 멈추지 않는다.
break를 붙였다 뗏다 하면서 플로우를 조절할 수 있다. default는 if문의 else같은 느낌

★for문
{% highlight java %}
for (int i = 0; i < 100; i++) {
	System.out.println("★");
}
{% endhighlight %}

★foreach문
{% highlight java %}
for (int[] inner : arr) {
	System.out.println("★ : " + inner);
}
{% endhighlight %}
이 경우 위처럼 index값은 사용할 수 없다.

★while문
{% highlight java %}
while(조건) {
	실행문;
}
{% endhighlight %}
조건문이 true라면 실행문을 실행한다. 조건을 잘못 설정해주면 무한루프에 빠지게 된다.

★break, continue<br>
둘다 실행을 멈추고 반복문을 탈출한다는 것은 같으나 break는 그대로 반복문을 종료, continue는 실행을 멈추고 다시 조건문으로 돌아간다는 점이 다르다.