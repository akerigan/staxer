package org.staxer.sample.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staxer.sample.ClientStaxerSampleServiceWs;
import org.staxer.sample.bean.EchoXsdTypesRequest;
import org.staxer.sample.bean.EchoXsdTypesResponse;
import org.staxer.util.SecurityUtils;
import org.staxer.util.staxer.client.*;

import java.security.NoSuchAlgorithmException;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-22 16:23 (Europe/Moscow)
 */
public class EchoXsdTypesWsClient {

    private static Logger logger = LoggerFactory.getLogger(EchoXsdTypesWsClient.class);

    public static void main(String[] args) throws WsClientException, NoSuchAlgorithmException {

        ClientStaxerSampleServiceWs serviceWs = new ClientStaxerSampleServiceWs();
        serviceWs.setHttpWsClient(getWsClient(true));

        WssWsRequestHeader wsRequest = new WssWsRequestHeader();
        wsRequest.setEndpoint("http://localhost:8080/sample/sample");
        wsRequest.setLogin("user");
        wsRequest.setPassword(SecurityUtils.getMD5_EncodedBase64("user"));

        EchoXsdTypesResponse response = serviceWs.echoXsdTypes(wsRequest, new EchoXsdTypesRequest());

        logger.info(response.toString());
    }

    public static HttpWsClient getWsClient(boolean jettyClient) {
        HttpWsClient httpWsClient;
        if (jettyClient) {
            httpWsClient = new HttpWsClientJetty(false);
        } else {
            httpWsClient = new HttpWsClientCommons();
        }
        httpWsClient.setName("SERVER");
        httpWsClient.setConnectionTimeout(10);
        httpWsClient.setDefaultReadTimeout(30);
        return httpWsClient;
    }
}
