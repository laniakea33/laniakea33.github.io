package proxy;
/*
 * 프록시 패턴
 * 	대리인을 내세워서 간단한 작업을 대신 처리하게 하는 것
 * 	즉 작업을 나눠서 구현할 수 있다.
 */
public class Main {

	public static void main(String[] args) {
		Subject real = new RealSubject();
		Subject proxy1 = new Proxy(real);
		Subject proxy2 = new Proxy(real);
		
		proxy1.action1();
		proxy2.action1();
		proxy1.action2();
		proxy2.action2();
		
	}

}
