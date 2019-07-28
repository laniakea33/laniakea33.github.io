package factorymethod.framework;

public abstract class ItemCreater {
	
	public Item create() {
		requestItemInfo();	//	step1
		Item item = createItem();	//	step2
		createItemLog();	//	step3
		return item;
	}
	
	abstract protected void requestItemInfo();
	
	abstract protected void createItemLog();
	
	abstract protected Item createItem();
}