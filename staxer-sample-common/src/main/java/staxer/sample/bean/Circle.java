package staxer.sample.bean;

import comtech.util.NumberUtils;
import comtech.util.props.StringMapProperties;
import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Circle extends Point implements StaxerXmlReader, StaxerXmlWriter {

    public static final XmlName XML_NAME_RADIUS = new XmlName("http://staxer.sample/", "radius");

    @XmlAttribute(name = "radius", namespace = "http://staxer.sample/")
    private Double radius;

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public void readXmlAttributes(
            StringMapProperties attributes
    ) throws StaxerXmlStreamException {
        super.readXmlAttributes(attributes);
        radius = NumberUtils.parseDouble(attributes.get(XML_NAME_RADIUS.toString()));
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        super.readXmlContent(xmlReader);
    }

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        super.writeXmlAttributes(xmlWriter);
        xmlWriter.attribute(XML_NAME_RADIUS, radius);
    }

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
