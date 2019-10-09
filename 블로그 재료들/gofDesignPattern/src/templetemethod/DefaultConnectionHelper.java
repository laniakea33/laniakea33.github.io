package templetemethod;

public class DefaultConnectionHelper extends AbsGameConnectionHelper {

	@Override
	protected String doSecurity(String str) {
		System.out.println("암호화 한다");
		return str;
	}

	@Override
	protected boolean authentication(String id, String password) {
		System.out.println("아이디 패스워드 인증한다");
		return true;
	}

	@Override
	protected int authozation(String userName) {
		System.out.println("인증중");
		return 0;
	}

	@Override
	protected String connection(String info) {
		System.out.println("연결 성공!");
		return info;
	}
}