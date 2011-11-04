package comtech.staxer.domain.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 17.09.2009
 * Time: 12:54:18
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WsMessage {

    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "elementName")
    private String elementName;
    @XmlElement(name = "elementType")
    private WsType elementType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public WsType getElementType() {
        return elementType;
    }

    public void setElementType(WsType type) {
        this.elementType = type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WsMessage>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<elementName>");
        sb.append(elementName);
        sb.append("</elementName>\n");
        sb.append("<elementType>");
        sb.append(elementType);
        sb.append("</elementType>\n");
        sb.append("</WsMessage>\n");

        return sb.toString();
    }
}
