package comtech.staxer.domain.wss;

import comtech.util.props.StringMapProperties;
import comtech.util.xml.ReadXml;
import comtech.util.xml.WriteXml;
import comtech.util.xml.XmlName;
import comtech.util.xml.read.DocumentXmlStreamReader2;
import comtech.util.xml.write.DocumentXmlStreamWriter2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 12.11.2009
 * Time: 16:58:17
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WssPassword implements ReadXml, WriteXml {

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

    public void readXml(DocumentXmlStreamReader2 reader, XmlName elementName) throws XMLStreamException {
        StringMapProperties attributes = reader.getAttributes();
        type = attributes.get("Type");
        value = reader.readCharacters(elementName);
    }

    public void writeXml(DocumentXmlStreamWriter2 writer, XmlName elementName) throws IOException {
        writer.startElement(elementName);
        writer.attribute(new XmlName("Type"), type);
        writer.text(value);
        writer.endElement();
    }

}
