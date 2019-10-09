package prototype1;

public class Shape implements Cloneable{
	
	private String id;

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}