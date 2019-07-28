package observer3;

import observer3.Observable.Observer;

/*
 * 옵저버 패턴3 - 옵저버 패턴을 좀더 세련되게
 * 
 * 1.제네릭 사용
 * 2.델리게이트 사용
 * 3.내부에 옵저버를 넣는다
 * 
 * 델리게이트를 사용해서 상속하지 않고도 옵저버블을 멤버변수로 가지고
 * 사용할 수 있도록 했다.
 * 
 */

public class Main {

	public static void main(String[] args) {
		Button button = new Button();
		button.addObserver(new Observer<String>() {
			
			@Override
			public void update(Observable<String> o, String arg) {
				System.out.println(o + " is clicked");
			}
		});
		
		button.onClick();
		button.onClick();
		button.onClick();
	}
}