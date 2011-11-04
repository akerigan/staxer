package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 12:13:47
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleType {

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "restriction", namespace = "http://www.w3.org/2001/XMLSchema")
    private Restriction restriction;

    @XmlElement(name = "union", namespace = "http://www.w3.org/2001/XMLSchema")
    private Union union;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

    public Union getUnion() {
        return union;
    }

    public void setUnion(Union union) {
        this.union = union;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<SimpleType>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<restriction>");
        sb.append(restriction);
        sb.append("</restriction>\n");
        sb.append("<union>");
        sb.append(union);
        sb.append("</union>\n");
        sb.append("</SimpleType>\n");

        return sb.toString();
    }
}
