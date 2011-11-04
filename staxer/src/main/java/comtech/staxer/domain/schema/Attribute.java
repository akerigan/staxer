package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.11.2009
 * Time: 16:23:54
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Attribute {

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "type")
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Attribute>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<type>");
        sb.append(type);
        sb.append("</type>\n");
        sb.append("</Attribute>\n");

        return sb.toString();
    }
}
