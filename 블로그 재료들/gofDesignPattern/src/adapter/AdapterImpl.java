package adapter;

public class AdapterImpl implements Adapter {
	
	@Override
	public Float twiceOf(Float f) {
		System.out.println("");
		return (float)Math.doubled(f.doubleValue());
	}

	@Override
	public Float halfOf(Float f) {
		System.out.println("");
		return (float)Math.half(f.doubleValue());
	}
}