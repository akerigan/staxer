package org.staxer.igniter.log.beans;

import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.StaxerXmlStreamException;
import org.staxer.util.xml.StaxerXmlStreamReader;
import org.staxer.util.xml.StaxerXmlStreamWriter;
import org.staxer.util.xml.XmlName;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class DailyRollingFileAppenderXml extends AppenderXml {

    public static final XmlName XML_NAME_FILE = new XmlName("file");
    public static final XmlName XML_NAME_DATE_PATTERN = new XmlName("datePattern");

    @XmlAttribute(name = "file")
    private String file;

    @XmlAttribute(name = "datePattern")
    private String datePattern;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    @Override
    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
        super.readXmlAttributes(attributes);
        file = attributes.get(XML_NAME_FILE);
        datePattern = attributes.get(XML_NAME_DATE_PATTERN);
    }

    @Override
    public boolean readXmlContentElement(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        return super.readXmlContentElement(xmlReader);
    }

    @Override
    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        super.writeXmlAttributes(xmlWriter);
        xmlWriter.attribute(XML_NAME_FILE, file);
        xmlWriter.attribute(XML_NAME_DATE_PATTERN, datePattern);
    }

    @Override
    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        super.writeXmlContent(xmlWriter);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<DailyRollingFileAppenderXml>\n");
        toString(sb);
        sb.append("</DailyRollingFileAppenderXml>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("<file>");
        sb.append(file);
        sb.append("</file>\n");
        sb.append("<datePattern>");
        sb.append(datePattern);
        sb.append("</datePattern>\n");
    }

}
