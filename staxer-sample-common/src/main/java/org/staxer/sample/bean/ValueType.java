package org.staxer.sample.bean;

import org.staxer.util.StringUtils;
import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class ValueType implements StaxerReadXml, StaxerWriteXml {

    public static final XmlName XML_NAME_ATT_BOOLEAN = new XmlName("http://sample.staxer.org/", "attBoolean");

    @XmlValue
    private EnumType value;

    @XmlAttribute(name = "attBoolean", namespace = "http://sample.staxer.org/")
    private Boolean attBoolean;

    public EnumType getValue() {
        return value;
    }

    public void setValue(EnumType value) {
        this.value = value;
    }

    public Boolean getAttBoolean() {
        return attBoolean;
    }

    public void setAttBoolean(Boolean attBoolean) {
        this.attBoolean = attBoolean;
    }

    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
        attBoolean = StringUtils.parseBooleanInstance(attributes.get(XML_NAME_ATT_BOOLEAN));
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        value = EnumType.getByCode(xmlReader.readCharacters());
    }

    public boolean readXmlContentElement(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        return false;
    }

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        xmlWriter.attribute(XML_NAME_ATT_BOOLEAN, attBoolean);
    }

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        if (value != null) {
            xmlWriter.text(value.getCode());
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<ValueType>\n");
        toString(sb);
        sb.append("</ValueType>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        sb.append("<value>");
        sb.append(value);
        sb.append("</value>\n");
        sb.append("<attBoolean>");
        sb.append(attBoolean);
        sb.append("</attBoolean>\n");
    }

}
