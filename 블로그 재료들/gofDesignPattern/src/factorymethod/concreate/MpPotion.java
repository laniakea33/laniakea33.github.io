package factorymethod.concreate;

import factorymethod.framework.Item;

public class MpPotion implements Item {

	@Override
	public void use() {
		System.out.println("���� ȸ��");
	}
}
