package observer1;

import observer1.Button.OnClickListener;

/*
 * 옵저버 패턴1
 * 	이벤트 발생 후 객체 외부에서 처리한다.
 */
public class Main {

	public static void main(String[] args) {
		Button button = new Button();
		
		//button.setOnClickListener(new ButtonClick());
		//	익명 클래스 버전
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Button button) {
				System.out.println(button + " is clicked");
			}
		});
		
		button.onClick();
	}

}

class ButtonClick implements OnClickListener {

	@Override
	public void onClick(Button button) {
		System.out.println(button + " is clicked");
		
	}
	
}
