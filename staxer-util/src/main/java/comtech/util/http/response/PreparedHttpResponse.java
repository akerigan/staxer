package comtech.util.http.response;

import comtech.util.http.helper.HttpHelper;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 23.10.2009
 * Time: 10:57:12
 */
public class PreparedHttpResponse extends AbstractHttpResponse {

    private InputStream inputStream;

    public PreparedHttpResponse(InputStream inputStream, HttpResponseContentType contentType) {
        this(inputStream, contentType, 0);
    }

    public PreparedHttpResponse(InputStream inputStream, HttpResponseContentType contentType, int expireSeconds) {
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.expireSeconds = expireSeconds;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void respond(HttpHelper httpHelper) throws IOException, ServletException {
        super.respond(httpHelper);
        IOUtils.copy(inputStream, httpHelper.getResponseOutputStream());
        inputStream.close();
    }
}
