package comtech.util.servlet.response;

import comtech.util.servlet.helper.HttpHelper;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 23.10.2009
 * Time: 11:09:20
 */
public abstract class AbstractHttpResponse implements HttpResponse {

    protected HttpResponseContentType contentType = HttpResponseContentType.HTML;
    protected int expireSeconds;

    public HttpResponseContentType getContentType() {
        return contentType;
    }

    public void setContentType(HttpResponseContentType contentType) {
        this.contentType = contentType;
    }

    public int getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(int expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    public void respond(HttpHelper httpHelper) throws IOException, ServletException {
        httpHelper.setResponseContentType(contentType);
        if (expireSeconds == 0) {
            httpHelper.setResponseNoCache();
        } else {
            httpHelper.setDateResponseHeader("Expires", System.currentTimeMillis() + expireSeconds * 1000);
        }

    }
}
