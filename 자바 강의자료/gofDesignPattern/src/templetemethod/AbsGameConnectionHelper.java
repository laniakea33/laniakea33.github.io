package templetemethod;

public abstract class AbsGameConnectionHelper {
	
	protected abstract String doSecurity(String str);
	protected abstract boolean authentication(String id, String password);
	protected abstract int authozation(String userName);
	protected abstract String connection(String info);
	
	public String requestConnection(String encodedInfo) {
		String info = doSecurity(encodedInfo);
		String id = info + "aaa";
		String password = info + "bbb";
		
		if(!authentication(id, password)) {
			throw new Error("인증 오류 발생");
		};
	
		int i = authozation(id);
		switch (i) {
		case -1:
			throw new Error("오류!!");
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
			default:
				break;
		}
		
		return connection(info);
	}	 
}