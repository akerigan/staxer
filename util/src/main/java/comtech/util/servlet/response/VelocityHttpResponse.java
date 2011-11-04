package comtech.util.servlet.response;

import comtech.util.servlet.helper.HttpHelper;
import comtech.util.velocity.MiscTool;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.NumberTool;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Writer;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 15.01.2010
 * Time: 15:37:19
 */
public class VelocityHttpResponse extends AbstractHttpResponse {

    private String templateLocation;
    private VelocityEngine velocityEngine;
    private Writer resultWriter;

    private static MiscTool miscTool = new MiscTool();

    public VelocityHttpResponse(String templateLocation) {
        this(templateLocation, HttpResponseContentType.HTML, 0);
    }

    public VelocityHttpResponse(String templateLocation, HttpResponseContentType contentType) {
        this(templateLocation, contentType, 0);
    }

    public VelocityHttpResponse(String templateLocation, HttpResponseContentType contentType, int expireSeconds) {
        this.templateLocation = templateLocation;
        this.contentType = contentType;
        this.expireSeconds = expireSeconds;
    }

    public String getTemplateLocation() {
        return templateLocation;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public Writer getResultWriter() {
        return resultWriter;
    }

    public void setResultWriter(Writer resultWriter) {
        this.resultWriter = resultWriter;
    }

    @Override
    public void respond(HttpHelper httpHelper) throws IOException, ServletException {
        super.respond(httpHelper);
        Context context = new VelocityContext();

        for (String attributeName : httpHelper.getRequestAttributesNames()) {
            context.put(attributeName, httpHelper.getRequestAttribute(attributeName));
        }

        for (String attributeName : httpHelper.getSessionAttributesNames()) {
            context.put(attributeName, httpHelper.getSessionAttribute(attributeName));
        }

        for (String attributeName : httpHelper.getApplicationAttributesNames()) {
            context.put(attributeName, httpHelper.getApplicationAttribute(attributeName));
        }

        context.put("httpHelper", httpHelper.getRequestParametersMap());
        context.put("requestURI", httpHelper.getRequestURI());
        context.put("contextPath", httpHelper.getContextPath());
        context.put("dateTool", new DateTool());
        context.put("numberTool", new NumberTool());
        context.put("miscTool", miscTool);

        Writer writer;
        if (resultWriter != null) {
            writer = resultWriter;
        } else {
            writer = httpHelper.getResponseWriter();
        }
        try {
            velocityEngine.getTemplate(templateLocation).merge(context, writer);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
