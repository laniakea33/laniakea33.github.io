package builder1;

public class LgGramBlueprint extends Blueprint {
	
	private Computer computer = null;
	private String cpu = "";
	private String ram = "";
	private String storage = "";
	
	public LgGramBlueprint() {
		//computer = new Computer("default", "default", "default");
	}

	@Override
	public void setCpu() {
		//computer.setCpu("i7");
		cpu = "i7";
	}

	@Override
	public void setRam() {
		//computer.setRam("8g");
		ram = "8g";
	}

	@Override
	public void setStorage() {
		//computer.setStorage("256GB SSD");
		storage = "256GB SSD";
	}

	@Override
	public Computer getComputer() {
		return computer;
	}
}