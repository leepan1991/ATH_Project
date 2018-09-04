package cn.innovativest.ath.utils;

import java.util.List;

public class CUtils {

    public static boolean isEmpty(Object o) {
        if (o instanceof String) {
            if (o == null || "null".equalsIgnoreCase(o.toString()) || o.toString().trim().isEmpty()) {
                return true;
            }
        } else if (o instanceof List) {
            List<?> list = (List) o;
            if (list == null || list.size() == 0) {
                return true;
            }
        } else if (o == null) {
            return true;
        }

        return false;
    }
}
