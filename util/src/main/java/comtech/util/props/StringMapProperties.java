package comtech.util.props;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 24.09.2009
 * Time: 12:10:43
 */
public class StringMapProperties extends StringProperties {

    private Map<String, String> parametersMap = new TreeMap<String, String>();

    public StringMapProperties() {
    }

    public StringMapProperties(Map<String, String> stringMap) {
        parametersMap.putAll(stringMap);
    }

    public Set<String> getNames() {
        Set<String> names = new TreeSet<String>();
        names.addAll(parametersMap.keySet());
        return names;
    }

    public Map<String, String> getMap() {
        return parametersMap;
    }

    public String get(String name) {
        return parametersMap.get(name);
    }

    public String put(String key, String value) {
        return parametersMap.put(key, value);
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<StringMapProperties>\n");
        if (parametersMap != null) {
            sb.append("<parametersMap>");
            for (Object key : parametersMap.keySet()) {
                sb.append("<entry key=\"");
                sb.append(key);
                sb.append("\">");
                sb.append(parametersMap.get(key));
                sb.append("</entry>\n");
            }
            sb.append("</parametersMap>\n");
        } else {
            sb.append("<parametersMap/>\n");
        }
        sb.append("</StringMapProperties>\n");

        return sb.toString();
    }
}
