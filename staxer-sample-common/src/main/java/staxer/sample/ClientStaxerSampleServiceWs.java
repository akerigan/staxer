package staxer.sample;

import comtech.staxer.client.HttpWsClient;
import comtech.staxer.client.WsClientException;
import comtech.staxer.client.WsRequest;
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
            WsRequest wsRequest, EchoXsdTypesRequest parameters
    ) throws WsClientException {
        return httpWsClient.processSoapQuery(
                wsRequest, parameters, XML_NAME_ECHO_XSD_TYPES, EchoXsdTypesResponse.class
        );
    }

    public EchoCustomTypesResponse echoCustomTypes(
            WsRequest wsRequest, EchoCustomTypesRequest parameters
    ) throws WsClientException {
        return httpWsClient.processSoapQuery(
                wsRequest, parameters, XML_NAME_ECHO_CUSTOM_TYPES, EchoCustomTypesResponse.class
        );
    }

}
