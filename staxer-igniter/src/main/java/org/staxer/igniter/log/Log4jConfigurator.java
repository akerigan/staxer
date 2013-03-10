package org.staxer.igniter.log;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggerRepository;
import org.staxer.igniter.log.beans.*;
import org.staxer.util.StringUtils;

import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-17 14:05 (Europe/Moscow)
 */
public class Log4jConfigurator {

    public static void configure(Log4jConfigurationXml configuration) {
        Layout defaultLayout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss,SSS} [%-5p] %l%n%m%n");
        ConsoleAppender defaultAppender = new ConsoleAppender(defaultLayout, ConsoleAppender.SYSTEM_OUT);
//        defaultAppender.setFollow(true);
//        defaultAppender.activateOptions();
        defaultAppender.setWriter(new OutputStreamWriter(System.out));
        LoggerRepository repository = LogManager.getLoggerRepository();
        Logger rootLogger = null;
        if (configuration != null) {
            Map<String, Layout> layoutMap = new HashMap<String, Layout>();
            for (PatternLayoutXml layoutXml : configuration.getPatternLayout()) {
                String name = layoutXml.getName();
                String pattern = layoutXml.getConversionPattern();
                if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(pattern)) {
                    layoutMap.put(name, new PatternLayout(pattern));
                }
            }

            Map<String, Appender> appenderMap = new HashMap<String, Appender>();
            for (ConsoleAppenderXml appenderXml : configuration.getConsoleAppender()) {
                String name = appenderXml.getName();
                if (!StringUtils.isEmpty(name)) {
                    ConsoleAppender appender = new ConsoleAppender();
                    appender.setName(name);
                    String target = appenderXml.getTarget();
                    if (ConsoleAppender.SYSTEM_OUT.equals(target)) {
                        appender.setWriter(new OutputStreamWriter(System.out));
                    } else if (ConsoleAppender.SYSTEM_ERR.equals(target)) {
                        appender.setWriter(new OutputStreamWriter(System.err));
                    } else {
                        appender.setWriter(new OutputStreamWriter(System.out));
                    }
                    Layout layout = layoutMap.get(appenderXml.getLayout());
                    if (layout == null) {
                        layout = defaultLayout;
                    }
                    appender.setLayout(layout);
                    appenderMap.put(name, appender);
                }
            }
            for (DailyRollingFileAppenderXml appenderXml : configuration.getDailyRollingFileAppender()) {
                String name = appenderXml.getName();
                String file = appenderXml.getFile();
                if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(file)) {
                    DailyRollingFileAppender appender = new DailyRollingFileAppender();
                    appender.setName(name);
                    appender.setFile(file);
                    String datePattern = appenderXml.getDatePattern();
                    if (StringUtils.isEmpty(datePattern)) {
                        appender.setDatePattern("'.'yyyy-MM-dd");
                    } else {
                        appender.setDatePattern(datePattern);
                    }
                    Layout layout = layoutMap.get(appenderXml.getLayout());
                    if (layout == null) {
                        layout = defaultLayout;
                    }
                    appender.setLayout(layout);
                    appender.activateOptions();
                    appenderMap.put(name, appender);
                }
            }
            for (LoggerXml loggerXml : configuration.getLogger()) {
                String name = loggerXml.getName();
                Logger logger;
                if (StringUtils.isEmpty(name)) {
                    if (rootLogger == null) {
                        logger = repository.getRootLogger();
                        rootLogger = logger;
                    } else {
                        continue;
                    }
                } else {
                    logger = repository.getLogger(name);
                }
                LogLevelXml levelXml = loggerXml.getLevel();
                if (levelXml == null) {
                    logger.setLevel(Level.INFO);
                } else {
                    switch (levelXml) {
                        case FATAL:
                            logger.setLevel(Level.FATAL);
                            break;
                        case ERROR:
                            logger.setLevel(Level.ERROR);
                            break;
                        case WARN:
                            logger.setLevel(Level.WARN);
                            break;
                        case INFO:
                            logger.setLevel(Level.INFO);
                            break;
                        case DEBUG:
                            logger.setLevel(Level.DEBUG);
                            break;
                    }
                }
                int appendersAdded = 0;
                for (String appenderName : loggerXml.getAppender()) {
                    Appender appender = appenderMap.get(appenderName);
                    if (appender != null && logger.getAppender(appender.getName()) == null) {
                        logger.addAppender(appender);
                        appendersAdded += 1;
                    }
                }
                if (appendersAdded == 0) {
                    logger.addAppender(defaultAppender);
                }
            }
        }
        if (rootLogger == null) {
            rootLogger = repository.getRootLogger();
            rootLogger.removeAllAppenders();
            rootLogger.setLevel(Level.INFO);
            rootLogger.addAppender(defaultAppender);
        }
    }

}
