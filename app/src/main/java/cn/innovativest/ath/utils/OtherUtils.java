package cn.innovativest.ath.utils;

public class OtherUtils {
	public static StackTraceElement getCallerStackTraceElement() {
		return Thread.currentThread().getStackTrace()[4];
	}
}
