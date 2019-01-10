package com.arman.jfx.util;

public class TimeFormatter {

	public static String format(String time) {
		if (time.length() != 5) {
			if (time.length() == 4) {
				if (time.charAt(1) == ':') {
					return "0" + time;
				} else if (time.charAt(2) == ':') {
					return time.substring(0, 3) + "0" + time.charAt(3);
				}
			} else if (time.length() == 3) {
				return "0" + time.substring(0, 2) + "0" + time.charAt(2);
			}
		}
		return time;
	}

}
