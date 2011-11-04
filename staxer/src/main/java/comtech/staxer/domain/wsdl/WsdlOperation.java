package comtech.staxer.domain.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 13:03:20
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WsdlOperation {

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "input", namespace = "http://schemas.xmlsoap.org/wsdl/")
    private WsdlInput input;

    @XmlElement(name = "output", namespace = "http://schemas.xmlsoap.org/wsdl/")
    private WsdlOutput output;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WsdlInput getInput() {
        return input;
    }

    public void setInput(WsdlInput input) {
        this.input = input;
    }

    public WsdlOutput getOutput() {
        return output;
    }

    public void setOutput(WsdlOutput output) {
        this.output = output;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WsdlOperation>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<input>");
        sb.append(input);
        sb.append("</input>\n");
        sb.append("<output>");
        sb.append(output);
        sb.append("</output>\n");
        sb.append("</WsdlOperation>\n");

        return sb.toString();
    }

}
