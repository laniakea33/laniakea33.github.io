package chainOfResponsibility1;
/*
 * 책임 사슬 패턴1
 * 
 * 다양한 처리방식을 유연하게 처리할 수 있다.
 * 
 * 예제 : 사칙연산을 하는 프로그램 
 */
public class Main {

	public static void main(String[] args) {
		Calculator plus = new PlusCalculator();
		Calculator sub = new SubCalculator();
		
		plus.setNext(sub);
		Request request1 = new Request(1,2,"+");	//	3
		Request request2 = new Request(10,2,"-");	//	8
		
		plus.process(request1);
		plus.process(request2);
	}
}