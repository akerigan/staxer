package org.staxer.util.staxer.domain;

import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 12.11.2009
 * Time: 16:58:17
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WssPassword implements StaxerReadXml, StaxerWriteXml {

    public static final XmlName XML_NAME_TYPE = new XmlName("Type");

    @XmlAttribute(name = "Type")
    private String type;

    @XmlValue
    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void readXmlAttributes(XmlNameMapProperties attributes) throws StaxerXmlStreamException {
        type = attributes.get(XML_NAME_TYPE);
    }

    public void readXmlContent(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        value = xmlReader.readCharacters();
    }

    public void writeXmlAttributes(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        xmlWriter.attribute(XML_NAME_TYPE, type);
    }

    public void writeXmlContent(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        xmlWriter.text(value);
    }

}
