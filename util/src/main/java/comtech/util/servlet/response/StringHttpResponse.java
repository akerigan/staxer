package comtech.util.servlet.response;

import comtech.util.servlet.helper.HttpHelper;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2010-12-06 17:02:27 (Europe/Moscow)
 */
public class StringHttpResponse extends AbstractHttpResponse {

    private String s;

    public StringHttpResponse(String s, HttpResponseContentType contentType) {
        this.s = s;
        this.contentType = contentType;
    }

    public void respond(HttpHelper httpHelper) throws IOException, ServletException {
        super.respond(httpHelper);
        httpHelper.getResponseWriter().write(s);
    }
}
