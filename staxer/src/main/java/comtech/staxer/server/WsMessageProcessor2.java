package comtech.staxer.server;

import comtech.staxer.StaxerException;
import comtech.staxer.domain.wss.WssNonce;
import comtech.staxer.domain.wss.WssPassword;
import comtech.staxer.domain.wss.WssSecurity;
import comtech.staxer.domain.wss.WssUsernameToken;
import comtech.staxer.soap.SoapFault;
import comtech.util.LogUtils;
import comtech.util.ResourceUtils;
import comtech.util.StringUtils;
import comtech.util.servlet.helper.HttpHelper;
import comtech.util.servlet.helper.HttpMethod;
import comtech.util.servlet.response.HttpResponseContentType;
import comtech.util.urlparams.ReadHttpParameters;
import comtech.util.xml.ReadXml;
import comtech.util.xml.WriteXml;
import comtech.util.xml.XmlConstants;
import comtech.util.xml.XmlName;
import comtech.util.xml.read.DocumentXmlStreamReader2;
import comtech.util.xml.write.DocumentXmlStreamWriter2;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static comtech.util.xml.XmlConstants.*;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 04.09.2009
 * Time: 12:49:18
 */
public abstract class WsMessageProcessor2 {

    private static XmlName XML_NAME_WSSECURITY =
            new XmlName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");

    public static final Integer PARAM_WS_REQUEST_ID = 1;
    public static final Integer PARAM_USER_LOGIN = 2;

    private ServerServiceWs2 serviceWs;
    private AtomicInteger wsRequestIdGenerator;
    private String wsdlPath;
    private String servletPath;

    public void setServiceWs(ServerServiceWs2 serviceWs) {
        this.serviceWs = serviceWs;
    }

    public void setWsRequestIdGenerator(AtomicInteger wsRequestIdGenerator) {
        this.wsRequestIdGenerator = wsRequestIdGenerator;
    }

    public void setWsdlPath(String wsdlPath) {
        this.wsdlPath = wsdlPath;
    }

    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public abstract Log getLog();

