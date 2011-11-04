package comtech.util.servlet.response;

import comtech.util.servlet.helper.HttpHelper;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2010-10-29 12:37:49 (Europe/Moscow)
 */
public class StaxHttpResponse extends AbstractHttpResponse {

    public StaxHttpResponse() {
        this.contentType = HttpResponseContentType.XML;
    }

    public void flushHeaders(HttpHelper httpHelper) throws IOException, ServletException {
        super.respond(httpHelper);
    }

    @Override
    public void respond(HttpHelper httpHelper) throws IOException, ServletException {
        // do nohing
    }
}
