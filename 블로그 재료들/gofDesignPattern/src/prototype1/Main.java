package prototype1;

/*	
 * 프로토타입 패턴 1
 * 
 * 여러번 만들기 힘들거나 까다로운 객체의 경우
 * 하나 만들어 놓고 계속 복사해서 사용하게 할 수 있다.
 * 
 * 기본적으로 Object객체에 clone()함수가 내장되어 있는데
 * 객체에 Clonable인터페이스를 상속시켜 놓고
 * clone()함수를 오버라이드 해서 사용할 수 있다.
 */

public class Main {

	public static void main(String[] args) {
		Circle circle1 = new Circle(1,1,3);
		Circle circle2 = null; 
		try {
			circle2 = circle1.copy();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		System.out.println(circle1.getX()+ ", " + circle1.getY() + ", " + circle1.getR());
		System.out.println(circle2.getX()+ ", " + circle2.getY() + ", " + circle2.getR());
	}

}
