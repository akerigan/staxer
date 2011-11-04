package comtech.staxer.domain.wsdl;

import comtech.staxer.domain.schema.Schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 13:33:08
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WsdlTypes {

    @XmlElement(name = "schema", namespace = "http://www.w3.org/2001/XMLSchema")
    private List<Schema> schemas = new ArrayList<Schema>();

    public List<Schema> getSchemas() {
        return schemas;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WsdlTypes>\n");
        if (schemas != null) {
            sb.append("<schemas>");
            for (Object obj : schemas) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</schemas>\n");
        } else {
            sb.append("<schemas/>\n");
        }
        sb.append("</WsdlTypes>\n");

        return sb.toString();
    }
}
