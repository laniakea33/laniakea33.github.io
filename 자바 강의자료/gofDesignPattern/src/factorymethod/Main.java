package factorymethod;

import factorymethod.concreate.HpCreater;
import factorymethod.concreate.MpCreater;
import factorymethod.framework.Item;
import factorymethod.framework.ItemCreater;
/*
 * 팩토리 메소드 패턴
 * 	객체를 만드는 부분을 서브클래스에 델리게이트 하는 패턴이다.
 * 	여기서 객체를 만드는 절차는 ItemCreater가, 실제로 만드는 부분은 HpCreater, MpCreater가 담당한다.
 * 	결국 이 패턴은 객체(Potion)을 만들어 내는 팩토리(Creater)를 만드는 패턴이다.
 *
 * 이 패턴은 객체를 만드는 부분을 팩토리에 위임해, 메인클래스에서는 어떻게 객체가 만들어지는지
 * 전혀 신경쓰지 않도록 하기위해 사용한다. 결과적으로 클래스간의 결합도를 낮추게 된다.
 */
public class Main {
	public static void main(String[] args) {
		ItemCreater creater;
		Item item;
		
		creater = new HpCreater();
		item = creater.create();
		item.use();
		
		creater = new MpCreater();
		item = creater.create();
		item.use();
	}
}