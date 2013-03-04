package comtech.util.json;

import java.io.IOException;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-07-08 11:03 (Europe/Moscow)
 */
public abstract class JsonObjectSimple implements JsonObject {

    public boolean isSimple() {
        return true;
    }

    public boolean isList() {
        return false;
    }

    public boolean isMap() {
        return false;
    }

    public abstract boolean isNumber();

    public abstract boolean isBoolean();

    public abstract boolean isString();

    public abstract void toJsonString(Appendable appendable) throws IOException;

    public abstract void fromJsonString(String s);

}
