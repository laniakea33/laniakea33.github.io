package chainOfResponsibility1;

public class PlusCalculator extends Calculator {

	@Override
	protected boolean operator(Request request) {
		if (request.getOperator().equals("+")) {
			int a = request.getA();
			int b = request.getB();
			int result = a+b;
			System.out.println("c1에서 처리 : " + a + " + " + b + "=" + result);
			return true;
		}
		return false;
	}
}