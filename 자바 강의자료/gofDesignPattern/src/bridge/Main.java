package bridge;

/*
 * 브릿지 패턴
 * 
 * 기능부분과 구현부분의 분리
 * 어댑터 패턴과 굉장히 비슷함
 * 
 * 예제 : 모스부호 출력코드
 * 
 * MorseCodeRootFunction인터페이스를 구현한 Default, Flash, SoundMCF에서 기능을 구현하고
 * MorseCode클래스에서 해당 인터페이스로 기능을 델리게이트 한 다음 PrintMorseCode에서 기능을 사용해준다.
 * 기능이 인터페이스로 델리게이트 되었으므로 인터페이스를 구현한 DefaultMCF등등에서
 * 기능을 구현해주면 된다.
 * 
 * 이 경우에 구현은 DefaultMCF, FlashMCF, SoundMCF부분에서 담당하고
 * 기능은 MCF를 가지는 MorseCode를 상속한 PrintMorseCode에서 담당한다.
 */

public class Main {

	public static void main(String[] args) {
		//PrintMorseCode code = new PrintMorseCode(new DefaultMCF());
		//PrintMorseCode code = new PrintMorseCode(new SoundMCF());
		PrintMorseCode code = new PrintMorseCode(new FlashMCF());
		code.g().a().r().a().m();
	}
}
