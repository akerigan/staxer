package comtech.staxer.ws;

import comtech.util.xml.XmlName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-14 18:32 (Europe/Moscow)
 */
public class WebServiceType {

    private XmlName xmlName;
    private XmlName superTypeXmlName;
    private String javaPackage;
    private String jaxbXmlScheme;
    private String javaName;
    private List<WebServiceTypeField> fields = new ArrayList<WebServiceTypeField>();

    public XmlName getXmlName() {
        return xmlName;
    }

    public void setXmlName(XmlName xmlName) {
        this.xmlName = xmlName;
    }

    public XmlName getSuperTypeXmlName() {
        return superTypeXmlName;
    }

    public void setSuperTypeXmlName(XmlName superTypeXmlName) {
        this.superTypeXmlName = superTypeXmlName;
    }

    public String getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public List<WebServiceTypeField> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WebServiceType>\n");
        sb.append("<xmlName>");
        sb.append(xmlName);
        sb.append("</xmlName>\n");
        sb.append("<superTypeXmlName>");
        sb.append(superTypeXmlName);
        sb.append("</superTypeXmlName>\n");
        sb.append("<javaPackage>");
        sb.append(javaPackage);
        sb.append("</javaPackage>\n");
        sb.append("<javaName>");
        sb.append(javaName);
        sb.append("</javaName>\n");
        if (fields != null) {
            sb.append("<fields>");
            for (Object obj : fields) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</fields>\n");
        } else {
            sb.append("<fields/>\n");
        }
        sb.append("</WebServiceType>\n");

        return sb.toString();
    }
}
