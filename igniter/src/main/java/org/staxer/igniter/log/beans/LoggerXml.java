package org.staxer.igniter.log.beans;

import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;

@XmlAccessorType(XmlAccessType.FIELD)
public class LoggerXml implements StaxerReadXml, StaxerWriteXml {

    public static final XmlName XML_NAME_APPENDER = new XmlName("appender");
    public static final XmlName XML_NAME_NAME = new XmlName("name");
    public static final XmlName XML_NAME_LEVEL = new XmlName("level");

    @XmlElement(name = "appender")
    private ArrayList<String> appender = new ArrayList<String>();

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "level")
    private LogLevelXml level;

    public ArrayList<String> getAppender() {
        return appender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LogLevelXml getLevel() {
        return level;
    }

    public void setLevel(LogLevelXml level) {
        this.level = level;
    }

    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
        name = attributes.get(XML_NAME_NAME);
        level = LogLevelXml.getByCode(attributes.get(XML_NAME_LEVEL));
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        XmlName rootElementName = xmlReader.getLastStartedElement();
        while (xmlReader.readNext() && !xmlReader.elementEnded(rootElementName)) {
            readXmlContentElement(xmlReader);
        }
    }

    public boolean readXmlContentElement(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        if (xmlReader.elementStarted(XML_NAME_APPENDER)) {
            String appenderItem = xmlReader.readCharacters(XML_NAME_APPENDER);
            if (appenderItem != null) {
                appender.add(appenderItem);
            }
            return true;
        }
        return false;
    }

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        xmlWriter.attribute(XML_NAME_NAME, name);
        if (level != null) {
            xmlWriter.attribute(XML_NAME_LEVEL, level.getCode());
        }
    }

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        if (!appender.isEmpty()) {
            for (String appenderItem : appender) {
                xmlWriter.element(XML_NAME_APPENDER, appenderItem, false);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<LoggerXml>\n");
        toString(sb);
        sb.append("</LoggerXml>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        if (appender != null) {
            sb.append("<appender>");
            for (Object obj : appender) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</appender>\n");
        } else {
            sb.append("<appender/>\n");
        }
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<level>");
        sb.append(level);
        sb.append("</level>\n");
    }

}
