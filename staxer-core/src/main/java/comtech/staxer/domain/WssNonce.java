package comtech.staxer.domain;

import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 12.11.2009
 * Time: 16:58:58
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WssNonce implements StaxerReadXml, StaxerWriteXml {

    private static final XmlName XML_NAME_ENCODING_TYPE = new XmlName("EncodingType");

    @XmlAttribute(name = "EncodingType")
    private String encodingType;

    @XmlValue
    private String value;

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void readXmlAttributes(XmlNameMapProperties attributes) throws StaxerXmlStreamException {
        encodingType = attributes.get(XML_NAME_ENCODING_TYPE);
    }

    public void readXmlContent(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        value = xmlReader.readCharacters();
    }

    public void writeXmlAttributes(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        xmlWriter.attribute(XML_NAME_ENCODING_TYPE, encodingType);
    }

    public void writeXmlContent(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        xmlWriter.text(value);
    }

}
