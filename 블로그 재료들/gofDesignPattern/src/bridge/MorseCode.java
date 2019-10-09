package bridge;

public class MorseCode {
	
	private MorseCodeRootFunction func;
	
	public MorseCode(MorseCodeRootFunction func) {
		this.func = func;
	}

	public void setFunc(MorseCodeRootFunction func) {
		this.func = func;
	}

	//	델리게이트
	public void dot() {
		func.dot();
	}
	public void dash() {
		func.dash();
	}
	public void space() {
		func.space();
	}
}
