package comtech.util.staxer.domain;

import comtech.util.xml.XmlName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-21 14:46 (Europe/Moscow)
 */
public class XmlSchemaSimpleType implements XmlSchemaType{

    private XmlName xmlName;
    private XmlName superTypeXmlName;
    private String javaName;
    private List<XmlSchemaEnumValue> values = new ArrayList<XmlSchemaEnumValue>();
    private String documentation;

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

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public String getJavaPackage() {
        return null;
    }

    public List<XmlSchemaEnumValue> getValues() {
        return values;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public boolean isComplexType() {
        return false;
    }

    public boolean isEnumSimpleType() {
        return !values.isEmpty();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<XmlSchemaSimpleType>\n");
        sb.append("<xmlName>");
        sb.append(xmlName);
        sb.append("</xmlName>\n");
        sb.append("<superTypeXmlName>");
        sb.append(superTypeXmlName);
        sb.append("</superTypeXmlName>\n");
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
        sb.append("<documentation>");
        sb.append(documentation);
        sb.append("</documentation>\n");
        sb.append("</XmlSchemaSimpleType>\n");

        return sb.toString();
    }
}
