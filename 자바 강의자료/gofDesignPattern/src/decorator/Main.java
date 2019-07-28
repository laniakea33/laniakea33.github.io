package decorator;

import java.util.Scanner;

/*
 * 데코레이터 패턴
 * 	동적으로 책임 추가가 필요할때 사용할 수 있다.
 * 
 * 
 * 컴포넌트 : 실질적인 컴포넌트를 컨트롤해줌(IBeverage)
 * 데코레이터 : 컴포넌트와 장식을 동일시 해줌(AbstAdding - Espresso, Milk)
 * 	Espresso와 Milk는 Base의 장식품에 불과함. 이 장식을 하나하나 더해주는 느낌
 * 컨크리트 컴포넌트 : 컴포넌트의 실질적인 인스턴스이며 책임의 주체(Base)
 * 
 * 예제 - 커피 제조방법
 * 	에스프레소 : 커피의 기본
 * 	아메리카노 : 에스프레소 + 물
 * 	카페라떼 : 에스프레서 + 스팀밀크
 * 	헤이즐넛 : 아메리카노 + 시럽
 * 	카페모카 : 카페라떼 + 초콜릿
 * 	캬라멜 마키아또 : 카페라떼 + 캬라멜시럽
 */
public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		IBeverage beverage = new Base();
		boolean done = false;
		while (!done) {
			System.out.println("음료 현재 가격 : " + beverage.getTotalPrice());
			System.out.println("선택 : 1 : 샷추가 / 2 : 우유 추가");
			switch(Integer.parseInt(sc.nextLine())) {
			case 0 : 
				done = true;
				break;
			case 1:
				beverage = new Espresso(beverage);
				break;
			case 2:
				beverage = new Milk(beverage);
				break;
			}
		}
		
		sc.close();
	}
}