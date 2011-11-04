package comtech.staxer.client;

import comtech.staxer.soap.SoapFault;
import comtech.staxer.soap.SoapHeader;
import comtech.util.xml.ReadXml;
import comtech.util.xml.XmlName;
import comtech.util.xml.read.DocumentXmlStreamReader;
import comtech.util.xml.read.DocumentXmlStreamReader2;
import comtech.util.xml.read.StartElement;
import comtech.util.xml.write.DocumentXmlStreamWriter;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

import static comtech.staxer.StaxerConstants.ACCEPT_MIME;
import static comtech.staxer.StaxerConstants.XML_CONTENT_TYPE;
import static comtech.util.xml.XmlConstants.*;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 14.09.2009
 * Time: 16:27:47
 */
public class HttpWsClient {

    private static final String DEFAULT_NAME = "UNKNOWN";
    private static final int DEFAULT_CONNECTION_TIMEOUT = 15000;
    private static final int DEFAULT_PROCESS_TIMEOUT = 60000;

    private HttpClient httpClient;

    private AtomicInteger requestIdHolder = new AtomicInteger(0);

    private static Log log = LogFactory.getLog(HttpWsClient.class);

    private String name;

    public HttpWsClient() {
        httpClient = new HttpClient();
        httpClient.getParams().setContentCharset("UTF-8");

        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams connectionParams = connectionManager.getParams();
        connectionParams.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
        connectionParams.setSoTimeout(DEFAULT_PROCESS_TIMEOUT);
        httpClient.setHttpConnectionManager(connectionManager);
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
            ProxyHost proxyHost = null;
            switch (splitted.length) {
                case 1:
                    proxyHost = new ProxyHost(splitted[0]);
                    break;
                case 2:
                    proxyHost = new ProxyHost(splitted[0], Integer.valueOf(splitted[1]));
                    break;
            }
            setProxyHost2(proxyHost);
        }
    }

    public void setProxyHost2(ProxyHost proxyHost) {
        if (proxyHost != null) {
            httpClient.getHostConfiguration().setProxyHost(proxyHost);
        }
    }

    public void setConnectionTimeout(int timeout) {
        HttpConnectionManager connectionManager = httpClient.getHttpConnectionManager();
        HttpConnectionManagerParams connectionParams = connectionManager.getParams();
        connectionParams.setConnectionTimeout(timeout * 1000);
    }

    public void setProcessTimeout(int timeout) {
        HttpConnectionManager connectionManager = httpClient.getHttpConnectionManager();
        HttpConnectionManagerParams connectionParams = connectionManager.getParams();
        connectionParams.setSoTimeout(timeout * 1000);
    }

    public <T> T processSoapQuery(
            WsRequest wsRequest, Object requestObject, Class<T> responseClass
    ) throws WsClientException {
        int requestId = requestIdHolder.addAndGet(1);

        log.info(name + ", rid=" + requestId + ", wsRequest: " + wsRequest);

        if (wsRequest == null) {
            throw new IllegalStateException("Endpoint parameters is null");
        }

        StringWriter soapRequest = new StringWriter();
        if (requestObject instanceof String) {
            soapRequest.write(
                    "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\"><env:Body>"
            );
            soapRequest.write(requestObject.toString());
            soapRequest.write(
                    "</env:Body></env:Envelope>"
            );
        } else {
            try {
                DocumentXmlStreamWriter document1 = new DocumentXmlStreamWriter(soapRequest);
                document1.startDocument("utf-8", "1.0");
                document1.startElement(NAMESPACE_PREFIX_SOAP, SOAP_ENVELOPE, NAMESPACE_URI_SOAP_ENVELOPE);
                document1.namespace(NAMESPACE_PREFIX_SOAP, NAMESPACE_URI_SOAP_ENVELOPE);
                document1.namespace(NAMESPACE_PREFIX_XSI, NAMESPACE_URI_XSI);
                document1.namespace(NAMESPACE_PREFIX_XSD, NAMESPACE_URI_XSD);

                SoapHeader soapHeader = wsRequest.getSoapHeader();
                if (soapHeader != null) {
                    soapHeader.write(document1);
                }

                document1.startElement(NAMESPACE_PREFIX_SOAP, SOAP_BODY, NAMESPACE_URI_SOAP_ENVELOPE);
/*
                if (requestObject instanceof StaxerXmlHandler) {
                    ((StaxerXmlHandler) requestObject).callWriteXmlAsSoapBody((StaxerXmlHandler) requestObject, document1);
                } else {
*/
                document1.object(requestObject);
/*
                }
*/
                document1.endElement();
                document1.endElement();
                document1.endDocument();
            } catch (XMLStreamException e) {
                throw new WsClientException("Error while serializing soap request", e);
            } catch (NoSuchAlgorithmException e) {
                throw new WsClientException("Error while creating SHA-1 hash", e);
            }
        }

        log.info(name + ", rid=" + requestId + ", soap request: " + soapRequest.toString());

        try {
            String soapResponse = postQuery(
                    wsRequest, soapRequest.toString().getBytes("UTF-8"), requestId
            );

            log.info(name + ", rid=" + requestId + ", soap response: " + soapResponse);

            if (!responseClass.equals(String.class)) {
                if (Arrays.asList(responseClass.getInterfaces()).contains(ReadXml.class)) {
                    DocumentXmlStreamReader2 document2 = new DocumentXmlStreamReader2(new StringReader(soapResponse));
                    boolean envelopBodyRead = document2.readStartElement(XML_NAME_SOAP_ENVELOPE_BODY);
                    if (envelopBodyRead) {
                        XmlName bodyChildElement = document2.readStartElement();
                        if (bodyChildElement == null) {
                            throw new WsClientException(
                                    name + ", rid=" + requestId +
                                    ", invalid XML: cant locate payload element"
                            );
                        } else if (XML_NAME_SOAP_ENVELOPE_FAULT.equals(bodyChildElement)) {
                            SoapFault soapFault = new SoapFault();
                            soapFault.readXml(document2, XML_NAME_SOAP_ENVELOPE_FAULT);
                            throw new WsClientException(soapFault);
                        }
                        try {
                            T t = responseClass.newInstance();
                            ((ReadXml) t).readXml(document2, bodyChildElement);
                            return t;
                        } catch (InstantiationException e) {
                            throw new WsClientException(e);
                        } catch (IllegalAccessException e) {
                            throw new WsClientException(e);
                        }
                    } else {
                        throw new WsClientException(
                                name + ", rid=" + requestId +
                                ", invalid XML: cant locate '" +
                                XML_NAME_SOAP_ENVELOPE_BODY + "' element"
                        );
                    }
                } else {
                    DocumentXmlStreamReader document2 = new DocumentXmlStreamReader(new StringReader(soapResponse));
                    StartElement startElement = document2.readStartElement(QNAME_SOAP_ENVELOP_BODY);
                    if (startElement == null || !QNAME_SOAP_ENVELOP_BODY.equals(startElement.getName())) {
                        throw new WsClientException(name + ", rid=" + requestId + ", invalid XML: cant locate '" + QNAME_SOAP_ENVELOP_BODY + "' element");
                    }
                    startElement = document2.readStartElement();
                    if (startElement == null) {
                        throw new WsClientException(name + ", rid=" + requestId + ", invalid XML: cant locate payload element");
                    } else if (QNAME_SOAP_ENVELOP_FAULT.equals(startElement.getName())) {
                        throw new WsClientException(document2.readObject(SoapFault.class, false));
                    }
                    return document2.readObject(responseClass, false);
                }
            } else {
                return (T) soapResponse;
            }
        } catch (XMLStreamException e) {
            throw new WsClientException(name + ", rid=" + requestId + ", error while deserializing soap response", e);
        } catch (JAXBException e) {
            throw new WsClientException(name + ", rid=" + requestId + ", error while deserializing soap response", e);
        } catch (UnsupportedEncodingException e) {
            throw new WsClientException(name + ", rid=" + requestId + ", error while serializing soap response", e);
        }

    }

    private String postQuery(
            WsRequest wsRequest, byte[] data, int requestId
    ) throws WsClientException {
        PostMethod postMethod = new PostMethod(wsRequest.getEndpoint());
        postMethod.setRequestEntity(new ByteArrayRequestEntity(data, XML_CONTENT_TYPE));
        postMethod.setRequestHeader("Accept", ACCEPT_MIME);
        postMethod.setRequestHeader("User-Agent", "Staxer/1.0");
        postMethod.setRequestHeader("Cache-Control", "no-cache");
        postMethod.setRequestHeader("Pragma", "no-cache");
        postMethod.addRequestHeader("Content-Type", XML_CONTENT_TYPE);
        postMethod.addRequestHeader("Accept-Encoding", "deflate,gzip");

        List<HttpRequestHeader> requestHeaders = wsRequest.getRequestHeaders();
        if (requestHeaders != null && requestHeaders.size() > 0) {
            for (HttpRequestHeader requestHeader : requestHeaders) {
                postMethod.setRequestHeader(requestHeader.getName(), requestHeader.getValue());
            }
        }
        try {
            Map<String, String> headers = new LinkedHashMap<String, String>();
            for (Header header : postMethod.getRequestHeaders()) {
                headers.put(header.getName(), header.getValue());
            }
            log.info(name + ", rid=" + requestId + ", request headers: " + headers);
            int statusCode = httpClient.executeMethod(postMethod);

            if (statusCode != HttpStatus.SC_OK) {
                log.warn(name + ", rid=" + requestId + ", method failed: " + postMethod.getStatusLine());
            }

            headers.clear();
            for (Header header : postMethod.getResponseHeaders()) {
                headers.put(header.getName(), header.getValue());
            }
            log.info(name + ", rid=" + requestId + ", response headers: " + headers);

            Header encodingHeader = postMethod.getResponseHeader("Content-Encoding");
            if (encodingHeader != null) {
                InputStream inputStream;
                StringWriter writer = new StringWriter();
                if ("deflate".equalsIgnoreCase(encodingHeader.getValue())) {
                    inputStream = new DeflaterInputStream(postMethod.getResponseBodyAsStream());
                } else if ("gzip".equalsIgnoreCase(encodingHeader.getValue())) {
                    inputStream = new GZIPInputStream(postMethod.getResponseBodyAsStream());
                } else {
                    throw new WsClientException("Invalid encoding in response: " + encodingHeader.getValue());
                }
                InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
                IOUtils.copy(reader, writer);
                reader.close();
                return writer.toString();
            } else {
                return postMethod.getResponseBodyAsString();
            }

        } catch (HttpException e) {
            throw new WsClientException(name + ", rid=" + requestId + ", fatal protocol violation: ", e);
        } catch (IOException e) {
            throw new WsClientException(name + ", rid=" + requestId + "fatal transport error: ", e);
        } finally {
            postMethod.releaseConnection();
        }
    }

}
