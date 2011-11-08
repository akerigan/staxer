package comtech.staxer.domain;

import comtech.util.props.StringMapProperties;
import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 12.11.2009
 * Time: 16:58:58
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WssNonce implements ReadXml, WriteXml {

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

    public void readXml(XmlStreamReader reader, XmlName elementName) throws XMLStreamException {
        StringMapProperties attributes = reader.getAttributes();
        encodingType = attributes.get("EncodingType");
        value = reader.readCharacters(elementName);
    }

    public void writeXml(XmlStreamWriter writer, XmlName elementName) throws IOException {
        writer.startElement(elementName);
        writer.attribute(new XmlName("EncodingType"), encodingType);
        writer.text(value);
        writer.endElement();
    }


}
