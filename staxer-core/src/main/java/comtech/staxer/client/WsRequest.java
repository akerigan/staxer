package comtech.staxer.client;

import comtech.util.xml.StaxerXmlStreamWriter;

import java.util.List;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 17.03.2010
 * Time: 14:52:57
 */
public interface WsRequest {

    public String getEndpoint();

    public void writeSoapHeader(StaxerXmlStreamWriter xmlWriter);

    public List<HttpRequestHeader> getRequestHeaders();

}
