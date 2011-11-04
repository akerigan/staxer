package comtech.util;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-02-15 12:41 (Europe/Moscow)
 */
public class NumberUtils {

    public static BigDecimal getBigDecimal(Number n) {
        if (n != null) {
            return new BigDecimal(n.doubleValue());
        } else {
            return null;
        }
    }

    public static Integer getInteger(BigDecimal value) throws SQLException {
        if (value != null) {
            return value.intValue();
        } else {
            return null;
        }
    }

    public static Float getFloat(BigDecimal value) {
        if (value != null) {
            return value.floatValue();
        } else {
            return null;
        }
    }

    public static int compare(Number n1, Number n2) {
        if (n1 == null && n2 == null) {
            return 0;
        } else if (n1 == null) {
            return 1;
        } else if (n2 == null) {
            return 1;
        } else {
            double d = n1.doubleValue() - n2.doubleValue();
            if (d > 0) {
                return 1;
            } else if (d == 0) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    public static String toString(Integer i) {
        if (i != null) {
            return Integer.toString(i);
        } else {
            return null;
        }
    }

    public static String toString(Float f) {
        if (f != null) {
            double v = f.doubleValue();
            if (v - Math.floor(v) > 0) {
                return String.format("%.2f", v).replaceAll(",", "\\.");
            } else {
                return String.format("%.0f", v).replaceAll(",", "\\.");
            }
        } else {
            return "0";
        }
    }

    public static Integer parseInteger(String s) {
        try {
            if (!StringUtils.isEmpty(s)) {
                return Integer.valueOf(s);
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    public static Float parseFloat(String s) {
        try {
            if (!StringUtils.isEmpty(s)) {
                return Float.valueOf(s);
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    public static Number firstNotEmpty(Number... nn) {
        if (nn != null) {
            for (Number n : nn) {
                if (n != null && n.doubleValue() != 0d) {
                    return n;
                }
            }
        }
        return null;
    }

    public static int getPrimitiveInt(Integer i) {
        return getPrimitiveInt(i, 0);
    }

    public static int getPrimitiveInt(Integer i, int defaultValue) {
        if (i != null) {
            return i;
        } else {
            return defaultValue;
        }
    }

}
