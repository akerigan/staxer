package comtech.util.servlet.response;

import comtech.util.servlet.helper.HttpHelper;

import java.io.IOException;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 23.10.2009
 * Time: 10:26:17
 */
public class RedirectHttpResponse implements HttpResponse {

    private String redirectUrl;
    private boolean useContextPath;

    public RedirectHttpResponse(String redirectUrl) {
        this(redirectUrl, false);
    }

    public RedirectHttpResponse(String redirectUrl, boolean useContextPath) {
        this.redirectUrl = redirectUrl;
        this.useContextPath = useContextPath;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public boolean isUseContextPath() {
        return useContextPath;
    }

    public void setUseContextPath(boolean useContextPath) {
        this.useContextPath = useContextPath;
    }

    public void respond(HttpHelper httpHelper) throws IOException {
        if (useContextPath) {
            httpHelper.redirect(httpHelper.getContextPath() + "/" + redirectUrl);
        } else {
            httpHelper.redirect(redirectUrl);
        }
    }

}
