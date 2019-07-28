package chainOfResponsibility2;

public class Armor implements Defense {

	private Defense nextDefense;
	private int def;
	
	public Armor(int def) {
		this.def = def;
	}

	@Override
	public void defense(Attack attack) {
		int amount = attack.getAmount();
		amount -= def;
		attack.setAmount(amount);
		
		if(nextDefense != null) {
			nextDefense.defense(attack);
		}
	}

	public void setNextDefense(Defense nextDefense) {
		this.nextDefense = nextDefense;
	}	
}