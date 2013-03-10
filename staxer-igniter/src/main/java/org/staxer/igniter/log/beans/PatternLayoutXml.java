package org.staxer.igniter.log.beans;

import org.staxer.util.http.helper.HttpRequestHelper;
import org.staxer.util.http.helper.ReadHttpParameters;
import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class PatternLayoutXml implements StaxerReadXml, StaxerWriteXml, ReadHttpParameters {

    public static final XmlName XML_NAME_NAME = new XmlName("name");
    public static final XmlName XML_NAME_CONVERSION_PATTERN = new XmlName("conversionPattern");

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "conversionPattern")
    private String conversionPattern;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConversionPattern() {
        return conversionPattern;
    }

    public void setConversionPattern(String conversionPattern) {
        this.conversionPattern = conversionPattern;
    }

    public void readHttpParameters(
            HttpRequestHelper httpRequestHelper
    ) {
        name = httpRequestHelper.getRequestParameter("name");
        conversionPattern = httpRequestHelper.getRequestParameter("conversionPattern");
    }

    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
        name = attributes.get(XML_NAME_NAME);
        conversionPattern = attributes.get(XML_NAME_CONVERSION_PATTERN);
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        XmlName rootElementName = xmlReader.getLastStartedElement();
        while (xmlReader.readNext() && !xmlReader.elementEnded(rootElementName)) {
            readXmlContentElement(xmlReader);
        }
    }

    public boolean readXmlContentElement(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        return false;
    }

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        xmlWriter.attribute(XML_NAME_NAME, name);
        xmlWriter.attribute(XML_NAME_CONVERSION_PATTERN, conversionPattern);
    }

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<PatternLayoutXml>\n");
        toString(sb);
        sb.append("</PatternLayoutXml>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<conversionPattern>");
        sb.append(conversionPattern);
        sb.append("</conversionPattern>\n");
    }

}
