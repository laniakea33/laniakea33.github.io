package templetemethod;
/*
 * 템플릿 메소드 패턴
 * 
 * 전체적인 알고리즘의 흐름을 먼저 추상클래스로 만든 후
 * 세부적인 메소드를 구현하는 클래스를 만듬
 */
public class Main {
	public static void main(String[] args) {
		AbsGameConnectionHelper helper = new DefaultConnectionHelper();
		helper.requestConnection("안뇽");
	}
}
