package flyweight;
/*
 * 플라이웨이트 패턴
 * 	메모리공간을 절약한다.
 * 
 * 예제
 * 	클라이언트가 이미지를 다룬다고 가정한다.
 * 	같은 이미지를 여러번 사용하는 상황에서
 * 	매번 이미지를 새로 생성해야 한다면 메모리의 낭비가 발생한다.
 * 	그래서 이미지(FlyWeight)를 관리하는 매니저 객체(FlyWeightFactory)를 만들어
 * 	클라이언트가 원하는 이미지가 이미 있는경우 있는걸 넘겨주고
 * 	없을때만 생성해서 넘겨줄 수 있다.
 */
public class Main {

	public static void main(String[] args) {
		FlyweightFactory factory = new FlyweightFactory();
		Flyweight flyweight = factory.getFlyweight("A");

		System.out.println(flyweight);

		flyweight = factory.getFlyweight("A");

		System.out.println(flyweight);

		flyweight = factory.getFlyweight("B");

		System.out.println(flyweight);

		flyweight = factory.getFlyweight("B");

		System.out.println(flyweight);

		flyweight = factory.getFlyweight("A");

		System.out.println(flyweight);
	}
}