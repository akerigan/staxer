package comtech.staxer.domain;

import comtech.util.xml.XmlName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-21 14:46 (Europe/Moscow)
 */
public class WebServiceEnum {

    private XmlName xmlName;
    private XmlName xmlType;
    private String javaName;
    private List<WebServiceEnumValue> values = new ArrayList<WebServiceEnumValue>();

    public XmlName getXmlName() {
        return xmlName;
    }

    public void setXmlName(XmlName xmlName) {
        this.xmlName = xmlName;
    }

    public XmlName getXmlType() {
        return xmlType;
    }

    public void setXmlType(XmlName xmlType) {
        this.xmlType = xmlType;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public List<WebServiceEnumValue> getValues() {
        return values;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WebServiceEnum>\n");
        sb.append("<xmlName>");
        sb.append(xmlName);
        sb.append("</xmlName>\n");
        sb.append("<xmlType>");
        sb.append(xmlType);
        sb.append("</xmlType>\n");
        sb.append("<javaName>");
        sb.append(javaName);
        sb.append("</javaName>\n");
        if (values != null) {
            sb.append("<values>");
            for (Object obj : values) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</values>\n");
        } else {
            sb.append("<values/>\n");
        }
        sb.append("</WebServiceEnum>\n");

        return sb.toString();
    }
}
