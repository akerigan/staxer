package org.staxer.util.http.response;

import org.staxer.util.http.helper.HttpHelper;
import org.staxer.util.json.JsonException;
import org.staxer.util.json.JsonObject;
import org.staxer.util.json.JsonObjectNull;
import org.staxer.util.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 14.01.2010
 * Time: 14:11:39
 */
public class JsonHttpResponse extends AbstractHttpResponse {

    private static Logger LOG = LoggerFactory.getLogger(JsonHttpResponse.class);

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
