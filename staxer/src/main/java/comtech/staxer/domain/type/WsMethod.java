package comtech.staxer.domain.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 17.09.2009
 * Time: 12:48:54
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WsMethod {

    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "inName")
    private String inName;
    @XmlElement(name = "inType")
    private String inType;
    @XmlElement(name = "outType")
    private String outType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInName() {
        return inName;
    }

    public void setInName(String inName) {
        this.inName = inName;
    }

    public String getInType() {
        return inType;
    }

    public void setInType(String inType) {
        this.inType = inType;
    }

    public String getOutType() {
        return outType;
    }

    public void setOutType(String outType) {
        this.outType = outType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WsMethod>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<inName>");
        sb.append(inName);
        sb.append("</inName>\n");
        sb.append("<inType>");
        sb.append(inType);
        sb.append("</inType>\n");
        sb.append("<outType>");
        sb.append(outType);
        sb.append("</outType>\n");
        sb.append("</WsMethod>\n");

        return sb.toString();
    }
}
