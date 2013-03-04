package comtech.util.props;

import comtech.util.xml.XmlName;

import java.util.*;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 24.09.2009
 * Time: 12:10:43
 */
public class XmlNameMapProperties extends KeyProperties<XmlName> {

    private Map<XmlName, String> parametersMap = new LinkedHashMap<XmlName, String>();

    public XmlNameMapProperties() {
    }

    public XmlNameMapProperties(Map<XmlName, String> stringMap) {
        parametersMap.putAll(stringMap);
    }

    public Set<XmlName> getNames() {
        Set<XmlName> names = new TreeSet<XmlName>();
        names.addAll(parametersMap.keySet());
        return names;
    }

    public Map<XmlName, String> getMap() {
        return parametersMap;
    }

    @Override
    public String get(XmlName xmlName) {
        return parametersMap.get(xmlName);
    }

    public String put(XmlName key, String value) {
        return parametersMap.put(key, value);
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<XmlNameMapProperties>\n");
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
        sb.append("</XmlNameMapProperties>\n");

        return sb.toString();
    }

}
