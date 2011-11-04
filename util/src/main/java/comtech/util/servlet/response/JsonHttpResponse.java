package comtech.util.servlet.response;

import comtech.util.json.JsonException;
import comtech.util.json.JsonObject;
import comtech.util.json.JsonObjectNull;
import comtech.util.json.JsonUtils;
import comtech.util.servlet.helper.HttpHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 14.01.2010
 * Time: 14:11:39
 */
public class JsonHttpResponse extends AbstractHttpResponse {

    private static Log LOG = LogFactory.getLog(JsonHttpResponse.class);

    private JsonObject jsonObject;
    private Object object;

    public JsonHttpResponse(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        this.contentType = HttpResponseContentType.PLAIN;
    }

    public JsonHttpResponse(JsonObject jsonObject, int expireSeconds) {
        this(jsonObject);
        this.expireSeconds = expireSeconds;
    }

    public JsonHttpResponse(Object object) {
        this.object = object;
        this.contentType = HttpResponseContentType.PLAIN;
    }

    public JsonHttpResponse(Object object, int expireSeconds) {
        this(object);
        this.expireSeconds = expireSeconds;
    }

    @Override
    public void respond(HttpHelper httpHelper) throws IOException, ServletException {
        super.respond(httpHelper);
        httpHelper.setResponseCharacterEncoding("UTF-8");
        try {
            if (object != null) {
                JsonUtils.serialize(object, httpHelper.getResponseWriter(), false);
            } else if (jsonObject != null) {
                JsonUtils.serialize(jsonObject, httpHelper.getResponseWriter());
            } else {
                JsonUtils.serialize(JsonObjectNull.getInstance(), httpHelper.getResponseWriter());
            }
        } catch (JsonException e) {
            LOG.error("", e);
            throw new ServletException(e);
        }
    }
}
