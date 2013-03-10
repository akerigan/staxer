package org.staxer.util.xml.soap;

import org.staxer.util.json.*;
import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-05-04 17:13 (Europe/Moscow)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SoapFaultDetailEntry implements StaxerReadXml, StaxerWriteXml, StaxerReadJson, StaxerWriteJson {

    private static final XmlName XML_NAME_TYPE = new XmlName("type");
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

    public void readJson(StaxerJsonStreamReader jsonReader) throws StaxerJsonStreamException {
        if (jsonReader.readObjectStart()) {
            int rootLevel = jsonReader.getLevel();
            while (jsonReader.readNext() && !jsonReader.objectEnded(rootLevel)) {
                String fieldName = jsonReader.readFieldName(rootLevel);
                if ("type".equals(fieldName)) {
                    if (jsonReader.readFieldValue(rootLevel)) {
                        type = jsonReader.getStringValue();
                    }
                } else if ("value".equals(fieldName)) {
                    if (jsonReader.readFieldValue(rootLevel)) {
                        value = jsonReader.getStringValue();
                    }
                }
            }
        }
    }

    public void writeJson(StaxerJsonStreamWriter jsonWriter) throws StaxerJsonStreamException {
        jsonWriter.startObject();
        jsonWriter.objectEntry("type", type);
        jsonWriter.objectEntry("value", value);
        jsonWriter.endObject();
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
