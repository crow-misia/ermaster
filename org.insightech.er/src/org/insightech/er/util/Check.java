package org.insightech.er.util;

public final class Check {

	public static boolean isAlphabet(String str) {
		final int n = str.length();
		
		for (int i = 0; i < n; i++) {
			final char c = str.charAt(i);
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
}
