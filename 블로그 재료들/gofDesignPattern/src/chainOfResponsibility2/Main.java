package chainOfResponsibility2;
/*
 * 책임 사슬 패턴2 - 확장
 * 
 * 게임에서 공격을 받고 처리하는 방식을 책임사슬 패턴으로 구현해 본다.
 * 
 * Armor(Defense)들을 줄줄줄 엮어 놓고
 * 프로세스를 차례차례 시켜 준다.
 * 
 * 책임 사슬 패턴1에서는 프로세스를 어떤 처리기가 받으면 거기서 끝이였으나
 *  2에서는 사슬의 처음부터 끝까지 모두 프로세스를 진행 시킨다.
 *  
 *  여러가지 객체가 공동으로 책임을 진다는 점이 가장 중요
 *  
 *  거기에 더해 이 예제에서는 동적으로 책임을 추가하였다.
 */
public class Main {

	public static void main(String[] args) {
		Attack attack = new Attack(100);
		
		Armor armor1 = new Armor(10);
		Armor armor2 = new Armor(15);
		
		armor1.setNextDefense(armor2);
		//	첫번째 공격 시작
		armor1.defense(attack);
		
		System.out.println(attack.getAmount());
		
		
		Defense helmet = new Defense() {
			
			private Defense nextDefense;
			
			@Override
			public void defense(Attack attack) {
				int amount = attack.getAmount();
				amount -= 50;
				attack.setAmount(amount);
				
				if(nextDefense != null) {
					nextDefense.defense(attack);
				}
			}
		};
		
		//	추가 착용
		armor2.setNextDefense(helmet);
		
		//	두번째 공격
		attack.setAmount(100);
		armor1.defense(attack);
		
		System.out.println(attack.getAmount());
	}
}