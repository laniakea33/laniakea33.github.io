package builder1;
/*
 * 	빌더 패턴
 * 
 * 	★복잡한 단계가 필요한 인스턴스 생성을 빌더패턴을 통해 쉽게 구현할 수 있다.
 * 		ㄴ객체의 구현을 추상화 시킨 후 서브클래스에게 넘겨주는 패턴이다.
 * 
 * 	★Blueprint객체는 설계도가 되고, Factory객체는 설계도대로 객체를 만드는 역할을 한다.
 */
public class Main {

	public static void main(String[] args) {
		ComputerFactory factory = new ComputerFactory();
		factory.setBlueprint(new LgGramBlueprint());
		factory.make();
		Computer com = factory.getComputer();
		
		System.out.println(com);
	}
}