package strategy;

public class GameCharactor {
	
	private Weapon weapon;
	
	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}
	
	public void attack() {
		if (weapon != null) {
			weapon.attack();
		} else {
			System.out.println("맨손으로 후림");
		}
	}
}