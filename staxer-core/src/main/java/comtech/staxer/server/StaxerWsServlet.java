package comtech.staxer.server;

import comtech.staxer.StaxerException;
import comtech.util.servlet.helper.HttpHelper;
import comtech.util.xml.XmlConstants;
import comtech.util.xml.XmlUtils;
import comtech.util.xml.soap.SoapFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2010-12-16 15:13 (Europe/Moscow)
 */
public class StaxerWsServlet extends HttpServlet {

    protected ServletContext servletContext;
    protected WebApplicationContext applicationContext;
    protected Map<String, WsMessageProcessor> processors;

    private static Logger log = LoggerFactory.getLogger(StaxerWsServlet.class);

    @Override
    public void init() throws ServletException {
        servletContext = getServletContext();
        applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        Map<String, WsMessageProcessor> messageProcessorMap = applicationContext.getBeansOfType(WsMessageProcessor.class);
        if (messageProcessorMap.size() == 0) {
            throw new IllegalStateException("Ws message processor not found in application context");
        }
        processors = new HashMap<String, WsMessageProcessor>();
        for (WsMessageProcessor wsMessageProcessor : messageProcessorMap.values()) {
            processors.put(wsMessageProcessor.getServletPath(), wsMessageProcessor);
        }
    }

    @Override
    protected void service(
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {
        HttpHelper httpHelper = new HttpHelper(servletContext, request, response);
        String servletPath = httpHelper.getServletPath().substring(1);
        WsMessageProcessor processor = processors.get(servletPath);
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