    public void process(
            HttpHelper httpHelper, OutputStream responseOutputStream
    ) throws StaxerException, InstantiationException, IllegalAccessException {
        try {
            httpHelper.setResponseContentType(HttpResponseContentType.XML);
            httpHelper.setResponseCharacterEncoding("UTF-8");
            if (httpHelper.getRequestParametersNames().contains("wsdl")) {
                Reader resourceReader = ResourceUtils.getResourceReader(serviceWs.getClass(), wsdlPath);
                StringWriter stringWriter = new StringWriter();
                IOUtils.copy(resourceReader, stringWriter);
                String wsdl = stringWriter.toString().replaceFirst(
                        "location=\"[^\"]+\"",
                        "location=\"" + httpHelper.getWebappURL() + "/" + servletPath + "\""
                );
                responseOutputStream.write(wsdl.getBytes("UTF-8"));
            } else {
                int wsRequestId = wsRequestIdGenerator.addAndGet(1);

                StringWriter requestBody = new StringWriter();
                BufferedReader reader = httpHelper.getRequestReader();
                IOUtils.copy(reader, requestBody);
                reader.close();

                Log log = getLog();
                log.info(LogUtils.getRequestDetails(httpHelper, wsRequestId, requestBody.toString()));

                WsMessage wsMessage = null;
                XmlName requestXmlName = null;
                Method method = null;
                XmlName responseXmlName = null;
                Object response = null;
                if (HttpMethod.GET == httpHelper.getMethod()) {
                    requestXmlName = new XmlName(
                            httpHelper.getRequestParameter("namespace"),
                            httpHelper.getRequestParameter("operation", "empty")
                    );
                    Class<? extends ReadHttpParameters> payloadClass =
                            serviceWs.getReadHttpParametersClass(requestXmlName);
                    if (payloadClass == null) {
                        response = new SoapFault("env:Server", "Cant handle element: " + requestXmlName);
                    } else {
                        ReadHttpParameters readHttpParametersInstance = payloadClass.newInstance();
                        readHttpParametersInstance.readHttpParameters(httpHelper);
                        wsMessage = buildMessage(wsRequestId, httpHelper, null);
                        wsMessage.setBody(readHttpParametersInstance);
                        String methodName = serviceWs.getMethodName(requestXmlName);
                        method = serviceWs.getClass().getMethod(methodName, WsMessage.class);
                        responseXmlName = serviceWs.getResponseXmlName(requestXmlName);
                    }
                } else if (HttpMethod.POST == httpHelper.getMethod()) {
                    DocumentXmlStreamReader2 document =
                            new DocumentXmlStreamReader2(new StringReader(requestBody.toString()));
                    if (document.readStartElement(XML_NAME_SOAP_ENVELOPE)) {
                        if (!document.elementStarted(XML_NAME_SOAP_ENVELOPE_BODY)
                            && !document.readStartElement(XML_NAME_SOAP_ENVELOPE_BODY)) {
                            response = new SoapFault("env:Sender", "Invalid SOAP message");
                        } else {
                            requestXmlName = document.readStartElement();
                            if (requestXmlName == null) {
                                response = new SoapFault("env:Sender", "Invalid SOAP message");
                            } else {
                                Class<? extends ReadXml> payloadClass =
                                        serviceWs.getReadXmlClass(requestXmlName);
                                responseXmlName = serviceWs.getResponseXmlName(requestXmlName);
                                if (payloadClass == null) {
                                    response = new SoapFault("env:Server", "Cant handle element: " + requestXmlName);
                                } else {
                                    ReadXml readXmlInstance = payloadClass.newInstance();
                                    readXmlInstance.readXml(document, requestXmlName);
                                    wsMessage = buildMessage(wsRequestId, httpHelper, document);
                                    wsMessage.setBody(readXmlInstance);
                                    String methodName = serviceWs.getMethodName(requestXmlName);
                                    method = serviceWs.getClass().getMethod(methodName, WsMessage.class);
                                }
                            }
                        }
                    } else {
                        response = new SoapFault("env:Sender", "Invalid SOAP message");
                    }
                } else {
                    response = new SoapFault("env:Sender", "Unsupported HTTP method");
                }
                if (response == null && method != null) {
                    try {
                        preProcess(wsMessage, httpHelper, requestXmlName);
                        response = method.invoke(serviceWs, wsMessage);
                    } catch (Exception e) {
                        getLog().error("", e);
                        response = faultProcess(e, wsMessage, requestXmlName);
                    }
                    try {
                        postProcess(wsMessage, requestXmlName);
                    } catch (Exception e) {
                        getLog().error("", e);
                    }
                } else {
                    log.error("Java method missing");
                    response = new SoapFault("env:Server", "Internal error occured");
                }
                ByteArrayOutputStream soapResponse = new ByteArrayOutputStream();
                DocumentXmlStreamWriter2 document = new DocumentXmlStreamWriter2(soapResponse, "UTF-8", 2);
                document.startDocument();
                document.startElement(XmlConstants.XML_NAME_SOAP_ENVELOPE);
                document.startElement(XmlConstants.XML_NAME_SOAP_ENVELOPE_BODY);
                if (response instanceof SoapFault) {
                    ((WriteXml) response).writeXml(document, XML_NAME_SOAP_ENVELOPE_FAULT);
                } else {
                    ((WriteXml) response).writeXml(document, responseXmlName);
                }
                document.endElement();
                document.endElement();
                document.endDocument();
                byte[] soapResponseBytes = soapResponse.toByteArray();
                if (log.isInfoEnabled()) {
                    log.info("----- Response -----\nID: " + wsRequestId + "\n"
                             + new String(soapResponseBytes, "UTF-8") + "\n--------------------\n"
                    );
                }
                responseOutputStream.write(soapResponseBytes);
            }
        } catch (IOException e) {
            throw new StaxerException(e);
        } catch (XMLStreamException e) {
            throw new StaxerException(e);
        } catch (NoSuchMethodException e) {
            throw new StaxerException(e);
        }
    }

