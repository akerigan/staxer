package comtech.staxer.client;

import comtech.staxer.soap.SoapHeader;
import comtech.staxer.soap.WssSoapHeader;
import comtech.util.SecurityUtils;
import comtech.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-03-30 11:09 (Europe/Moscow)
 */
public class SwcWsRequest implements WsRequest {

    public static final String HEADER_DYNAMIC_ID = "dynamicId";

    public static enum AuthType {
        http, wss
    }

    private static Log log = LogFactory.getLog(SwcWsRequest.class);

    private String endpoint;
    private AuthType authType = AuthType.http;
    private String login;
    private String password;
    private Integer dynamicId;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
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

    public Integer getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(Integer dynamicId) {
        this.dynamicId = dynamicId;
    }

    public SoapHeader getSoapHeader() {
        if (authType == AuthType.wss && !StringUtils.isEmpty(login) && !StringUtils.isEmpty(password)) {
            WssSoapHeader result = new WssSoapHeader();
            result.setClientName(login);
            result.setClientPassword(password);
            return result;
        } else {
            return null;
        }
    }

    public List<HttpRequestHeader> getRequestHeaders() {
        List<HttpRequestHeader> result = null;
        if (authType == AuthType.http && !StringUtils.isEmpty(login) && !StringUtils.isEmpty(password)) {
            result = new LinkedList<HttpRequestHeader>();
            try {
                result.add(new HttpRequestHeader("Authorization", SecurityUtils.getBasicHttpAuth(
                        login, password
                )));
            } catch (UnsupportedEncodingException e) {
                log.error("Cant encode http basic auth to UTF-8", e);
            }
        }
        if (dynamicId != null) {
            if (result == null) {
                result = new LinkedList<HttpRequestHeader>();
            }
            result.add(new HttpRequestHeader(HEADER_DYNAMIC_ID, Integer.toString(dynamicId)));
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<SwcWsRequest>\n");
        sb.append("<endpoint>");
        sb.append(endpoint);
        sb.append("</endpoint>\n");
        sb.append("<authType>");
        sb.append(authType);
        sb.append("</authType>\n");
        sb.append("<login>");
        sb.append(login);
        sb.append("</login>\n");
        sb.append("<password>");
        sb.append(password);
        sb.append("</password>\n");
        sb.append("<dynamicId>");
        sb.append(dynamicId);
        sb.append("</dynamicId>\n");
        sb.append("</SwcWsRequest>\n");

        return sb.toString();
    }
}
