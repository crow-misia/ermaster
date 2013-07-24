package org.insightech.er.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class DateUtil {
	public static final ThreadLocal<DateFormat> YYYYMMDD = generateSimpleDateFormat("yyyy/MM/dd");
	public static final ThreadLocal<DateFormat> YYYYMMDDHHMMSS = generateSimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static final ThreadLocal<DateFormat> YYYYMMDDbar = generateSimpleDateFormat("yyyy-MM-dd");
	public static final ThreadLocal<DateFormat> YYYYMMDDHHMMSSbar = generateSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final ThreadLocal<DateFormat> YYYYMMDDHHMMSSSSSbar = generateSimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	private DateUtil() {
		// nop.
	}

	private static ThreadLocal<DateFormat> generateSimpleDateFormat(final String pattern) {
		return new ThreadLocal<DateFormat>() {
			@Override
			protected SimpleDateFormat initialValue() {
				return new SimpleDateFormat(pattern);
			}
		};
	}
}
