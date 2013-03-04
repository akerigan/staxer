package org.staxer.util.http;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.util.ClassUtils;
import org.springframework.web.util.Log4jWebConfigurer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 19.04.2010
 * Time: 17:34:32
 */
public class WebappLog4jConfigListener implements ServletContextListener {

    /**
     * Parameter specifying the location of the log4j config file
     */
    public static final String CONFIG_LOCATION_PARAM = "log4jConfigLocation";

    /**
     * Pseudo URL prefix for loading from the class path: "classpath:"
     */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    public static final String EXCEPTION_PREFIX =
            "Invalid 'log4jConfigLocation' parameter: ";

    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();

        // Only perform custom log4j initialization in case of a config file.
        String location = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
        if (location != null) {
            String resourceLocation = servletContext.getRealPath(location);
            if (resourceLocation == null) {
                throw new IllegalArgumentException(EXCEPTION_PREFIX + "resource location must not be null");
            }
            URL url;
            if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
                String path = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
                url = ClassUtils.getDefaultClassLoader().getResource(path);
                if (url == null) {
                    throw new IllegalArgumentException(
                            EXCEPTION_PREFIX + "class path resource [" + path + "]" +
                            " cannot be resolved to URL because it does not exist"
                    );
                }
            } else {
                try {
                    // try URL
                    url = new URL(resourceLocation);
                } catch (MalformedURLException ex) {
                    // no URL -> treat as file path
                    try {
                        url = new File(resourceLocation).toURI().toURL();
                    } catch (MalformedURLException ex2) {
                        throw new IllegalArgumentException(
                                "Resource location [" + resourceLocation +
                                "] is neither a URL not a well-formed file path");
                    }
                }
            }
            PropertyConfigurator.configure(url);
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        Log4jWebConfigurer.shutdownLogging(event.getServletContext());
    }

}

