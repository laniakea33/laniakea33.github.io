package singleton;

public class SystemSpeaker {
	private int volume;
	
	private SystemSpeaker() {
		volume = 5;
	}
	
	public static SystemSpeaker getInstance() {
		return LazyHolder.INSTANCE;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	private static class LazyHolder {
		private static final SystemSpeaker INSTANCE = new SystemSpeaker();
	}
}