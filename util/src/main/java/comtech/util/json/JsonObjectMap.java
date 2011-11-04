package comtech.util.json;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-05-13 11:00 (Europe/Moscow)
 */
public class JsonObjectMap implements JsonObject {

    private Map<String, JsonObject> map;

    public boolean isNull() {
        return map == null;
    }

    public boolean isList() {
        return false;
    }

    public boolean isMap() {
        return true;
    }

    public boolean isSimple() {
        return false;
    }

    public int size() {
        return map != null ? map.size() : 0;
    }

    public void put(String name, JsonObject jsonObject) {
        if (jsonObject != null) {
            if (map == null) {
                map = new LinkedHashMap<String, JsonObject>();
            }
            map.put(name, jsonObject);
        }
    }

    public void putString(String name, String s) {
        if (s != null) {
            put(name, new JsonObjectString(s));
        }
    }

    public void putNumber(String name, Number n) {
        if (n != null) {
            put(name, new JsonObjectNumber(n));
        }
    }

    public void putBoolean(String name, Boolean b) {
        if (b != null) {
            put(name, new JsonObjectBoolean(b));
        }
    }

    public JsonObject get(String name) {
        if (map != null) {
            return map.get(name);
        } else {
            return null;
        }
    }

    public Iterator<String> fieldsNames() {
        if (map != null) {
            return map.keySet().iterator();
        } else {
            return null;
        }
    }

    public boolean containsField(Object key) {
        return map.containsKey(key);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<JsonObjectMap>\n");
        if (map != null) {
            sb.append("<map>");
            for (String key : map.keySet()) {
                sb.append("<entry key=\"");
                sb.append(key);
                sb.append("\">");
                sb.append(map.get(key));
                sb.append("</entry>\n");
            }
            sb.append("</map>\n");
        } else {
            sb.append("<map/>\n");
        }
        sb.append("</JsonObjectMap>\n");

        return sb.toString();
    }
}
