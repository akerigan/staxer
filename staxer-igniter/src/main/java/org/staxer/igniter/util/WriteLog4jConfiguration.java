package org.staxer.igniter.util;

import org.staxer.igniter.log.beans.*;
import org.staxer.util.xml.StaxerXmlStreamException;
import org.staxer.util.xml.XmlName;
import org.staxer.util.xml.XmlUtils;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-17 15:27 (Europe/Moscow)
 */
public class WriteLog4jConfiguration {

    public static void main(String[] args) throws StaxerXmlStreamException {
        Log4jConfigurationXml configuration = new Log4jConfigurationXml();
        PatternLayoutXml patternLayout = new PatternLayoutXml();
        patternLayout.setName("patternLayout");
        patternLayout.setConversionPattern("%d{yyyy-MM-dd HH:mm:ss,SSS} [%-5p] %l%n%m%n");
        configuration.getPatternLayout().add(patternLayout);
        ConsoleAppenderXml consoleAppender = new ConsoleAppenderXml();
        consoleAppender.setName("stdout");
        consoleAppender.setTarget("System.out");
        consoleAppender.setLayout("patternLayout");
        configuration.getConsoleAppender().add(consoleAppender);
        DailyRollingFileAppenderXml fileAppender = new DailyRollingFileAppenderXml();
        fileAppender.setName("R");
        fileAppender.setLayout("patternLayout");
        fileAppender.setFile("${catalina.home}/logs/swc.log");
        fileAppender.setDatePattern("'.'yyyy-MM-dd");
        configuration.getDailyRollingFileAppender().add(fileAppender);
        LoggerXml loggerXml = new LoggerXml();
        loggerXml.setLevel(LogLevelXml.INFO);
        loggerXml.getAppender().add("stdout");
        loggerXml.getAppender().add("R");
        configuration.getLogger().add(loggerXml);
        loggerXml = new LoggerXml();
        loggerXml.setName("org.staxer.util");
        loggerXml.setLevel(LogLevelXml.INFO);
        loggerXml.getAppender().add("stdout");
        loggerXml.getAppender().add("R");
        configuration.getLogger().add(loggerXml);
        XmlUtils.writeXml(System.out, "UTF-8", 4, configuration, new XmlName("log4j"));
    }

}
