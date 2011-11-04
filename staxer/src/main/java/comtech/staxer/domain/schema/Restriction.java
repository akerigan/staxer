package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 12:15:21
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Restriction {

    @XmlAttribute(name = "base")
    private String base;

    @XmlElement(name = "enumeration", namespace = "http://www.w3.org/2001/XMLSchema")
    private List<Enumeration> enumerations = new ArrayList<Enumeration>();

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public List<Enumeration> getEnumerations() {
        return enumerations;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Restriction>\n");
        sb.append("<base>");
        sb.append(base);
        sb.append("</base>\n");
        if (enumerations != null) {
            sb.append("<enumerations>");
            for (Object obj : enumerations) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</enumerations>\n");
        } else {
            sb.append("<enumerations/>\n");
        }
        sb.append("</Restriction>\n");

        return sb.toString();
    }
}
