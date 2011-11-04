package comtech.staxer.domain.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 12:34:37
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WsdlPart {

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "element")
    private String element;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WsdlPart>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<element>");
        sb.append(element);
        sb.append("</element>\n");
        sb.append("</WsdlPart>\n");

        return sb.toString();
    }
}
