package comtech.util.json;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-05-13 11:00 (Europe/Moscow)
 */
public class JsonObjectNumber extends JsonObjectSimple {

    private Number num;

    public JsonObjectNumber(Number num) {
        if (num != null) {
            this.num = num;
        } else {
            this.num = new BigDecimal(0);
        }
    }

    public boolean isNull() {
        return false;
    }

    public boolean isNumber() {
        return true;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    @Override
    public void toJsonString(Appendable appendable) throws IOException {
        appendable.append(num.toString());
    }

    @Override
    public void fromJsonString(String s) {
        try {
            num = new BigDecimal(s);
        } catch (NumberFormatException e) {
            num = new BigDecimal(0);
        }
    }

    public Number getValue() {
        return num;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<JsonObjectNumber>\n");
        sb.append("<num>");
        sb.append(num);
        sb.append("</num>\n");
        sb.append("</JsonObjectNumber>\n");

        return sb.toString();
    }
}
