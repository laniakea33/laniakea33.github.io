package prototype2;

/*
 * 프로토타입 패턴 2
 * 
 * 프로토타입1과 같으나
 * 만약 clone되는 객체의 필드중에 객체가 있을 경우
 * clone함수는 객체 자체가 아니라 객체의 주소만을 복사해오게 된다.(얕은복사)
 * 이 경우 첫번째 객체의 필드 객체의 값을 변경하는 경우
 * 복사된 두번째 객체의 필드 값도 변경되므로 제대로 복사된다고 할 수 없다.
 * clone()함수를 재정의하여 새로운 객체를 만들어줘야 한다.(깊은복사)
 */

public class Main {

	public static void main(String[] args) {
		Cat navi = new Cat();
		navi.setName("나비");
		navi.setAge(new Age(2001,13));
		
		Cat yo = null;
		
		try {
			yo = navi.copy();
		} catch (CloneNotSupportedException e) { 
			e.printStackTrace();
		}

		yo.setName("요");
		yo.getAge().setYear(2013);
		yo.getAge().setValue(2);
		
		System.out.println(navi.getName());
		System.out.println(navi.getAge().getYear() + ", " + navi.getAge().getValue());
		System.out.println(yo.getName());
		System.out.println(yo.getAge().getYear() + ", " + yo.getAge().getValue());
	}
}