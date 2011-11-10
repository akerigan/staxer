package comtech.util.xml;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-10 12:26 (Europe/Moscow)
 */
public class StaxerXmlStreamException extends Exception {

    public StaxerXmlStreamException() {
    }

    public StaxerXmlStreamException(String message) {
        super(message);
    }

    public StaxerXmlStreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public StaxerXmlStreamException(Throwable cause) {
        super(cause);
    }

    public StaxerXmlStreamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
