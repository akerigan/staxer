package comtech.staxer.domain.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 15:08:51
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WsType {

    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "xmlElementName")
    private String xmlElementName;

    @XmlElement(name = "enumType")
    private boolean enumType;
    @XmlElement(name = "enumValue")
    private List<WsEnumValue> enumValues = new LinkedList<WsEnumValue>();
    @XmlElement(name = "field")
    private List<WsField> fields = new ArrayList<WsField>();
    @XmlElement(name = "containedField")
    private List<WsField> containedFields = new ArrayList<WsField>();

    @XmlElement(name = "javaTypeName")
    private String javaTypeName;
    @XmlElement(name = "superTypeName")
    private String superTypeName;

    @XmlElement
    private String prefix = "";

    @XmlElement
    private String nsURI = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXmlElementName() {
        return xmlElementName;
    }

    public void setXmlElementName(String xmlElementName) {
        this.xmlElementName = xmlElementName;
    }

    public boolean isEnumType() {
        return enumType;
    }

    public void setEnumType(boolean enumType) {
        this.enumType = enumType;
    }

    public List<WsEnumValue> getEnumValues() {
        return enumValues;
    }

    public List<WsField> getFields() {
        return fields;
    }

    public List<WsField> getContainedFields() {
        return containedFields;
    }

    public String getJavaTypeName() {
        return javaTypeName;
    }

    public void setJavaTypeName(String javaTypeName) {
        this.javaTypeName = javaTypeName;
    }

    public String getSuperTypeName() {
        return superTypeName;
    }

    public void setSuperTypeName(String superTypeName) {
        this.superTypeName = superTypeName;
    }

    public boolean isRequest() {
        return javaTypeName.endsWith("Request");
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getNsURI() {
        return nsURI;
    }

    public void setNsURI(String nsURI) {
        this.nsURI = nsURI;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WsType>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<xmlElementName>");
        sb.append(xmlElementName);
        sb.append("</xmlElementName>\n");
        sb.append("<enumType>");
        sb.append(enumType);
        sb.append("</enumType>\n");
        if (enumValues != null) {
            sb.append("<enumValues>");
            for (Object obj : enumValues) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</enumValues>\n");
        } else {
            sb.append("<enumValues/>\n");
        }
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
        sb.append("<javaTypeName>");
        sb.append(javaTypeName);
        sb.append("</javaTypeName>\n");
        sb.append("<superTypeName>");
        sb.append(superTypeName);
        sb.append("</superTypeName>\n");
        sb.append("<prefix>");
        sb.append(prefix);
        sb.append("</prefix>\n");
        sb.append("<nsURI>");
        sb.append(nsURI);
        sb.append("</nsURI>\n");
        sb.append("</WsType>\n");

        return sb.toString();
    }
}
