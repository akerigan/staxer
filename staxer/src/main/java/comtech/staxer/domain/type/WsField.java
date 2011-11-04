package comtech.staxer.domain.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import static comtech.util.StringUtils.capitalize;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 16:02:36
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WsField {

    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "typeName")
    private String typeName;
    @XmlElement(name = "nillable")
    private boolean nillable;
    @XmlElement(name = "required")
    private boolean required;
    @XmlElement(name = "list")
    private boolean list;

    @XmlElement(name = "javaName")
    private String javaName;
    @XmlTransient
    private String javaNameCap;
    @XmlElement(name = "javaTypeName")
    private String javaTypeName;

    @XmlElement(name = "xmlElement")
    private boolean xmlElement;
    @XmlElement(name = "value")
    private boolean value;
    @XmlElement(name = "isEnum")
    private boolean isEnum;

    @XmlTransient
    private String prefix;

    // null if default xmlns
    @XmlTransient
    private String nsURI;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public String getJavaNameCap() {
        if (javaNameCap == null) {
            javaNameCap = capitalize(javaName);
        }
        return javaNameCap;
    }

    public String getJavaTypeName() {
        return javaTypeName;
    }

    public void setJavaTypeName(String javaTypeName) {
        this.javaTypeName = javaTypeName;
    }

    public boolean isXmlElement() {
        return xmlElement;
    }

    public void setXmlElement(boolean xmlElement) {
        this.xmlElement = xmlElement;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean isEnum() {
        return isEnum;
    }

    public void setEnum(boolean anEnum) {
        isEnum = anEnum;
    }

    public boolean isDateTime() {
        return "dateTime".equals(typeName);
    }

    public boolean isDate() {
        return "date".equals(typeName);
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
        sb.append("<WsField>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<typeName>");
        sb.append(typeName);
        sb.append("</typeName>\n");
        sb.append("<nillable>");
        sb.append(nillable);
        sb.append("</nillable>\n");
        sb.append("<required>");
        sb.append(required);
        sb.append("</required>\n");
        sb.append("<list>");
        sb.append(list);
        sb.append("</list>\n");
        sb.append("<javaName>");
        sb.append(javaName);
        sb.append("</javaName>\n");
        sb.append("<javaNameCap>");
        sb.append(javaNameCap);
        sb.append("</javaNameCap>\n");
        sb.append("<javaTypeName>");
        sb.append(javaTypeName);
        sb.append("</javaTypeName>\n");
        sb.append("<xmlElement>");
        sb.append(xmlElement);
        sb.append("</xmlElement>\n");
        sb.append("<value>");
        sb.append(value);
        sb.append("</value>\n");
        sb.append("<isEnum>");
        sb.append(isEnum);
        sb.append("</isEnum>\n");
        sb.append("<prefix>");
        sb.append(prefix);
        sb.append("</prefix>\n");
        sb.append("<nsURI>");
        sb.append(nsURI);
        sb.append("</nsURI>\n");
        sb.append("</WsField>\n");

        return sb.toString();
    }
}
