package comtech.staxer.domain.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 13:01:49
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WsdlPortType {

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "operation", namespace = "http://schemas.xmlsoap.org/wsdl/")
    private List<WsdlOperation> operations = new ArrayList<WsdlOperation>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WsdlOperation> getOperations() {
        return operations;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WsdlPortType>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        if (operations != null) {
            sb.append("<operations>");
            for (Object obj : operations) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</operations>\n");
        } else {
            sb.append("<operations/>\n");
        }
        sb.append("</WsdlPortType>\n");

        return sb.toString();
    }
}
