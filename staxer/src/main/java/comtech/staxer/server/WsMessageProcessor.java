package comtech.staxer.server;

import comtech.staxer.StaxerException;
import comtech.staxer.domain.wss.WssNonce;
import comtech.staxer.domain.wss.WssPassword;
import comtech.staxer.domain.wss.WssSecurity;
import comtech.staxer.domain.wss.WssUsernameToken;
import comtech.staxer.soap.SoapFault;
import comtech.staxer.soap.SoapUtils;
import comtech.util.LogUtils;
import comtech.util.ResourceUtils;
import comtech.util.StringUtils;
import comtech.util.servlet.helper.HttpHelper;
import comtech.util.servlet.helper.HttpMethod;
import comtech.util.servlet.response.HttpResponseContentType;
import comtech.util.xml.ReadXml;
import comtech.util.xml.XmlName;
import comtech.util.xml.read.DocumentXmlStreamReader2;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static comtech.util.xml.XmlConstants.*;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 04.09.2009
 * Time: 12:49:18
 */
public abstract class WsMessageProcessor {

    private static XmlName XML_NAME_WSSECURITY =
            new XmlName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");

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

                Object response;
                ByteArrayOutputStream soapResponse = new ByteArrayOutputStream();
                if (HttpMethod.GET == httpHelper.getMethod()) {
                    response = processGet(wsRequestId, httpHelper, requestBody.toString());
                } else if (HttpMethod.POST == httpHelper.getMethod()) {
                    response = processPost(wsRequestId, httpHelper, requestBody.toString());
                } else {
                    response = new SoapFault("env:Sender", "Unsupported HTTP method");
                }
                if (response != null) {
                    SoapUtils.serialize(response, soapResponse);
                }
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
        }
    }

    private Object processGet(
            int wsRequestId, HttpHelper httpHelper, String requestBody
    ) throws StaxerException {
        try {
            XmlName xmlElementName = new XmlName(
                    httpHelper.getRequestParameter("namespace"),
                    httpHelper.getRequestParameter("operation", "empty")
            );
            Class payloadClass = serviceWs.getClass(xmlElementName);
            if (payloadClass == null) {
                return new SoapFault("env:Server", "Cant handle element: " + xmlElementName);
            }
            boolean matches = false;
            for (Class interf : payloadClass.getInterfaces()) {
                if ("comtech.staxer.server.HttpParametersParser".equals(interf.getName())) {
                    matches = true;
                    break;
                }
            }
            if (!matches) {
                return new SoapFault("env:Server", "Cant handle element: " + xmlElementName);
            }
            WsMessage wsMessage = buildMessage(wsRequestId, httpHelper, null);
            HttpParametersParser body = (HttpParametersParser) payloadClass.newInstance();
            body.parseHttpParameters(httpHelper);
            wsMessage.setBody(body);
            String methodName = serviceWs.getMethodName(xmlElementName);
            Method method = serviceWs.getClass().getMethod(methodName, WsMessage.class);
            Object response;
            try {
                preProcess(wsMessage, httpHelper, xmlElementName);
                response = method.invoke(serviceWs, wsMessage);
            } catch (Exception e) {
                getLog().error("", e);
                response = faultProcess(e, wsMessage, xmlElementName);
            }
            try {
                postProcess(wsMessage, xmlElementName);
            } catch (Exception e) {
                getLog().error("", e);
            }
            return response;
        } catch (NoSuchMethodException e) {
            throw new StaxerException(e);
        } catch (JAXBException e) {
            throw new StaxerException(e);
        } catch (XMLStreamException e) {
            throw new StaxerException(e);
        } catch (InstantiationException e) {
            throw new StaxerException(e);
        } catch (IllegalAccessException e) {
            throw new StaxerException(e);
        }
    }

    private Object processPost(
            int wsRequestId, HttpHelper httpHelper, String requestBody
    ) throws StaxerException, IllegalAccessException, InstantiationException {
        try {
            DocumentXmlStreamReader2 document = new DocumentXmlStreamReader2(new StringReader(requestBody));
            if (document.readStartElement(XML_NAME_SOAP_ENVELOPE)) {
                WsMessage wsMessage = buildMessage(wsRequestId, httpHelper, document);
                if (!document.elementStarted(XML_NAME_SOAP_ENVELOPE_BODY)
                    && !document.readStartElement(XML_NAME_SOAP_ENVELOPE_BODY)) {
                    return new SoapFault("env:Sender", "Invalid SOAP message");
                }
                XmlName xmlElementName = document.readStartElement();
                if (xmlElementName == null) {
                    return new SoapFault("env:Sender", "Invalid SOAP message");
                }
                Class payloadClass = serviceWs.getClass(xmlElementName);
                if (payloadClass == null) {
                    return new SoapFault("env:Server", "Cant handle element: " + xmlElementName);
                }
                String methodName = serviceWs.getMethodName(xmlElementName);
                Method method = serviceWs.getClass().getMethod(methodName, WsMessage.class);
                if (Arrays.asList(payloadClass.getInterfaces()).contains(ReadXml.class)) {
                    Object o = payloadClass.newInstance();
                    ((ReadXml) o).readXml(document, xmlElementName);
                    wsMessage.setBody(o);
                } else {
                    JAXBContext context = JAXBContext.newInstance(payloadClass);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    wsMessage.setBody(unmarshaller.unmarshal(document.getReader()));
                }
                Object response;
                try {
                    preProcess(wsMessage, httpHelper, xmlElementName);
                    response = method.invoke(serviceWs, wsMessage);
                } catch (Exception e) {
                    getLog().error("", e);
                    response = faultProcess(e, wsMessage, xmlElementName);
                }
                try {
                    postProcess(wsMessage, xmlElementName);
                } catch (Exception e) {
                    getLog().error("", e);
                }
                return response;
            } else {
                return new SoapFault("env:Sender", "Invalid SOAP message");
            }
        } catch (NoSuchMethodException e) {
            throw new StaxerException(e);
        } catch (JAXBException e) {
            throw new StaxerException(e);
        } catch (XMLStreamException e) {
            throw new StaxerException(e);
        }
    }

    public WsMessage buildMessage(
            int wsRequestId, HttpHelper httpHelper,
            DocumentXmlStreamReader2 document
    ) throws XMLStreamException, JAXBException {
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

    private String parseWssAuth(int wsRequestId, DocumentXmlStreamReader2 document) throws XMLStreamException, JAXBException {
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
