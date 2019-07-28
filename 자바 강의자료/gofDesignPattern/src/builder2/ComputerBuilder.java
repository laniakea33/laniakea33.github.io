package builder2;

public class ComputerBuilder {

	private Computer computer;
	
	private ComputerBuilder() {
		computer = new Computer("default", "default", "default");
	}
	
	public static ComputerBuilder start() {
		return new ComputerBuilder();
	}
	
	/*	시작할때 cpu를 넣을 수 있게 이렇게 만들 수도 있음
	public static ComputerBuilder startWithCPU(String cpu) {
		ComputerBuilder cb =  new ComputerBuilder();
		cb.computer.setCpu(cpu);
		return cb;
	}
	*/
	
	public ComputerBuilder setCpu(String cpu) {
		computer.setCpu(cpu);
		return this;
	}
	
	public ComputerBuilder setRam(String ram) {
		computer.setRam(ram);
		return this;
	}
	
	public ComputerBuilder setStorage(String storage) {
		computer.setStorage(storage);
		return this;
	}
	
	public Computer build() {
		return this.computer;
	}
	
	
}
