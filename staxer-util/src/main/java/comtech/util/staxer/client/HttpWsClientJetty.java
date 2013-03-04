package comtech.util.staxer.client;

import comtech.util.xml.*;
import comtech.util.xml.soap.SoapFault;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.Address;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

import static comtech.util.staxer.StaxerConstants.ACCEPT_MIME;
import static comtech.util.staxer.StaxerConstants.XML_CONTENT_TYPE;
import static comtech.util.xml.XmlConstants.XML_NAME_SOAP_ENVELOPE_BODY;
import static comtech.util.xml.XmlConstants.XML_NAME_SOAP_ENVELOPE_FAULT;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 14.09.2009
 * Time: 16:27:47
 */
public class HttpWsClientJetty implements HttpWsClient {

    private static final String DEFAULT_NAME = "UNKNOWN";
    private static final int DEFAULT_CONNECTION_TIMEOUT = 15000;
    private static final int DEFAULT_IDLE_TIMEOUT = 60000;

    private HttpClient httpClient;

    private AtomicInteger requestIdHolder = new AtomicInteger(0);

    private static Logger log = LoggerFactory.getLogger(HttpWsClientJetty.class);

    private String name;

    public HttpWsClientJetty(boolean userNioConnector) {
        httpClient = new HttpClient();
        if (userNioConnector) {
            httpClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        } else {
            httpClient.setConnectorType(HttpClient.CONNECTOR_SOCKET);
        }
        httpClient.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
        httpClient.setIdleTimeout(DEFAULT_IDLE_TIMEOUT);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name.toUpperCase();
        } else {
            this.name = DEFAULT_NAME;
        }
    }

    public void setProxyHost(String proxyHostString) {
        if (proxyHostString != null && proxyHostString.trim().length() > 0) {
            String[] splitted = proxyHostString.trim().split(":");
            Address proxyAddress = null;
            switch (splitted.length) {
                case 1:
                    proxyAddress = new Address(splitted[0], 80);
                    break;
                case 2:
                    proxyAddress = new Address(splitted[0], Integer.valueOf(splitted[1]));
                    break;
            }
            setProxyHost2(proxyAddress);
        }
    }

    public void setProxyHost2(Address proxyHost) {
        if (proxyHost != null) {
            httpClient.setProxy(proxyHost);
        }
    }

    public void setConnectionTimeout(int timeout) {
        httpClient.setConnectTimeout(timeout * 1000);
    }

    public void setDefaultReadTimeout(int timeout) {
        httpClient.setIdleTimeout(timeout * 1000);
    }

    public <Q extends StaxerWriteXml, A extends StaxerReadXml> A processSoapQuery(
            WsRequestHeader wsRequestHeader, Q requestObject,
            XmlName requestXmlName, Class<A> responseClass, int readTimeout
    ) throws WsClientException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            XmlUtils.writeSoapEnvelopedElement(
                    baos, "UTF-8", 2, requestObject, requestXmlName,
                    Arrays.asList(wsRequestHeader)
            );
        } catch (StaxerXmlStreamException e) {
            throw new WsClientException("Error while serializing soap request", e);
        }
        try {
            return processSoapQuery(
                    wsRequestHeader, new String(baos.toByteArray(), "UTF-8"), responseClass, readTimeout
            );
        } catch (UnsupportedEncodingException e) {
            throw new WsClientException("Error while serializing soap request", e);
        }
    }

    public <A extends StaxerReadXml> A processSoapQuery(
            WsRequestHeader wsRequestHeader, String soapRequestXml, Class<A> responseClass, int readTimeout
    ) throws WsClientException {
        int requestId = requestIdHolder.addAndGet(1);
        String soapResponseXml = sendSoapQuery(
                wsRequestHeader, soapRequestXml, requestId, readTimeout
        );
        try {
            StaxerXmlStreamReader document = new StaxerXmlStreamReader(new StringReader(soapResponseXml));
            boolean envelopBodyRead = document.readStartElement(XML_NAME_SOAP_ENVELOPE_BODY);
            if (envelopBodyRead) {
                XmlName bodyChildElement = document.readStartElement();
                if (bodyChildElement == null) {
                    throw new WsClientException(
                            name + ", rid=" + requestId +
                            ", invalid XML: cant locate payload element"
                    );
                } else if (XML_NAME_SOAP_ENVELOPE_FAULT.equals(bodyChildElement)) {
                    SoapFault soapFault = XmlUtils.readXml(document, SoapFault.class, XML_NAME_SOAP_ENVELOPE_FAULT);
                    throw new WsClientException(soapFault);
                }
                return XmlUtils.readXml(document, responseClass, bodyChildElement);
            } else {
                throw new WsClientException(
                        name + ", rid=" + requestId + ", invalid XML: cant locate '" +
                        XML_NAME_SOAP_ENVELOPE_BODY + "' element"
                );
            }
        } catch (StaxerXmlStreamException e) {
            throw new WsClientException(name + ", rid=" + requestId + ", error while deserializing soap response", e);
        }
    }

    public String sendSoapQuery(
            WsRequestHeader wsRequestHeader, String soapRequestXml, int requestId, int readTimeout
    ) throws WsClientException {
        log.info(name + ", rid=" + requestId + ", wsRequestHeader: " + wsRequestHeader);
        if (wsRequestHeader == null) {
            throw new IllegalStateException("Ws request header is null");
        }
        log.info(name + ", rid=" + requestId + ", soap request: " + soapRequestXml);
        try {
            String soapResponseXml = postQuery(
                    wsRequestHeader, soapRequestXml.getBytes("UTF-8"), requestId
            );
            log.info(name + ", rid=" + requestId + ", soap response: " + soapResponseXml);
            return soapResponseXml;
        } catch (UnsupportedEncodingException e) {
            throw new WsClientException(name + ", rid=" + requestId + ", error while serializing soap response", e);
        }
    }

    private String postQuery(
            WsRequestHeader wsRequestHeader, byte[] data, int requestId
    ) throws WsClientException {
        ContentExchange contentExchange = new ContentExchange(true);
/*
        try {
            URI uri = new URI(wsRequestHeader.getEndpoint());
            log.info("uri=" + uri);
            log.info("host=" + uri.getHost());
            log.info("port=" + uri.getPort());
            log.info("path=" + uri.getPath());
            contentExchange.setURI(uri);
        } catch (URISyntaxException e) {
            throw new WsClientException("Error while serializing soap request", e);
        }
*/
        contentExchange.setURL(wsRequestHeader.getEndpoint());
        contentExchange.setMethod("POST");
        contentExchange.setRequestContentSource(new ByteArrayInputStream(data));

        List<HttpRequestHeader> requestHeaders = new ArrayList<HttpRequestHeader>();
        requestHeaders.add(new HttpRequestHeader("Content-Type", XML_CONTENT_TYPE));
        requestHeaders.add(new HttpRequestHeader("Accept", ACCEPT_MIME));
        requestHeaders.add(new HttpRequestHeader("User-Agent", "Staxer/1.1"));
        requestHeaders.add(new HttpRequestHeader("Cache-Control", "no-cache"));
        requestHeaders.add(new HttpRequestHeader("Pragma", "no-cache"));
        requestHeaders.add(new HttpRequestHeader("Accept-Encoding", "deflate,gzip"));
        requestHeaders.add(new HttpRequestHeader("Content-Length", Integer.toString(data.length)));

        List<HttpRequestHeader> wsRequestHeaders = wsRequestHeader.getRequestHeaders();
        if (wsRequestHeaders != null && !wsRequestHeaders.isEmpty()) {
            requestHeaders.addAll(wsRequestHeaders);
        }
        try {
            Map<String, String> headers = new LinkedHashMap<String, String>();
            for (HttpRequestHeader header : requestHeaders) {
                headers.put(header.getName(), header.getValue());
                contentExchange.setRequestHeader(header.getName(), header.getValue());
            }
            log.info(name + ", rid=" + requestId + ", request headers: " + headers);
            if (!httpClient.isStarted()) {
                httpClient.start();
            }
            httpClient.send(contentExchange);
            contentExchange.waitForDone();
            int statusCode = contentExchange.getResponseStatus();

            String responseContent = null;
            if (statusCode != HttpStatus.OK_200) {
                responseContent = contentExchange.getResponseContent();
                log.warn(name + ", rid=" + requestId + ", method failed: " + responseContent);
                throw new WsClientException(
                        name + ", rid=" + requestId + ", http status code: " +
                        statusCode + ", response: " + responseContent);
            }

            headers.clear();
            HttpFields responseFields = contentExchange.getResponseFields();
            if (responseFields != null) {
                for (String fieldName : responseFields.getFieldNamesCollection()) {
                    headers.put(fieldName, responseFields.getStringField(fieldName));
                }
                log.info(name + ", rid=" + requestId + ", response headers: " + headers);

                String encodingHeader = responseFields.getStringField("Content-Encoding");
                if (encodingHeader != null) {
                    InputStream inputStream;
                    if ("deflate".equalsIgnoreCase(encodingHeader)) {
                        inputStream = new DeflaterInputStream(
                                new ByteArrayInputStream(contentExchange.getResponseContentBytes())
                        );
                    } else if ("gzip".equalsIgnoreCase(encodingHeader)) {
                        inputStream = new GZIPInputStream(
                                new ByteArrayInputStream(contentExchange.getResponseContentBytes())
                        );
                    } else {
                        throw new WsClientException("Invalid encoding in response: " + encodingHeader);
                    }
                    StringWriter writer = new StringWriter();
                    InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
                    IOUtils.copy(reader, writer);
                    reader.close();
                    return writer.toString();
                } else {
                    responseContent = contentExchange.getResponseContent();
                }
            }
            return responseContent;
        } catch (IOException e) {
            throw new WsClientException(name + ", rid=" + requestId + " fatal transport error: ", e);
        } catch (InterruptedException e) {
            throw new WsClientException(name + ", rid=" + requestId + " thread interrupted: ", e);
        } catch (Exception e) {
            throw new WsClientException(name + ", rid=" + requestId + " fatal transport error: ", e);
        } finally {
            contentExchange.reset();
        }
    }

}
