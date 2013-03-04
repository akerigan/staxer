package comtech.util.json;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-07-15 10:21 (Europe/Moscow)
 */
public class JsonException extends Exception {

    public JsonException() {
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

}
