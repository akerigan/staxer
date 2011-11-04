package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.List;
import java.util.LinkedList;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 27.11.2009
 * Time: 10:49:21
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtensionComplex {

    @XmlAttribute(name = "base")
    private String baseType;

    @XmlElement(name = "sequence", namespace = "http://www.w3.org/2001/XMLSchema")
    private Sequence sequence;

    @XmlElement(name = "attribute", namespace = "http://www.w3.org/2001/XMLSchema")
    private List<Attribute> attributes = new LinkedList<Attribute>();

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<ExtensionComplex>\n");
        sb.append("<baseType>");
        sb.append(baseType);
        sb.append("</baseType>\n");
        sb.append("<sequence>");
        sb.append(sequence);
        sb.append("</sequence>\n");
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
        sb.append("</ExtensionComplex>\n");

        return sb.toString();
    }
}
