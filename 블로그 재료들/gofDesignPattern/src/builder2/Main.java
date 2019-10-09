package builder2;
/*
 * 빌더 패턴 2
 * 	키워드는 가독성, 수많은 변수
 * 	결국 엄청나게 많은 변수를 가진 객체를 생성할 때 쉽게 사용할 수 있도록 하는 것
 */
public class Main {

	public static void main(String[] args) {
		Computer com = ComputerBuilder.start()
				.setCpu("i7")
				.setRam("8g")
				.build();
		
		System.out.println(com);
	}

}
