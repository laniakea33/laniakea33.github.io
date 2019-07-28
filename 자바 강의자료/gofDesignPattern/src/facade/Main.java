package facade;

import facade.system.Facade;

/*
 * 퍼사드 패턴
 * 	복잡한 과정을 간단하게 제공
 * 
 * HelpSystem들이 굉장히 복잡할 때
 * Facade클래스로 간단히 모아 진행 가능
 * HelpSystem들을 메인에서 사용할 수 없도록 해놓고 Facade에서 접근하여 사용한다. 
 */
public class Main {

	public static void main(String[] args) {
		Facade facade = new Facade();
		facade.process();
	}

}
