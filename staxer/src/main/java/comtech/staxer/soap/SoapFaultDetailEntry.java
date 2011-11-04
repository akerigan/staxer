package comtech.staxer.soap;

import comtech.util.props.StringMapProperties;
import comtech.util.xml.ReadXml;
import comtech.util.xml.XmlName;
import comtech.util.xml.read.DocumentXmlStreamReader2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.stream.XMLStreamException;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-05-04 17:13 (Europe/Moscow)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SoapFaultDetailEntry implements ReadXml {

    @XmlAttribute(name = "type")
    private String type;
    @XmlValue
    private String value;

    public SoapFaultDetailEntry() {
    }

    public SoapFaultDetailEntry(String type, String value) {
        this.type = type;
        this.value = value;
    }

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
        type = attributes.get("type");
        value = reader.readCharacters(elementName);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<SoapFaultDetailEntry>\n");
        sb.append("<type>");
        sb.append(type);
        sb.append("</type>\n");
        sb.append("<value>");
        sb.append(value);
        sb.append("</value>\n");
        sb.append("</SoapFaultDetailEntry>\n");

        return sb.toString();
    }
}
