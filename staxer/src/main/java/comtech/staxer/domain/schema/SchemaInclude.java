package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author Anton Proshin (proshin.anton@gmail.com)
 * @since 2011-09-20 14:36 (Moscow/Europe)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SchemaInclude {

    @XmlAttribute(name = "schemaLocation")
    private String schemaLocation;

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<SchemaInclude>\n");
        sb.append("<schemaLocation>");
        sb.append(schemaLocation);
        sb.append("</schemaLocation>\n");
        sb.append("</SchemaInclude>\n");

        return sb.toString();
    }
}
