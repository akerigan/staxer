package org.staxer.util.json;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-07-15 10:21 (Europe/Moscow)
 */
public class StaxerJsonStreamException extends Exception {

    public StaxerJsonStreamException() {
    }

    public StaxerJsonStreamException(String message) {
        super(message);
    }

    public StaxerJsonStreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public StaxerJsonStreamException(Throwable cause) {
        super(cause);
    }

}
