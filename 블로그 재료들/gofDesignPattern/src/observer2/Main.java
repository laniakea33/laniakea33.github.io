package observer2;
/*
 * 옵저버 패턴2
 * 
 * 자바에서 기본적으로 제공하는 것들
 * 	Observer 인터페이스 : update()함수를 가지고 있음
 * 	Observable 클래스 : 상속해서 사용할 수 있다.
 * 		changed라는 멤버변수가 있는데 이게 true일 때만 동작한다.
 * 		하지만 역시 클래스인지라... 확장성이 떨어지는 문제가 있다.
 */

import java.util.Observable;
import java.util.Observer;

public class Main {

	public static void main(String[] args) {
		Button button = new Button();
		button.addObserver(new Observer() {
			
			@Override
			public void update(Observable o, Object arg) {
				System.out.println(o + " is clicked");
			}
		});
		
		button.onClick();
		button.onClick();
		button.onClick();
	}
}