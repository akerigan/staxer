package comtech.staxer.client;

import comtech.util.xml.StaxerXmlReader;
import comtech.util.xml.StaxerXmlWriter;
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

    public <Q extends StaxerXmlWriter, A extends StaxerXmlReader> A processSoapQuery(
            WsRequest wsRequest, Q requestObject,
            XmlName requestXmlName, Class<A> responseClass
    ) throws WsClientException;

    public <A extends StaxerXmlReader> A processSoapQuery(
            WsRequest wsRequest, String soapRequestXml, Class<A> responseClass
    ) throws WsClientException;

    public String sendSoapQuery(
            WsRequest wsRequest, String soapRequestXml, int requestId
    ) throws WsClientException;

}
