package comtech.staxer.client;

import comtech.util.xml.StaxerReadXml;
import comtech.util.xml.StaxerWriteXml;
import comtech.util.xml.XmlName;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-08 17:11 (Europe/Moscow)
 */
public interface HttpWsClient {

    public String getName();

    public void setName(String name);

    public void setProxyHost(String proxyHostString);

    public void setConnectionTimeout(int timeout);

    public void setProcessTimeout(int timeout);

    public <Q extends StaxerWriteXml, A extends StaxerReadXml> A processSoapQuery(
            WsRequestHeader wsRequestHeader, Q requestObject,
            XmlName requestXmlName, Class<A> responseClass
    ) throws WsClientException;

    public <A extends StaxerReadXml> A processSoapQuery(
            WsRequestHeader wsRequestHeader, String soapRequestXml, Class<A> responseClass
    ) throws WsClientException;

    public String sendSoapQuery(
            WsRequestHeader wsRequestHeader, String soapRequestXml, int requestId
    ) throws WsClientException;

}
