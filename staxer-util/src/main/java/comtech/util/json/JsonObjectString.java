package comtech.util.json;

import comtech.util.StringUtils;

import java.io.IOException;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-05-13 11:00 (Europe/Moscow)
 */
public class JsonObjectString extends JsonObjectSimple {

    private String s;

    public JsonObjectString(String s) {
        this.s = s;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isString() {
        return true;
    }

    @Override
    public void toJsonString(Appendable appendable) throws IOException {
        appendable.append('"');
        if (s != null) {
            appendable.append(StringUtils.escapeJson(s));
        }
        appendable.append('"');
    }

    @Override
    public void fromJsonString(String s) {
        this.s = s;
    }

    public String getValue() {
        return s;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<JsonObjectString>\n");
        sb.append("<s>");
        sb.append(s);
        sb.append("</s>\n");
        sb.append("</JsonObjectString>\n");

        return sb.toString();
    }
}
