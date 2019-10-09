package facade.system;

class HelpSystem3 {
	public HelpSystem3() {
		System.out.println("Call Constructor : " + getClass().getName());
	}
	
	public void process() {
		System.out.println("Call Process : " + getClass().getSimpleName());
	}
}