package staxer.sample.bean;

import comtech.util.NumberUtils;
import comtech.util.props.StringMapProperties;
import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Point implements StaxerXmlReader, StaxerXmlWriter {

    public static final XmlName XML_NAME_X = new XmlName("http://staxer.sample/", "x");
    public static final XmlName XML_NAME_Y = new XmlName("http://staxer.sample/", "y");

    @XmlElement(name = "x", namespace = "http://staxer.sample/")
    private Double x;

    @XmlElement(name = "y", namespace = "http://staxer.sample/")
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
            StringMapProperties attributes
    ) throws StaxerXmlStreamException {
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        XmlName rootElementName = xmlReader.getLastStartedElement();
        while (xmlReader.readNext()) {
            if (xmlReader.elementEnded(rootElementName)) {
                break;
            } else if (xmlReader.elementStarted(XML_NAME_X)) {
                x = NumberUtils.parseDouble(xmlReader.readCharacters(XML_NAME_X));
            } else if (xmlReader.elementStarted(XML_NAME_Y)) {
                y = NumberUtils.parseDouble(xmlReader.readCharacters(XML_NAME_Y));
            }
        }
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
