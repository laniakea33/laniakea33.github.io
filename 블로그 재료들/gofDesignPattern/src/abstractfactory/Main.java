package abstractfactory;

/*
 * 추상 팩토리 패턴 - 객체의 생성을 가상화
 * 	관련된 객체들을 통채로 묶어서 한 팩토리 클래스에서 만들도록 하고
 * 	그렇게 만들어진 여러종류의 팩토리 클래스들을 조건에 따라
 * 	생성하도록 하는 또다른 팩토리 클래스를 사용
 * 
 * 객체들을 조건에 따라 일관성 있게 만들 필요가 있을 때 사용한다
 * 	ex) 삼성컴퓨터를 만들때 부품들을 모두 삼성부품으로 만들어야 할때
 * 	부품(interface)를 생산하는 팩토리(interface)를 생성하고
 * 	그 팩토리를 구현한 삼성팩토리(class)에서 부품을 구현한 삼성부품(class)
 * 	를 생성하게 하는 방식
 * 	삼성팩토리, 엘지팩토리 등등 경우에 따라 다양한 팩토리를 분기해서 사용하므로
 * 	전체적으로 총괄해주는 팩토리(class)에서 팩토리를 생성해주는 기능을
 * 	구현한다.  
 * 
 * 예제 : 
 * 다양한 제조 공정의 팩토리들을 FactoryInstance에서
 * 상황에 따라 다르게(여기서는 OS에 따라 다르게)
 * GuiFactory를 구현한 여러 팩토리 객체를 만들어 준다.
 */
public class Main {

	public static void main(String[] args) {
		GuiFactory fac = FactoryInstance.getGuiFactory();
		Button button = fac.createButton();
		TextArea area = fac.createTextArea();
		
		button.click();
		System.out.println(area.getText());
	}
}