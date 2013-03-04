package org.staxer.sample.bean;

import org.staxer.util.NumberUtils;
import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Point implements StaxerReadXml, StaxerWriteXml {

    public static final XmlName XML_NAME_X = new XmlName("http://sample.staxer.org/", "x");
    public static final XmlName XML_NAME_Y = new XmlName("http://sample.staxer.org/", "y");

    @XmlElement(name = "x", namespace = "http://sample.staxer.org/")
    private Double x;

    @XmlElement(name = "y", namespace = "http://sample.staxer.org/")
    private Double y;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
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
        if (xmlReader.elementStarted(XML_NAME_X)) {
            x = NumberUtils.parseDouble(xmlReader.readCharacters(XML_NAME_X));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_Y)) {
            y = NumberUtils.parseDouble(xmlReader.readCharacters(XML_NAME_Y));
            return true;
        }
        return false;
    }

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
    }

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        xmlWriter.element(XML_NAME_X, x, false);
        xmlWriter.element(XML_NAME_Y, y, false);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Point>\n");
        toString(sb);
        sb.append("</Point>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        sb.append("<x>");
        sb.append(x);
        sb.append("</x>\n");
        sb.append("<y>");
        sb.append(y);
        sb.append("</y>\n");
    }

}
