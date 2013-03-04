package comtech.util.json;

import java.io.IOException;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2010-12-02 14:51:43 (Europe/Moscow)
 */
public class SoutAppendable implements Appendable {

    private StringBuilder sb = new StringBuilder();

    public Appendable append(CharSequence csq) throws IOException {
        System.out.println(csq);
        sb.append(csq);
        return this;
    }

    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        System.out.println(csq);
        sb.append(csq, start, end);
        return this;
    }

    public Appendable append(char c) throws IOException {
        System.out.println(c);
        sb.append(c);
        return this;
    }


    @Override
    public String toString() {
        return sb.toString();
    }
}
