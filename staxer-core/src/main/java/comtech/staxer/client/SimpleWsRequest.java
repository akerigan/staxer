package comtech.staxer.client;

import comtech.util.xml.StaxerXmlStreamWriter;

import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-03-30 12:42 (Europe/Moscow)
 */
public class SimpleWsRequest implements WsRequest {

    private String endpoint;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void writeSoapHeader(StaxerXmlStreamWriter xmlWriter) {
        // do nothing
    }

    public List<HttpRequestHeader> getRequestHeaders() {
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<SimpleWsRequest>\n");
        sb.append("<endpoint>");
        sb.append(endpoint);
        sb.append("</endpoint>\n");
        sb.append("</SimpleWsRequest>\n");

        return sb.toString();
    }
}
