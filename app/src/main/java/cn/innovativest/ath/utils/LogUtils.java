package cn.innovativest.ath.utils;

import android.util.Log;

public class LogUtils {
	public static boolean DEBUG = true;

	public LogUtils() {
	}

	private static StackTraceElement getCallerStackTraceElement() {
		return Thread.currentThread().getStackTrace()[4];
	}

	private static String generateTag(StackTraceElement caller) {
		String tag = "%s | %s | %d";
		String callerClazzName = caller.getClassName();
		callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
		tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
		return tag;
	}

	public static void init(boolean isDebug) {
		DEBUG = isDebug;
	}

	public static void i(Object msg) {
		if (DEBUG && msg != null) {
			Log.i(generateTag(getCallerStackTraceElement()), msg.toString());
		}

	}

	public static void e(Object msg) {
		if (DEBUG && msg != null) {
			Log.e(generateTag(getCallerStackTraceElement()), msg.toString());
		}

	}

	public static void d(Object msg) {
		if (DEBUG && msg != null) {
			Log.d(generateTag(getCallerStackTraceElement()), msg.toString());
		}

	}

	public static void w(Object msg) {
		if (DEBUG && msg != null) {
			Log.w(generateTag(getCallerStackTraceElement()), msg.toString());
		}

	}

}
