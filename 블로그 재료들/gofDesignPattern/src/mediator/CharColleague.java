package mediator;

import mediator.contract.Colleague;

public class CharColleague extends Colleague {

	@Override
	public void handle(String data) {
		System.out.println(this + " : " + data);
	}

}
