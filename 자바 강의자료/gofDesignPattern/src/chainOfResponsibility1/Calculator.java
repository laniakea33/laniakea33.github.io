package chainOfResponsibility1;

public abstract class Calculator {

	private Calculator nextCalc;

	public void setNext(Calculator nextCalc) {
		this.nextCalc = nextCalc;
	}

	public boolean process(Request request) {
		if (operator(request)) {
			return true;
		} else {
			if (nextCalc != null) {
				return nextCalc.process(request);
			} else {
				return false;
			}
		}
	}

	abstract protected boolean operator(Request request);
}