package org.staxer.util.staxer.domain;

import org.staxer.util.xml.XmlName;

/**
 * @author Vlad Vinichenko(akerigan@gmail.com)
 * @since 2011-10-15 15:04 (Europe/Moscow)
 */
public class XmlSchemaTypeField {

    private XmlName xmlName;
    private String javaName;
    private XmlName xmlType;
    private boolean nillable;
    private boolean required;
    private boolean array;
    private boolean elementField;
    private boolean valueField;
    private String documentation;

    public XmlName getXmlName() {
        return xmlName;
    }

    public void setXmlName(XmlName xmlName) {
        this.xmlName = xmlName;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public XmlName getXmlType() {
        return xmlType;
    }

    public void setXmlType(XmlName xmlType) {
        this.xmlType = xmlType;
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

    public boolean isArray() {
        return array;
    }

    public void setArray(boolean array) {
        this.array = array;
    }

    public boolean isElementField() {
        return elementField;
    }

    public void setElementField(boolean elementField) {
        this.elementField = elementField;
    }

    public boolean isValueField() {
        return valueField;
    }

    public void setValueField(boolean valueField) {
        this.valueField = valueField;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<XmlSchemaTypeField>\n");
        sb.append("<xmlName>");
        sb.append(xmlName);
        sb.append("</xmlName>\n");
        sb.append("<javaName>");
        sb.append(javaName);
        sb.append("</javaName>\n");
        sb.append("<xmlType>");
        sb.append(xmlType);
        sb.append("</xmlType>\n");
        sb.append("<nillable>");
        sb.append(nillable);
        sb.append("</nillable>\n");
        sb.append("<required>");
        sb.append(required);
        sb.append("</required>\n");
        sb.append("<array>");
        sb.append(array);
        sb.append("</array>\n");
        sb.append("<elementField>");
        sb.append(elementField);
        sb.append("</elementField>\n");
        sb.append("<valueField>");
        sb.append(valueField);
        sb.append("</valueField>\n");
        sb.append("<documentation>");
        sb.append(documentation);
        sb.append("</documentation>\n");
        sb.append("</XmlSchemaTypeField>\n");

        return sb.toString();
    }
}
