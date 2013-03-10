package org.staxer.igniter.log.beans;

import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;

@XmlAccessorType(XmlAccessType.FIELD)
public class Log4jConfigurationXml implements StaxerReadXml, StaxerWriteXml {

    public static final XmlName XML_NAME_PATTERN_LAYOUT = new XmlName("patternLayout");
    public static final XmlName XML_NAME_CONSOLE_APPENDER = new XmlName("consoleAppender");
    public static final XmlName XML_NAME_DAILY_ROLLING_FILE_APPENDER = new XmlName("dailyRollingFileAppender");
    public static final XmlName XML_NAME_LOGGER = new XmlName("logger");

    @XmlElement(name = "patternLayout")
    private ArrayList<PatternLayoutXml> patternLayout = new ArrayList<PatternLayoutXml>();

    @XmlElement(name = "consoleAppender")
    private ArrayList<ConsoleAppenderXml> consoleAppender = new ArrayList<ConsoleAppenderXml>();

    @XmlElement(name = "dailyRollingFileAppender")
    private ArrayList<DailyRollingFileAppenderXml> dailyRollingFileAppender = new ArrayList<DailyRollingFileAppenderXml>();

    @XmlElement(name = "logger")
    private ArrayList<LoggerXml> logger = new ArrayList<LoggerXml>();

    public ArrayList<PatternLayoutXml> getPatternLayout() {
        return patternLayout;
    }

    public ArrayList<ConsoleAppenderXml> getConsoleAppender() {
        return consoleAppender;
    }

    public ArrayList<DailyRollingFileAppenderXml> getDailyRollingFileAppender() {
        return dailyRollingFileAppender;
    }

    public ArrayList<LoggerXml> getLogger() {
        return logger;
    }

    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
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
        if (xmlReader.elementStarted(XML_NAME_PATTERN_LAYOUT)) {
            PatternLayoutXml patternLayoutItem = XmlUtils.readXml(xmlReader, PatternLayoutXml.class, XML_NAME_PATTERN_LAYOUT, false);
            if (patternLayoutItem != null) {
                patternLayout.add(patternLayoutItem);
            }
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_CONSOLE_APPENDER)) {
            ConsoleAppenderXml consoleAppenderItem = XmlUtils.readXml(xmlReader, ConsoleAppenderXml.class, XML_NAME_CONSOLE_APPENDER, false);
            if (consoleAppenderItem != null) {
                consoleAppender.add(consoleAppenderItem);
            }
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_DAILY_ROLLING_FILE_APPENDER)) {
            DailyRollingFileAppenderXml dailyRollingFileAppenderItem = XmlUtils.readXml(xmlReader, DailyRollingFileAppenderXml.class, XML_NAME_DAILY_ROLLING_FILE_APPENDER, false);
            if (dailyRollingFileAppenderItem != null) {
                dailyRollingFileAppender.add(dailyRollingFileAppenderItem);
            }
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_LOGGER)) {
            LoggerXml loggerItem = XmlUtils.readXml(xmlReader, LoggerXml.class, XML_NAME_LOGGER, false);
            if (loggerItem != null) {
                logger.add(loggerItem);
            }
            return true;
        }
        return false;
    }

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
    }

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        if (!patternLayout.isEmpty()) {
            for (PatternLayoutXml patternLayoutItem : patternLayout) {
                XmlUtils.writeXmlElement(xmlWriter, XML_NAME_PATTERN_LAYOUT, patternLayoutItem, false);
            }
        }
        if (!consoleAppender.isEmpty()) {
            for (ConsoleAppenderXml consoleAppenderItem : consoleAppender) {
                XmlUtils.writeXmlElement(xmlWriter, XML_NAME_CONSOLE_APPENDER, consoleAppenderItem, false);
            }
        }
        if (!dailyRollingFileAppender.isEmpty()) {
            for (DailyRollingFileAppenderXml dailyRollingFileAppenderItem : dailyRollingFileAppender) {
                XmlUtils.writeXmlElement(xmlWriter, XML_NAME_DAILY_ROLLING_FILE_APPENDER, dailyRollingFileAppenderItem, false);
            }
        }
        if (!logger.isEmpty()) {
            for (LoggerXml loggerItem : logger) {
                XmlUtils.writeXmlElement(xmlWriter, XML_NAME_LOGGER, loggerItem, false);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Log4jConfigurationXml>\n");
        toString(sb);
        sb.append("</Log4jConfigurationXml>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        if (patternLayout != null) {
            sb.append("<patternLayout>");
            for (Object obj : patternLayout) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</patternLayout>\n");
        } else {
            sb.append("<patternLayout/>\n");
        }
        if (consoleAppender != null) {
            sb.append("<consoleAppender>");
            for (Object obj : consoleAppender) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</consoleAppender>\n");
        } else {
            sb.append("<consoleAppender/>\n");
        }
        if (dailyRollingFileAppender != null) {
            sb.append("<dailyRollingFileAppender>");
            for (Object obj : dailyRollingFileAppender) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</dailyRollingFileAppender>\n");
        } else {
            sb.append("<dailyRollingFileAppender/>\n");
        }
        if (logger != null) {
            sb.append("<logger>");
            for (Object obj : logger) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</logger>\n");
        } else {
            sb.append("<logger/>\n");
        }
    }

}
