package singleton;
/*
 * 싱글톤 패턴
 * 	객체를 메모리에 단 하나만 올리도록 할때 사용한다.
 * 	모든 클래스에서 하나의 인스턴스를 공유하는 모양이 된다.
 */
public class Main {

	public static void main(String[] args) {
		SystemSpeaker speaker1 = SystemSpeaker.getInstance();
		SystemSpeaker speaker2 = SystemSpeaker.getInstance();
		
		//	5,5
		System.out.println(speaker1.getVolume());
		System.out.println(speaker2.getVolume());
		
		speaker1.setVolume(11);
		//	11,11
		System.out.println(speaker1.getVolume());
		System.out.println(speaker2.getVolume());
		
		speaker2.setVolume(22);
		//	22,22
		System.out.println(speaker1.getVolume());
		System.out.println(speaker2.getVolume());
	}
}