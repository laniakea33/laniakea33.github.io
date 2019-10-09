package factorymethod.concreate;

import factorymethod.framework.Item;

public class HpPotion implements Item {

	@Override
	public void use() {
		System.out.println("체력 회복");
	}
}
