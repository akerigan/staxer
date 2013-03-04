package org.staxer.sample.bean;

import org.staxer.util.NumberUtils;
import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.StaxerXmlStreamException;
import org.staxer.util.xml.StaxerXmlStreamReader;
import org.staxer.util.xml.StaxerXmlStreamWriter;
import org.staxer.util.xml.XmlName;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Oval extends Circle {

    public static final XmlName XML_NAME_SECOND_RADIUS = new XmlName("http://sample.staxer.org/", "secondRadius");

    @XmlElement(name = "secondRadius", namespace = "http://sample.staxer.org/")
    private Double secondRadius;

    public Double getSecondRadius() {
        return secondRadius;
    }

    public void setSecondRadius(Double secondRadius) {
        this.secondRadius = secondRadius;
    }

    @Override
    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
        super.readXmlAttributes(attributes);
    }

    @Override
    public boolean readXmlContentElement(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        if (xmlReader.elementStarted(XML_NAME_SECOND_RADIUS)) {
            secondRadius = NumberUtils.parseDouble(xmlReader.readCharacters(XML_NAME_SECOND_RADIUS));
            return true;
        }
        return super.readXmlContentElement(xmlReader);
    }

    @Override
    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        super.writeXmlAttributes(xmlWriter);
    }

    @Override
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
