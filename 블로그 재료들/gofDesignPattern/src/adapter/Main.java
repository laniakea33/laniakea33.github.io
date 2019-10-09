package adapter;

/*
 * 어댑터 패턴
 * 	호환되지 않는 두 인터페이스를 연결해준다
 * 
 *  클라이언트가 요청 -> 어댑터가 변환 -> 어댑티에게 도달
 *  어댑터는 어댑티와 같은 인터페이스를 공유하게 하고
 *  클라이언트는 자신와 어댑티 사이에 어댑터가 있는지 알지 못해도 된다.
 *  
 *  어댑터에는 클래스 어댑터와 객체 어댑터 두종류가 있다
 *  클래스 어댑터는 어댑터를 만들때 타겟과 어댑티 둘을 다중상속하지만
 *  자바에서는 다중상속이 지원되지 않기 때문에 target만을 상속하는 객체 어댑터 패턴을 사용한다.
 *  
 *  예를 들어 Enumeration만을 사용하는 구형 코드를 Iterator를 사용하는 신형코드에 적용시키려고 할 때
 *  Target : Iterator
 *  Adapter : EnumerationIterator implements Iterator
 *  Adaptee : Enumeration
 *  
 *	이런 형식으로 적용 시키면 Enumeration인터페이스를 Iterator에 연결해서 사용할 수 있다.
 *
 *	이 예제에서는 Float만을 사용하는 코드(Target)에 Double을 사용하는 코드(Adaptee : Math클래스)를 연결(Adapter : AdapterImpl클래스)시킨 것이다.
 */
public class Main {
	public static void main(String[] args) {
		Adapter adapter = new AdapterImpl();
		System.out.println(adapter.twiceOf(100f));
		System.out.println(adapter.halfOf(88f));
	}
}