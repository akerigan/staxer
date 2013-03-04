package comtech.util.staxer.client;

import comtech.util.xml.StaxerWriteXml;

import java.util.List;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 17.03.2010
 * Time: 14:52:57
 */
public interface WsRequestHeader extends StaxerWriteXml {

    public String getEndpoint();

    public List<HttpRequestHeader> getRequestHeaders();

}
