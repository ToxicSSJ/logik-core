package com.magmaticcode.logikcore.util;

/**
 * The few string helpers the legacy engine used from commons-lang3, with the
 * same null handling, so the dependency can go without changing behavior.
 */
public final class Strings {

	private Strings() {}

	/** Occurrences of {@code sub} in {@code str}; 0 when either is null or empty. */
	public static int countMatches(String str, String sub) {
		if (str == null || str.isEmpty() || sub == null || sub.isEmpty())
			return 0;
		int count = 0;
		int index = 0;
		while ((index = str.indexOf(sub, index)) != -1) {
			count++;
			index += sub.length();
		}
		return count;
	}

	/** Concatenates the elements' {@code toString()}; null elements count as empty, a null iterable gives null. */
	public static String join(Iterable<?> items, String separator) {
		if (items == null)
			return null;
		StringBuilder out = new StringBuilder();
		boolean first = true;
		for (Object item : items) {
			if (!first && separator != null)
				out.append(separator);
			if (item != null)
				out.append(item);
			first = false;
		}
		return out.toString();
	}

	/** {@link String#indexOf(String)}, or -1 when either argument is null. */
	public static int indexOf(String str, String search) {
		if (str == null || search == null)
			return -1;
		return str.indexOf(search);
	}

	/** {@link String#lastIndexOf(String)}, or -1 when either argument is null. */
	public static int lastIndexOf(String str, String search) {
		if (str == null || search == null)
			return -1;
		return str.lastIndexOf(search);
	}

	/** The text between the first {@code open} and the next {@code close}; null when absent. */
	public static String substringBetween(String str, String open, String close) {
		if (str == null || open == null || close == null)
			return null;
		int start = str.indexOf(open);
		if (start == -1)
			return null;
		int end = str.indexOf(close, start + open.length());
		if (end == -1)
			return null;
		return str.substring(start + open.length(), end);
	}
}
