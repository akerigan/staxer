package comtech.util;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-12-08 10:30 (Europe/Moscow)
 */
public class BooleanUtils {

    public static boolean ifNullDefaultFalse(Boolean b) {
        if (b != null) {
            return b;
        } else {
            return false;
        }
    }

    public static boolean ifNullDefaultTrue(Boolean b) {
        if (b != null) {
            return b;
        } else {
            return true;
        }
    }

}
