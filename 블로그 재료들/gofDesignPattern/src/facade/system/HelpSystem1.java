package facade.system;

class HelpSystem1 {
	public HelpSystem1() {
		System.out.println("Call Constructor : " + getClass().getName());
	}
	
	public void process() {
		System.out.println("Call Process : " + getClass().getSimpleName());
	}
}