package comtech.staxer.client;

import comtech.util.SecurityUtils;
import comtech.util.StringUtils;
import comtech.util.xml.StaxerXmlStreamException;
import comtech.util.xml.StaxerXmlStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-03-30 12:33 (Europe/Moscow)
 */
public class BasicHttpAuthWsRequestHeader implements WsRequestHeader {

    private static Logger log = LoggerFactory.getLogger(BasicHttpAuthWsRequestHeader.class);

    private String endpoint;
    private String login;
    private String password;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void writeXmlAttributes(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        // do nothing
    }

    public void writeXmlContent(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        // do nothing
    }

    public List<HttpRequestHeader> getRequestHeaders() {
        List<HttpRequestHeader> result = null;
        if (!StringUtils.isEmpty(login) && !StringUtils.isEmpty(password)) {
            result = new LinkedList<HttpRequestHeader>();
            try {
                result.add(new HttpRequestHeader("Authorization", SecurityUtils.getBasicHttpAuth(
                        login, password
                )));
            } catch (UnsupportedEncodingException e) {
                log.error("Cant encode http basic auth to UTF-8", e);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<BasicHttpAuthWsRequestHeader>\n");
        sb.append("<endpoint>");
        sb.append(endpoint);
        sb.append("</endpoint>\n");
        sb.append("<login>");
        sb.append(login);
        sb.append("</login>\n");
        sb.append("<password>");
        sb.append(password);
        sb.append("</password>\n");
        sb.append("</BasicHttpAuthWsRequestHeader>\n");

        return sb.toString();
    }
}
