package org.insightech.er.util;

public final class Check {

	public static boolean isAlphabet(String str) {
		char[] ch = str.toCharArray();

		for (int i = 0, n = ch.length; i < n; i++) {
			final char c = ch[i];
			if (c < '0' || 'z' < c) {
				return false;
			}

			if ('9' < c && c < 'A') {
				return false;
			}

			if ('z' < c && c < '_') {
				return false;
			}

			if ('_' < c && c < 'a') {
				return false;
			}
		}

		return true;
	}

	public static boolean equals(Object str1, Object str2) {
		if (str1 == null) {
			if (str2 == null) {
				return true;
			}

			return false;
		}

		return str1.equals(str2);
	}

	public static boolean isEmpty(String str) {
		if (str == null || str.length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmpty(String str) {
		if (str == null || str.length() == 0) {
			return false;
		}
		return true;
	}

	public static boolean isBlank(String str) {
		final char[] ch = str.toCharArray();
		for (final char c : ch) {
			if (c != '\u0020') {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(String str) {
		final char[] ch = str.toCharArray();
		for (final char c : ch) {
			if (c != '\u0020') {
				return true;
			}
		}
		return false;
	}
}
