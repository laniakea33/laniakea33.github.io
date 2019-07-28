package abstractfactory;

public class FactoryInstance {
	public static GuiFactory getGuiFactory()  {
		switch (getOsCode()) {
		case 0: return new MacGuiFactory();
		case 1: return new WinGuiFactory();
		case 2: return new LinuxGuiFactory();
		default:
			return null;
		}
	}
	
	private static int getOsCode() {
		if(System.getProperty("os.name").equals("Max OS X")) {
			return 0;
		} else if (System.getProperty("os.name").equals("Windows 10")) {
			return 1;
		}
		return 2;
	}
}

class LinuxButton implements Button {

	@Override
	public void click() {
		System.out.println("리눅스 버튼");
	}

}

class LinuxTextArea implements TextArea {

	@Override
	public String getText() {
		return "리눅스 텍스트 에어리어";
	}
	
}

class LinuxGuiFactory implements GuiFactory {

	@Override
	public Button createButton() {
		return new LinuxButton();
	}

	@Override
	public TextArea createTextArea() {
		return new LinuxTextArea();
	}
	
}

class MacButton implements Button {

	@Override
	public void click() {
		System.out.println("맥 버튼");
	}

}

class MacGuiFactory implements GuiFactory {
	@Override
	public Button createButton() {
		
		return new MacButton();
	}

	@Override
	public TextArea createTextArea() {
		return new MacTextArea();
	}
}

class MacTextArea implements TextArea {

	@Override
	public String getText() {
		return "맥 텍스트 에어리어";
	}
	
}

class WinButton implements Button {

	@Override
	public void click() {
		System.out.println("윈도우 버튼");
	}

}

class WinGuiFactory implements GuiFactory {
	@Override
	public Button createButton() {
		return new WinButton();
	}

	@Override
	public TextArea createTextArea() {
		return new WinTextArea();
	}
}

class WinTextArea implements TextArea {

	@Override
	public String getText() {
		return "윈도우 텍스트 에어리어";
	}
	
}
