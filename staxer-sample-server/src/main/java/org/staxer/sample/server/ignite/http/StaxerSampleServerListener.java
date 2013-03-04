package org.staxer.sample.server.ignite.http;

import org.staxer.igniter.http.Log4jListener;
import org.staxer.util.StringUtils;
import org.staxer.util.xml.StaxerXmlStreamReader;
import org.staxer.sample.server.ignite.StaxerSampleServerServices;

import javax.servlet.ServletContextEvent;

import static org.staxer.util.staxer.server.StaxerWsServlet.CONTEXT_CONTAINER_NAME;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-18 15:43 (Europe/Moscow)
 */
public class StaxerSampleServerListener extends Log4jListener {

    private StaxerSampleServerServices services;

    @Override
    protected void init(StaxerXmlStreamReader xmlReader, ServletContextEvent event) throws Exception {
        super.init(xmlReader, event);
        services = new StaxerSampleServerServices();
        String containerName = StringUtils.notEmptyTrimmedElseNull(
                servletContext.getInitParameter(CONTEXT_CONTAINER_NAME)
        );
        if (containerName == null) {
            throw new IllegalStateException(
                    "Invalid '" + CONTEXT_CONTAINER_NAME + "' parameter: resource location must not be null"
            );
        }
        servletContext.setAttribute(containerName, services);
    }

    @Override
    protected void destroy(ServletContextEvent event) throws Exception {
        super.destroy(event);
        if (services != null) {
            services.shutDown();
        }
    }
}
