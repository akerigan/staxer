package comtech.util.staxer.server;

import comtech.util.staxer.StaxerException;
import comtech.util.StringUtils;
import comtech.util.http.helper.HttpHelper;
import comtech.util.xml.XmlConstants;
import comtech.util.xml.XmlUtils;
import comtech.util.xml.soap.SoapFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2010-12-16 15:13 (Europe/Moscow)
 */
public class StaxerWsServlet extends HttpServlet {

    public static final String CONTEXT_CONTAINER_NAME = "wsProcessorContainerName";

    protected ServletContext servletContext;
    protected WsMessageProcessorsContainer wsMessageProcessorsContainer;

    private static Logger log = LoggerFactory.getLogger(StaxerWsServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        servletContext = config.getServletContext();
        String containerName = servletContext.getInitParameter(CONTEXT_CONTAINER_NAME);
        if (StringUtils.isEmpty(containerName)) {
            throw new IllegalStateException("'" + CONTEXT_CONTAINER_NAME + "' servlet init param not set");
        }
        wsMessageProcessorsContainer = (WsMessageProcessorsContainer) servletContext.getAttribute(containerName);
        if (wsMessageProcessorsContainer == null) {
            throw new IllegalStateException(containerName + ": ws message processors container not found in servlet context");
        }
    }

    @Override
    protected void service(
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {
        HttpHelper httpHelper = new HttpHelper(servletContext, request, response);
        String servletPath = httpHelper.getServletPath().substring(1);
        WsMessageProcessor processor = wsMessageProcessorsContainer.getWsMessageProcessor(servletPath);
        ServletOutputStream responseOutputStream = httpHelper.getResponseOutputStream();
        if (processor != null) {
            try {
                processor.process(httpHelper, responseOutputStream);
            } catch (Exception e) {
                log.error("", e);
                SoapFault soapFault = null;
                if (e instanceof StaxerException) {
                    soapFault = ((StaxerException) e).getSoapFault();
                }
                if (soapFault == null) {
                    soapFault = new SoapFault("env:Server", "Internal server error");
                }
                try {
                    XmlUtils.writeSoapEnvelopedElement(
                            responseOutputStream, "UTF-8", 2, soapFault,
                            XmlConstants.XML_NAME_SOAP_ENVELOPE_FAULT
                    );
                } catch (Exception se) {
                    log.error("", se);
                    throw new ServletException("Internal server error");
                }
            }
        } else {
            String message = "Ws processor not found for path: " + servletPath;
            log.error(message);
            try {
                XmlUtils.writeSoapEnvelopedElement(
                        responseOutputStream, "UTF-8", 2, new SoapFault("env:Server", message),
                        XmlConstants.XML_NAME_SOAP_ENVELOPE_FAULT
                );
            } catch (Exception e) {
                log.error("", e);
                throw new ServletException("Internal server error");
            }
        }
    }
}
