package comtech.util.xml.read;

import comtech.util.props.StringProperties;

import javax.xml.namespace.QName;
import java.util.LinkedHashMap;
import java.util.Map;

public class StartElement extends StringProperties {

    protected QName name;
    protected Map<String, String> attributes = new LinkedHashMap<String, String>();

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String get(String name) {
        return attributes.get(name);
    }

}
