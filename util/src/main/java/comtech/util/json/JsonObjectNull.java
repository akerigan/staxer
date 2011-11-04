package comtech.util.json;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-05-13 12:31 (Europe/Moscow)
 */
public class JsonObjectNull implements JsonObject {

    public static final String NULL_JSON = "\"\"";

    private static JsonObjectNull instance = new JsonObjectNull();

    private JsonObjectNull() {
    }

    public static JsonObjectNull getInstance() {
        return instance;
    }

    public boolean isNull() {
        return true;
    }

    public boolean isList() {
        return false;
    }

    public boolean isMap() {
        return false;
    }

    public boolean isSimple() {
        return false;
    }

}
