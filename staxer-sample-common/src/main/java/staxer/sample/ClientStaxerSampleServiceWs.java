package staxer.sample;

import comtech.util.staxer.client.HttpWsClient;
import comtech.util.staxer.client.WsClientException;
import comtech.util.staxer.client.WsRequestHeader;
import comtech.util.xml.XmlName;
import staxer.sample.bean.EchoCustomTypesRequest;
import staxer.sample.bean.EchoCustomTypesResponse;
import staxer.sample.bean.EchoXsdTypesRequest;
import staxer.sample.bean.EchoXsdTypesResponse;


public class ClientStaxerSampleServiceWs {

    public static final XmlName XML_NAME_ECHO_XSD_TYPES = new XmlName("http://staxer.sample/", "echoXsdTypes");
    public static final XmlName XML_NAME_ECHO_CUSTOM_TYPES = new XmlName("http://staxer.sample/", "echoCustomTypes");
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
