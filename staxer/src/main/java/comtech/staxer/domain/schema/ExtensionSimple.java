package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.11.2009
 * Time: 16:22:54
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtensionSimple {

    @XmlAttribute(name = "base")
    private String valueType;

    @XmlElement(name = "attribute", namespace = "http://www.w3.org/2001/XMLSchema")
    private List<Attribute> attributes = new LinkedList<Attribute>();

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<ExtensionSimple>\n");
        sb.append("<valueType>");
        sb.append(valueType);
        sb.append("</valueType>\n");
        if (attributes != null) {
            sb.append("<attributes>");
            for (Object obj : attributes) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</attributes>\n");
        } else {
            sb.append("<attributes/>\n");
        }
        sb.append("</ExtensionSimple>\n");

        return sb.toString();
    }
}
