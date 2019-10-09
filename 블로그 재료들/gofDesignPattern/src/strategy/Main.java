package strategy;
/*
 * 스트레터지(전략) 패턴
 */
public class Main {
	public static void main(String[] args) {
		
		GameCharactor gc = new GameCharactor();
		gc.attack();
		
		gc.setWeapon(new Knife());
		gc.attack();
		
		gc.setWeapon(new Sword());
		gc.attack();
		
		gc.setWeapon(new Ax());
		gc.attack();
		
	}
}