package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.LinkedList;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 11:49:32
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ComplexType {

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "sequence", namespace = "http://www.w3.org/2001/XMLSchema")
    private Sequence sequence;

    @XmlElement(name = "attribute", namespace = "http://www.w3.org/2001/XMLSchema")
    private List<Attribute> attributes = new LinkedList<Attribute>();

    @XmlElement(name = "simpleContent", namespace = "http://www.w3.org/2001/XMLSchema")
    private SimpleContent simpleContent;

    @XmlElement(name = "complexContent", namespace = "http://www.w3.org/2001/XMLSchema")
    private ComplexContent complexContent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public SimpleContent getSimpleContent() {
        return simpleContent;
    }

    public void setSimpleContent(SimpleContent simpleContent) {
        this.simpleContent = simpleContent;
    }

    public ComplexContent getComplexContent() {
        return complexContent;
    }

    public void setComplexContent(ComplexContent complexContent) {
        this.complexContent = complexContent;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<ComplexType>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
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
        sb.append("<simpleContent>");
        sb.append(simpleContent);
        sb.append("</simpleContent>\n");
        sb.append("<complexContent>");
        sb.append(complexContent);
        sb.append("</complexContent>\n");
        sb.append("</ComplexType>\n");

        return sb.toString();
    }

}
