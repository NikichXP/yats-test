package eduportal.util;

public class IdUtils {
	
	public static String convertId (long id) {
		StringBuffer sb = new StringBuffer(Long.toHexString(id).toUpperCase());
		for (int i = sb.toString().length()-1; i > 0; i--) {
			if ((i & 3) == 0) {
				sb.insert(i, '-');
			}
		}
		return sb.toString();
	}
	
	public static Long convertString (String id) {
		if (!id.contains("-")) {
			return Long.parseLong(id, 16);
		}
		if (!id.matches("[0-9A-F]{4}(\\-[0-9A-F]{4}){0,}")) {
			return null;
		}
		id = id.toUpperCase(); //This is just for now, as a test
		long res = 0;
		for (int c : id.toCharArray()) {
			c -= 48;
			if (c < 0) {
				continue;
			} else if (c > 9) {
				c -= 7;
			}
			res = res << 4;
			res += c;
		}
		return res;
	}

}
