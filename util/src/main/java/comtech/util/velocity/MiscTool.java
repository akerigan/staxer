package comtech.util.velocity;

import comtech.util.StringUtils;
import comtech.util.date.DateTimeUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * DateTime: 2010-08-05-17-16 (Europe/Moscow)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class MiscTool {

    public static String formatDate(Date date) {
        try {
            return DateTimeUtils.formatDate(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static Collection<Integer> iterateOver(int start, int end) {
        return new IteratorCollection(start, end);
    }

    public static Collection<Integer> iterateOver2(int start, int end) {
        return new IteratorCollection(start, end + 1);
    }

    public static boolean emptyString(String s) {
        return StringUtils.isEmpty(s);
    }

    public static boolean notEmptyString(String s) {
        return !StringUtils.isEmpty(s);
    }

    public static int listLength(List list) {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public static int mapLength(Map map) {
        if (map != null) {
            return map.size();
        } else {
            return 0;
        }
    }

    public static String capitalize2(String s) {
        return StringUtils.capitalize2(s, true);
    }

    public static boolean contains(String s, String subs) {
        return StringUtils.contains(s, subs);
    }

    public static String normalizeTimezone(String timeOffset) {
        if (!StringUtils.isEmpty(timeOffset)) {
            String timeOffsetTrimmed = timeOffset.trim();
            if (timeOffsetTrimmed.charAt(0) == '-') {
                return "GMT" + timeOffset;
            } else {
                return "GMT+" + timeOffset;
            }
        } else {
            return "";
        }
    }

}
