package comtech.util.json;

import java.io.IOException;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-05-13 11:00 (Europe/Moscow)
 */
public class JsonObjectBoolean extends JsonObjectSimple {

    private boolean bool;

    public JsonObjectBoolean(Boolean bool) {
        this.bool = Boolean.TRUE.equals(bool);
    }

    public boolean isNull() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isBoolean() {
        return true;
    }

    public boolean isString() {
        return false;
    }

    @Override
    public void toJsonString(Appendable appendable) throws IOException {
        appendable.append(Boolean.toString(bool));
    }

    @Override
    public void fromJsonString(String s) {
        bool = Boolean.parseBoolean(s);
    }

    public boolean getValue() {
        return bool;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<JsonObjectBoolean>\n");
        sb.append("<bool>");
        sb.append(bool);
        sb.append("</bool>\n");
        sb.append("</JsonObjectBoolean>\n");

        return sb.toString();
    }
}
