package factorymethod.concreate;

import java.util.Date;

import factorymethod.framework.Item;
import factorymethod.framework.ItemCreater;

public class HpCreater extends ItemCreater {

	@Override
	protected void requestItemInfo() {
		System.out.println("�����ͺ��̽����� ȸ�� ������ ������ �����ɴϴ�.");
	}

	@Override
	protected void createItemLog() {
		System.out.println("ȸ�� ������ ���� �����߽��ϴ�." + new Date());
	}

	@Override
	protected Item createItem() {
		return new HpPotion();
	}
}