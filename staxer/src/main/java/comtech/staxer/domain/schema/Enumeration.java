package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 12:17:40
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Enumeration {

    @XmlAttribute(name = "value")
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Enumeration>\n");
        sb.append("<value>");
        sb.append(value);
        sb.append("</value>\n");
        sb.append("</Enumeration>\n");

        return sb.toString();
    }
}
