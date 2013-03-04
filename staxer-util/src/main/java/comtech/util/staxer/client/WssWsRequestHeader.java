package comtech.util.staxer.client;

import comtech.util.staxer.domain.WssSoapHeader;
import comtech.util.StringUtils;
import comtech.util.xml.StaxerXmlStreamException;
import comtech.util.xml.StaxerXmlStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-24 12:37 (Europe/Moscow)
 */
public class WssWsRequestHeader implements WsRequestHeader {

    private static Logger log = LoggerFactory.getLogger(WssWsRequestHeader.class);

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
    }

    public void writeXmlContent(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        if (!StringUtils.isEmpty(login) && !StringUtils.isEmpty(password)) {
            WssSoapHeader result = new WssSoapHeader();
            result.setClientName(login);
            result.setClientPassword(password);
            result.writeXmlContent(xmlWriter);
        }
    }

    public List<HttpRequestHeader> getRequestHeaders() {
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<SwcWsRequest>\n");
        sb.append("<endpoint>");
        sb.append(endpoint);
        sb.append("</endpoint>\n");
        sb.append("<login>");
        sb.append(login);
        sb.append("</login>\n");
        sb.append("<password>");
        sb.append(password);
        sb.append("</password>\n");
        sb.append("</SwcWsRequest>\n");

        return sb.toString();
    }
}

