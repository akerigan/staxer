package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author Anton Proshin (proshin.anton@gmail.com)
 * @since 2011-09-20 14:33 (Moscow/Europe)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SchemaImport {

    @XmlAttribute(name = "schemaLocation")
    private String schemaLocation;

    @XmlAttribute(name = "namespace")
    private String namespace;

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<SchemaImport>\n");
        sb.append("<schemaLocation>");
        sb.append(schemaLocation);
        sb.append("</schemaLocation>\n");
        sb.append("<namespace>");
        sb.append(namespace);
        sb.append("</namespace>\n");
        sb.append("</SchemaImport>\n");

        return sb.toString();
    }
}
