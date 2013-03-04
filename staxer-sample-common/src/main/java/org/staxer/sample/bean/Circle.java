package org.staxer.sample.bean;

import org.staxer.util.NumberUtils;
import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.StaxerXmlStreamException;
import org.staxer.util.xml.StaxerXmlStreamReader;
import org.staxer.util.xml.StaxerXmlStreamWriter;
import org.staxer.util.xml.XmlName;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Circle extends Point {

    public static final XmlName XML_NAME_RADIUS = new XmlName("http://sample.staxer.org/", "radius");

    @XmlAttribute(name = "radius", namespace = "http://sample.staxer.org/")
    private Double radius;

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    @Override
    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
        super.readXmlAttributes(attributes);
        radius = NumberUtils.parseDouble(attributes.get(XML_NAME_RADIUS));
    }

    @Override
    public boolean readXmlContentElement(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        return super.readXmlContentElement(xmlReader);
    }

    @Override
    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        super.writeXmlAttributes(xmlWriter);
        xmlWriter.attribute(XML_NAME_RADIUS, radius);
    }

    @Override
    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        super.writeXmlContent(xmlWriter);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Circle>\n");
        toString(sb);
        sb.append("</Circle>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("<radius>");
        sb.append(radius);
        sb.append("</radius>\n");
    }

}
