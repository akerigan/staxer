package org.staxer.sample;

import org.staxer.util.staxer.client.HttpWsClient;
import org.staxer.util.staxer.client.WsClientException;
import org.staxer.util.staxer.client.WsRequestHeader;
import org.staxer.util.xml.XmlName;
import org.staxer.sample.bean.EchoCustomTypesRequest;
import org.staxer.sample.bean.EchoCustomTypesResponse;
import org.staxer.sample.bean.EchoXsdTypesRequest;
import org.staxer.sample.bean.EchoXsdTypesResponse;


public class ClientStaxerSampleServiceWs {

    public static final XmlName XML_NAME_ECHO_XSD_TYPES = new XmlName("http://sample.staxer.org/", "echoXsdTypes");
    public static final XmlName XML_NAME_ECHO_CUSTOM_TYPES = new XmlName("http://sample.staxer.org/", "echoCustomTypes");
    private HttpWsClient httpWsClient;

    public HttpWsClient getHttpWsClient() {
        return httpWsClient;
    }

    public void setHttpWsClient(HttpWsClient httpWsClient) {
        this.httpWsClient = httpWsClient;
    }

    // service related methods

    public EchoXsdTypesResponse echoXsdTypes(
            WsRequestHeader wsRequestHeader, EchoXsdTypesRequest parameters
    ) throws WsClientException {
        return httpWsClient.processSoapQuery(
                wsRequestHeader, parameters,
                XML_NAME_ECHO_XSD_TYPES, EchoXsdTypesResponse.class,
                60
        );
    }

    public EchoCustomTypesResponse echoCustomTypes(
            WsRequestHeader wsRequestHeader, EchoCustomTypesRequest parameters
    ) throws WsClientException {
        return httpWsClient.processSoapQuery(
                wsRequestHeader, parameters,
                XML_NAME_ECHO_CUSTOM_TYPES, EchoCustomTypesResponse.class,
                60
        );
    }

}
