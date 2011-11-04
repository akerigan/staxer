package comtech.staxer.domain.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 17:35:21
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WsEnumValue {

    @XmlElement(name = "value")
    private String value;
    @XmlElement(name = "javaName")
    private String javaName;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WsEnumValue>\n");
        sb.append("<value>");
        sb.append(value);
        sb.append("</value>\n");
        sb.append("<javaName>");
        sb.append(javaName);
        sb.append("</javaName>\n");
        sb.append("</WsEnumValue>\n");

        return sb.toString();
    }
}
