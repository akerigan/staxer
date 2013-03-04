package org.staxer.util.http.response;

import org.staxer.util.http.helper.HttpHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 23.10.2009
 * Time: 10:28:18
 */
public class ForwardHttpResponse extends AbstractHttpResponse {

    private String jspLocation;
    private HttpServletResponse response;

    public ForwardHttpResponse(String jspLocation) {
        this(jspLocation, HttpResponseContentType.HTML, 0);
    }

    public ForwardHttpResponse(String jspLocation, HttpResponseContentType contentType) {
        this(jspLocation, contentType, 0);
    }

    public ForwardHttpResponse(String jspLocation, HttpResponseContentType contentType, int expireSeconds) {
        this.jspLocation = jspLocation;
        this.contentType = contentType;
        this.expireSeconds = expireSeconds;
    }

    public String getJspLocation() {
        return jspLocation;
    }

    public void setJspLocation(String jspLocation) {
        this.jspLocation = jspLocation;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public void respond(HttpHelper httpHelper) throws IOException, ServletException {
        super.respond(httpHelper);
        if (response != null) {
            httpHelper.forward(jspLocation, response);
        } else {
            httpHelper.forward(jspLocation);
        }
    }
}
