package org.staxer.igniter.log.beans;

import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class AppenderXml implements StaxerReadXml, StaxerWriteXml {

    public static final XmlName XML_NAME_NAME = new XmlName("name");
    public static final XmlName XML_NAME_LAYOUT = new XmlName("layout");

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "layout")
    private String layout;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
        name = attributes.get(XML_NAME_NAME);
        layout = attributes.get(XML_NAME_LAYOUT);
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
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
        xmlWriter.attribute(XML_NAME_LAYOUT, layout);
    }

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<AppenderXml>\n");
        toString(sb);
        sb.append("</AppenderXml>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<layout>");
        sb.append(layout);
        sb.append("</layout>\n");
    }

}
