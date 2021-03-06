package org.staxer.util.staxer.domain;

import org.staxer.util.xml.XmlName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-24 15:45 (Europe/Moscow)
 */
public class XmlSchemaXsdType {

    private XmlName xmlName;
    private List<String> imports = new ArrayList<String>();
    private String jaxbXmlSchema;
    private String javaName;
    private String javaConverter;
    private String xmlConverter;

    public XmlName getXmlName() {
        return xmlName;
    }

    public void setXmlName(XmlName xmlName) {
        this.xmlName = xmlName;
    }

    public List<String> getImports() {
        return imports;
    }

    public String getJaxbXmlSchema() {
        return jaxbXmlSchema;
    }

    public void setJaxbXmlSchema(String jaxbXmlSchema) {
        this.jaxbXmlSchema = jaxbXmlSchema;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public String getJavaConverter() {
        return javaConverter;
    }

    public void setJavaConverter(String javaConverter) {
        this.javaConverter = javaConverter;
    }

    public String getXmlConverter() {
        return xmlConverter;
    }

    public void setXmlConverter(String xmlConverter) {
        this.xmlConverter = xmlConverter;
    }
}
