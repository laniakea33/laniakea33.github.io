package state;
/*
 * 스테이트(상태) 패턴
 * 
 * 상태를 객체로 나타내고 행동을 구현한다
 * 
 * 스트레터지 패턴이랑 크게 다를게 없으나
 * 스트레터지 패턴은 알고리즘을 변경시킨다는 점,
 * 스테이트 패턴은 어떤 이벤트로 인해 상태를 변화시킨다는 점,
 * 
 * 스트레터지 패턴은 알고리즘에서 구현된 행동이
 * 상태에 영향을 주지는 않지만
 * 스테이트 패턴은 영향을 준다는 점이 다르다.
 */
public class Main {

	public static void main(String[] args) {
		Light light = new Light();
		light.off();
		light.off();
		light.off();
		
		light.on();
		light.on();
		light.on();
		
		light.off();
		light.on();
		light.off();
		light.on();
		light.off();
		light.on();
	}
}