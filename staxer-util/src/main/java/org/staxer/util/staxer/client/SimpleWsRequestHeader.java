package org.staxer.util.staxer.client;

import org.staxer.util.xml.StaxerXmlStreamException;
import org.staxer.util.xml.StaxerXmlStreamWriter;

import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-03-30 12:42 (Europe/Moscow)
 */
public class SimpleWsRequestHeader implements WsRequestHeader {

    private String endpoint;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void writeXmlAttributes(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        // do nothing
    }

    public void writeXmlContent(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        // do nothing
    }

    public List<HttpRequestHeader> getRequestHeaders() {
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<SimpleWsRequestHeader>\n");
        sb.append("<endpoint>");
        sb.append(endpoint);
        sb.append("</endpoint>\n");
        sb.append("</SimpleWsRequestHeader>\n");

        return sb.toString();
    }
}
