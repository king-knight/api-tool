package com.jiaparts.api.util;

import java.util.List;

import com.jiaparts.api.token.Key;

public class StringUtils {
	public static boolean isBlank(String s) {
		if (s != null && !s.trim().equals("")) {
			return false;
		}
		return true;
	}

	public static boolean isNotBlank(String s) {
		return !isBlank(s);
	}

	public static boolean inArray(String s, String[] arr) {
		if (s == null || arr == null) {
			return false;
		}
		for (String arrStr : arr) {
			if (s.equals(arrStr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isModify(String s) {
		return inArray(s, new String[] { Key.PUBLIC.getName(), Key.PRIVATE.getName(), Key.PROTECTED.getName(),
				Key.DEFAULT.getName(),Key.ABSTRACT.getName(),Key.STATIC.getName(),Key.FINAL.getName()});
	}

	public static int searchInArray(String s, String[] arr) {
		if (s == null || arr == null) {
			return -1;
		}
		for (int i = 0; i < arr.length; i++) {
			if (s.equals(arr[i])) {
				return i;
			}
		}
		return -1;
	}

	public static String readLinesAsString(List<String> lines) {
		StringBuilder builder = new StringBuilder();
		lines.forEach((s) -> builder.append(s));
		return builder.toString().trim();
	}
}
