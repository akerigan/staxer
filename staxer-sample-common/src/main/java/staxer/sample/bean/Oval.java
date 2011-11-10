package staxer.sample.bean;

import comtech.util.NumberUtils;
import comtech.util.props.StringMapProperties;
import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Oval extends Circle implements StaxerXmlReader, StaxerXmlWriter {

    public static final XmlName XML_NAME_SECOND_RADIUS = new XmlName("http://staxer.sample/", "secondRadius");

    @XmlElement(name = "secondRadius", namespace = "http://staxer.sample/")
    private Double secondRadius;

    public Double getSecondRadius() {
        return secondRadius;
    }

    public void setSecondRadius(Double secondRadius) {
        this.secondRadius = secondRadius;
    }

    public void readXmlAttributes(
            StringMapProperties attributes
    ) throws StaxerXmlStreamException {
        super.readXmlAttributes(attributes);
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        super.readXmlContent(xmlReader);
        XmlName rootElementName = xmlReader.getLastStartedElement();
        while (xmlReader.readNext()) {
            if (xmlReader.elementEnded(rootElementName)) {
                break;
            } else if (xmlReader.elementStarted(XML_NAME_SECOND_RADIUS)) {
                secondRadius = NumberUtils.parseDouble(xmlReader.readCharacters(XML_NAME_SECOND_RADIUS));
            }
        }
    }

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        super.writeXmlAttributes(xmlWriter);
    }

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        super.writeXmlContent(xmlWriter);
        xmlWriter.element(XML_NAME_SECOND_RADIUS, secondRadius, false);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Oval>\n");
        toString(sb);
        sb.append("</Oval>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("<secondRadius>");
        sb.append(secondRadius);
        sb.append("</secondRadius>\n");
    }

}
