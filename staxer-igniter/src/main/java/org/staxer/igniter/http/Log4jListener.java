package org.staxer.igniter.http;

import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerRepository;
import org.staxer.igniter.log.Log4jConfigurator;
import org.staxer.igniter.log.beans.Log4jConfigurationXml;
import org.staxer.util.StringUtils;
import org.staxer.util.xml.StaxerXmlStreamReader;
import org.staxer.util.xml.XmlName;
import org.staxer.util.xml.XmlUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileInputStream;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-18 14:27 (Europe/Moscow)
 */
public class Log4jListener implements ServletContextListener {

    public static final String CONFIG_LOCATION_PARAM = "configLocation";
    protected ServletContext servletContext;

    public void contextInitialized(ServletContextEvent event) {
        servletContext = event.getServletContext();
        String location = StringUtils.notEmptyTrimmedElseNull(servletContext.getInitParameter(CONFIG_LOCATION_PARAM));
        if (location == null) {
            throw new IllegalStateException(
                    "Invalid '" + CONFIG_LOCATION_PARAM + "' parameter: resource location must not be null"
            );
        }
        try {
            FileInputStream inputStream = new FileInputStream(servletContext.getRealPath(location));
            StaxerXmlStreamReader xmlReader = new StaxerXmlStreamReader(inputStream);
            init(xmlReader, event);
            inputStream.close();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void init(
            StaxerXmlStreamReader xmlReader, ServletContextEvent event
    ) throws Exception {
        Log4jConfigurator.configure(
                XmlUtils.readXml(xmlReader, Log4jConfigurationXml.class, new XmlName("log4j"))
        );
    }

    public void contextDestroyed(ServletContextEvent event) {
        try {
            destroy(event);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void destroy(ServletContextEvent event) throws Exception {
        LoggerRepository repository = LogManager.getLoggerRepository();
        repository.shutdown();
    }

}