    public WsMessage buildMessage(
            int wsRequestId, HttpHelper httpHelper,
            DocumentXmlStreamReader2 document
    ) throws XMLStreamException {
        WsMessage result = new WsMessage();
        String auth = httpHelper.getRequestHeader("Authorization");
        if (auth == null) {
            auth = httpHelper.getRequestParameter("Authorization");
        }
        String userLogin = null;
        if (auth != null && auth.startsWith("Basic ")) {
            userLogin = parseHttpBasicAuth(auth);
        }
        if (document != null) {
            XmlName startElement = document.readStartElement();
            if (XML_NAME_SOAP_ENVELOPE_HEADER.equals(startElement)) {
                startElement = document.readStartElement();
                if (startElement != null && XML_NAME_WSSECURITY.equals(startElement)) {
                    userLogin = parseWssAuth(wsRequestId, document);
                }
            }
        }
        HashMap<Integer, Object> params = new HashMap<Integer, Object>();
        params.put(PARAM_WS_REQUEST_ID, wsRequestId);
        if (!StringUtils.isEmpty(userLogin)) {
            params.put(PARAM_USER_LOGIN, userLogin);
        }
        result.setParams(params);
        return result;
    }

    private String parseHttpBasicAuth(String auth) {
        String userPassword = new String(Base64.decodeBase64(auth.substring(6)));
        if (userPassword.length() > 0) {
            String[] credentials = userPassword.trim().split("\\s*:\\s*");
            if (credentials != null && credentials.length == 2) {
                String validPassword = getUserPassword(credentials[0]);
                if (validPassword != null && validPassword.equals(credentials[1])) {
                    return credentials[0];
                }
            }
        }
        return null;
    }

    private String parseWssAuth(int wsRequestId, DocumentXmlStreamReader2 document) throws XMLStreamException {
        WssSecurity wssSecurity = new WssSecurity();
        wssSecurity.readXml(document, XML_NAME_WSSECURITY);
        Log log = getLog();
        WssUsernameToken usernameToken = wssSecurity.getUsernameToken();
        if (usernameToken == null) {
            log.warn("wsreq " + wsRequestId + ": WssUsernameToken is empty");
            return null;
        }
        WssNonce wssNonce = usernameToken.getNonce();
        if (wssNonce == null) {
            log.warn("wsreq " + wsRequestId + ": WssNonce is empty");
            return null;
        }
        String ENCODING_BASE64 = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary";
        if (!ENCODING_BASE64.equals(wssNonce.getEncodingType())) {
            log.warn("wsreq " + wsRequestId + " - WssNonce encoding not supported: " + wssNonce.getEncodingType());
            return null;
        }
        WssPassword wssPassword = usernameToken.getPassword();
        if (wssPassword == null) {
            log.warn("wsreq " + wsRequestId + ": WssPassword is empty");
            return null;
        }
        String PASSWORD_DIGEST = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest";
        if (!PASSWORD_DIGEST.equals(wssPassword.getType())) {
            log.warn("wsreq " + wsRequestId + " - WssPassword type not supported: " + wssPassword.getType());
            return null;
        }
        String validPassword = getUserPassword(usernameToken.getUserName());
        if (validPassword == null) {
            return null;
        }
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            sha.update(Base64.decodeBase64(wssNonce.getValue().trim()));
            sha.update(usernameToken.getCreated().getBytes());
            sha.update(validPassword.getBytes());
            String digest = Base64.encodeBase64String(sha.digest()).trim();
            if (!digest.equals(usernameToken.getPassword().getValue().trim())) {
                log.warn(
                        "wsreq " + wsRequestId + " - digests not match: "
                        + digest + "!=" + usernameToken.getPassword().getValue().trim()
                );
                return null;
            }
            return usernameToken.getUserName();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract String getUserPassword(String userName);

    public abstract void preProcess(
            WsMessage wsMessage, HttpHelper httpHelper, XmlName operationXmlName
    );

    public abstract void postProcess(
            WsMessage wsMessage, XmlName operationXmlName
    );

    public abstract SoapFault faultProcess(
            Exception e, WsMessage wsMessage, XmlName operationXmlName
    );

}
