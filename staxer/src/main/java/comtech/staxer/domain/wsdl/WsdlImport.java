package comtech.staxer.domain.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * DateTime: 2010-08-03-10-52 (Europe/Moscow)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WsdlImport {

    @XmlAttribute(name = "location")
    private String location;

    @XmlAttribute(name = "namespace")
    private String namespace;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
        sb.append("<WsdlImport>\n");
        sb.append("<location>");
        sb.append(location);
        sb.append("</location>\n");
        sb.append("<namespace>");
        sb.append(namespace);
        sb.append("</namespace>\n");
        sb.append("</WsdlImport>\n");

        return sb.toString();
    }
}
