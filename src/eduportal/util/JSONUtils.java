package eduportal.util;

import java.util.HashMap;

public class JSONUtils {
	
	public static HashMap<String, String> parse (String jsobj) {
		HashMap<String, String> ret = new HashMap<>();
		if (!jsobj.startsWith("{") || !jsobj.endsWith("}")) {
			return null;
		}
		String [] str = jsobj.split(",");
		for (String s : str) {
			String[] data = s.trim().split("=");
			ret.put(data[0], data[1]);
		}
		return ret;
	}
}
