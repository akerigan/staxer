package comtech.staxer.server;

import comtech.staxer.StaxerException;
import comtech.staxer.domain.WssNonce;
import comtech.staxer.domain.WssPassword;
import comtech.staxer.domain.WssSecurity;
import comtech.staxer.domain.WssUsernameToken;
import comtech.util.LogUtils;
import comtech.util.ResourceUtils;
import comtech.util.StringUtils;
import comtech.util.http.helper.HttpHelper;
import comtech.util.http.helper.HttpMethod;
import comtech.util.http.helper.ReadHttpParameters;
import comtech.util.http.response.HttpResponseContentType;
import comtech.util.xml.*;
import comtech.util.xml.soap.SoapFault;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static comtech.util.xml.XmlConstants.*;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 04.09.2009
 * Time: 12:49:18
 */
public abstract class WsMessageProcessor {

    public static final Integer PARAM_WS_REQUEST_ID = 1;
    public static final Integer PARAM_USER_LOGIN = 2;

    private ServerServiceWs serviceWs;
    private AtomicInteger wsRequestIdGenerator;
    private String wsdlPath;
    private String servletPath;

    public void setServiceWs(ServerServiceWs serviceWs) {
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

    public abstract Logger getLog();

    public void process(
            HttpHelper httpHelper, OutputStream responseOutputStream
    ) throws StaxerException {
        try {
            httpHelper.setResponseContentType(HttpResponseContentType.XML);
            httpHelper.setResponseCharacterEncoding("UTF-8");
            Set<String> requestParametersNames = httpHelper.getRequestParametersNames();
            Logger log = getLog();
            if (requestParametersNames.contains("wsdl") || requestParametersNames.contains("def")) {
                log.info(LogUtils.getRequestDetails(httpHelper, 0));
                Reader resourceReader = null;
                String def = httpHelper.getRequestParameter("def");
                if (def == null) {
                    resourceReader = ResourceUtils.getResourceReader(serviceWs.getClass(), wsdlPath);
                } else if (def.endsWith(".wsdl") || def.endsWith(".xsd")) {
                    resourceReader = ResourceUtils.getResourceReader(serviceWs.getClass(), "/" + def);
                }
                if (resourceReader != null) {
                    StringWriter stringWriter = new StringWriter();
                    IOUtils.copy(resourceReader, stringWriter);
                    String wsdl = stringWriter.toString().replaceAll(
                            "ocation=\"",
                            "ocation=\"" + httpHelper.getWebappURL() + "/" + servletPath + "?def="
                    ).replaceAll("\\?def\\=\"", "\"");
                    responseOutputStream.write(wsdl.getBytes("UTF-8"));
                } else {
                    httpHelper.setResponseStatus(404);
                }
            } else {
                int wsRequestId = wsRequestIdGenerator.addAndGet(1);

                StringWriter requestBody = new StringWriter();
                BufferedReader reader = httpHelper.getRequestReader();
                IOUtils.copy(reader, requestBody);
                reader.close();

                log.info(LogUtils.getRequestDetails(httpHelper, wsRequestId, requestBody.toString()));

                WsMessage wsMessage = null;
                XmlName requestXmlName = null;
                Method method = null;
                XmlName responseXmlName = null;
                StaxerWriteXml response = null;
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
                    StaxerXmlStreamReader xmlReader =
                            new StaxerXmlStreamReader(new StringReader(requestBody.toString()));
                    if (xmlReader.readStartElement(XML_NAME_SOAP_ENVELOPE)) {
                        wsMessage = buildMessage(wsRequestId, httpHelper, xmlReader);
                        if (!xmlReader.elementStarted(XML_NAME_SOAP_ENVELOPE_BODY)
                            && !xmlReader.readStartElement(XML_NAME_SOAP_ENVELOPE_BODY)) {
                            response = new SoapFault("env:Sender", "Invalid SOAP message");
                        } else {
                            requestXmlName = xmlReader.readStartElement();
                            if (requestXmlName == null) {
                                response = new SoapFault("env:Sender", "Invalid SOAP message");
                            } else {
                                Class<? extends StaxerReadXml> payloadClass =
                                        serviceWs.getReadXmlClass(requestXmlName);
                                responseXmlName = serviceWs.getResponseXmlName(requestXmlName);
                                if (payloadClass == null) {
                                    response = new SoapFault("env:Server", "Cant handle element: " + requestXmlName);
                                } else {
                                    StaxerReadXml readXmlInstance = payloadClass.newInstance();
                                    readXmlInstance.readXmlAttributes(xmlReader.getAttributes());
                                    readXmlInstance.readXmlContent(xmlReader);
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
                        response = (StaxerWriteXml) method.invoke(serviceWs, wsMessage);
                    } catch (Exception e) {
                        log.error("", e);
                        response = faultProcess(e, wsMessage, requestXmlName);
                    }
                    try {
                        postProcess(wsMessage, requestXmlName);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                } else {
                    log.error("Java method missing");
                    response = new SoapFault("env:Server", "Internal error occured");
                }
                ByteArrayOutputStream soapResponse = new ByteArrayOutputStream();
                XmlUtils.writeSoapEnvelopedElement(soapResponse, "UTF-8", 2, response, responseXmlName);
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
        } catch (NoSuchMethodException e) {
            throw new StaxerException(e);
        } catch (Exception e) {
            throw new StaxerException(e);
        }
    }

    public WsMessage buildMessage(
            int wsRequestId, HttpHelper httpHelper,
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        WsMessage result = new WsMessage();
        String auth = httpHelper.getRequestHeader("Authorization");
        if (auth == null) {
            auth = httpHelper.getRequestParameter("Authorization");
        }
        String userLogin = null;
        if (auth != null && auth.startsWith("Basic ")) {
            userLogin = parseHttpBasicAuth(auth);
        }
        if (xmlReader != null) {
            XmlName startElement = xmlReader.readStartElement();
            if (XML_NAME_SOAP_ENVELOPE_HEADER.equals(startElement)) {
                startElement = xmlReader.readStartElement();
                if (startElement != null && XML_NAME_WSS_SECURITY.equals(startElement)) {
                    userLogin = parseWssAuth(wsRequestId, xmlReader);
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

    private String parseWssAuth(
            int wsRequestId, StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        WssSecurity wssSecurity = XmlUtils.readXml(xmlReader, WssSecurity.class, XML_NAME_WSS_SECURITY);
        if (wssSecurity != null) {
            Logger log = getLog();
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
                if (digest.equals(usernameToken.getPassword().getValue().trim())) {
                    return usernameToken.getUserName();
                }
                log.warn(
                        "wsreq " + wsRequestId + " - digests not match: "
                        + digest + "!=" + usernameToken.getPassword().getValue().trim()
                );
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
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
