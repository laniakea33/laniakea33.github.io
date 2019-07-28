package mediator;

import mediator.contract.Colleague;
import mediator.contract.Mediator;

/*
 * 미디에이터(중재자) 패턴
 * 	복잡한 관계(M:N)를 간단한 관계(1:1)로 구현한다.
 *	예를들어 그룹채팅 같은 기능이 있음
 */
public class Main {
	public static void main(String[] args) {
		Mediator mediator = new ChatMediator();
		
		Colleague colleague1 = new CharColleague();
		Colleague colleague2 = new CharColleague();
		Colleague colleague3 = new CharColleague();
		
		colleague1.join(mediator);
		colleague2.join(mediator);
		colleague3.join(mediator);
		
		colleague1.sendData("AAA");
		colleague2.sendData("BBB");
		colleague3.sendData("CCC");
	}
}