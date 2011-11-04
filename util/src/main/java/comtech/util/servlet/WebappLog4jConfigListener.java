package comtech.util.servlet;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.util.ResourceUtils;
import org.springframework.web.util.Log4jWebConfigurer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileNotFoundException;

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

    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();

        // Only perform custom log4j initialization in case of a config file.
        String location = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
        if (location != null) {
            try {
                PropertyConfigurator.configure(ResourceUtils.getURL(servletContext.getRealPath(location)));
            } catch (FileNotFoundException ex) {
                throw new IllegalArgumentException("Invalid 'log4jConfigLocation' parameter: " + ex.getMessage());
            }
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        Log4jWebConfigurer.shutdownLogging(event.getServletContext());
    }

}

