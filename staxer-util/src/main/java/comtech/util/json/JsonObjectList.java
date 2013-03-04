package comtech.util.json;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-05-13 11:00 (Europe/Moscow)
 */
public class JsonObjectList implements JsonObject {

    private List<JsonObject> list;

    public JsonObjectList() {
    }

    public JsonObjectList(List<JsonObject> list) {
        this.list = list;
    }

    public boolean isNull() {
        return list == null;
    }

    public boolean isList() {
        return true;
    }

    public boolean isMap() {
        return false;
    }

    public boolean isSimple() {
        return false;
    }

    public int size() {
        return list != null ? list.size() : 0;
    }

    public boolean contains(JsonObject object) {
        return list.contains(object);
    }

    public void add(JsonObject jsonObject) {
        if (list == null) {
            list = new ArrayList<JsonObject>();
        }
        list.add(jsonObject);
    }

    public JsonObjectList addString(String s) {
        add(new JsonObjectString(s));
        return this;
    }

    public void addNumber(Number n) {
        add(new JsonObjectNumber(n));
    }

    public void addBoolean(Boolean b) {
        add(new JsonObjectBoolean(b));
    }

    public JsonObject get(int idx) {
        if (list != null && idx < list.size()) {
            return list.get(idx);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<JsonObjectList>\n");
        if (list != null) {
            sb.append("<list>");
            for (Object obj : list) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</list>\n");
        } else {
            sb.append("<list/>\n");
        }
        sb.append("</JsonObjectList>\n");

        return sb.toString();
    }
}
