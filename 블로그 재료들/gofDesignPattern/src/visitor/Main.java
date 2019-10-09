package visitor;

import java.util.ArrayList;

/*
 * 방문자 패턴
 * 	객체(Class)에서 처리(Method)를 분리해서 사용할 수 있다.
 * 
 * 	visitor가 visitable을 돌아가면서 방문해서 어떤 동작을 한다.
 */
public class Main {

	public static void main(String[] args) {
		ArrayList<Visitable> visitables = new ArrayList<Visitable>();
		visitables.add(new VisitableA(1));
		visitables.add(new VisitableA(2));
		visitables.add(new VisitableA(3));
		visitables.add(new VisitableA(4));
		visitables.add(new VisitableA(5));
		
		Visitor visitor = new VisitorA();
		
		for(Visitable vis : visitables) {
			vis.accept(visitor);
		}
		System.out.println(((VisitorA)visitor).getAgeSum());
	}
}