package comtech.staxer.domain.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 13:14:24
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WsdlOutput {

    @XmlAttribute(name = "message")
    private String messageName;

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WsdlOutput>\n");
        sb.append("<messageName>");
        sb.append(messageName);
        sb.append("</messageName>\n");
        sb.append("</WsdlOutput>\n");

        return sb.toString();
    }
}
