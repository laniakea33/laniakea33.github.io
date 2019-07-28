package bridge;

public class SoundMCF implements MorseCodeRootFunction {

	@Override
	public void dot() {
		System.out.println("삣");
	}

	@Override
	public void dash() {
		System.out.println("삐이");
		
	}

	@Override
	public void space() {
		System.out.println(" ");
	}
	
}
