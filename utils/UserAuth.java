package utils;

public class UserAuth {
	private static final String ADMIN_PWD = "123456789"; 
	public static boolean verifyAdminPassword(String p) {
		if (p == null)
			return false;
		String in = p.trim();
		if (in.length() >= 2 && in.startsWith("\"") && in.endsWith("\"")) {
			in = in.substring(1, in.length() - 1);
		}
		return ADMIN_PWD.equals(in);
	}
}


