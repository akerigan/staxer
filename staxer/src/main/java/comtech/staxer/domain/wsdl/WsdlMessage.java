package comtech.staxer.domain.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 12:31:31
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WsdlMessage {

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "part", namespace = "http://schemas.xmlsoap.org/wsdl/")
    private WsdlPart part;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WsdlPart getPart() {
        return part;
    }

    public void setPart(WsdlPart part) {
        this.part = part;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WsdlMessage>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<part>");
        sb.append(part);
        sb.append("</part>\n");
        sb.append("</WsdlMessage>\n");

        return sb.toString();
    }
}
