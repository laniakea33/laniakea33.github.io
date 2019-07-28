---
title: "[Java] Exception"
categories:
    - Java
---
★프로그램 컴파일시, 런타임시에 더이상 진행할 수 없는 예외가 발생하면 자바런처가 예외발생을 알린다.

★예외처리<br>
try {..}	: 예외 발생 영역을 블록으로 지정<br>
catch(e) {..}	: try에서 발생된 예외 객체를 잡는다.<br>
finally {..} : try이후 예외가 발생했든 안했든 블럭을 실행한다. 보통 입출력 스트림을 닫는 용도로 많이 사용하며 예외와 관련되어 실행되어야 하는 코드를 쓴다.

★Exception객체 : 예외의 정보들을 담고있는 객체이다. printStackTrace()메소드는 메소드 스택을 역행하면서 에러시점을 출력한다.

★커스텀 Exception : 직접 예외를 만들어 사용할 수 있다
{% highlight java %}
class MyException extends Exception {
	public MyException(String m) {
		super(m);
	}
}
{% endhighlight %}

★프로그램 작성시 커스텀 예외 발생이 필요한 곳에 throw new MyException과 같이 예외를 던진다.

★함수 선언시 함수 내부에서 예외를 처리하는게 아니라 함수를 호출한 부분으로 넘길 필요가 있을 때 throws키워드를 사용할 수 있다
{% highlight java %}
public int divide(int a, int b) throws MyException {
	if (b==0) throw new MyException();
	return a/b;
}
{% endhighlight %}

★RuntimeException : 런타임시에 발생하는것 = 컴파일시에는 발생하지 않는다는 의미. 명시적인 예외처리를 강제하지 않아도 되므로 주로 프로그램 자체의 에러를 탐지하기위해 의도적으로 사용된다. 주로 개발자의 미스에 의해 발생되는 예외들이 많으므로 개발자 수준에서 처리가 가능하기에 컴파일러가 체크하지 않는다는 느낌. 반대로 RuntimeException을 제외한 Exception(컴파일시에 발생하는 예외들)클래스들은 주로 사용자의 오용과 같은 외부요소에 의해 발생할 수 있으므로 컴파일시에 미리 체크를 하는 것이다.