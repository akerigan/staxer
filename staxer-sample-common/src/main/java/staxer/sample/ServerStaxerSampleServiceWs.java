package staxer.sample;

import comtech.util.staxer.server.ServerServiceWs;
import comtech.util.staxer.server.WsMessage;
import comtech.util.http.helper.ReadHttpParameters;
import comtech.util.xml.StaxerReadXml;
import comtech.util.xml.XmlName;
import staxer.sample.bean.EchoCustomTypesRequest;
import staxer.sample.bean.EchoCustomTypesResponse;
import staxer.sample.bean.EchoXsdTypesRequest;
import staxer.sample.bean.EchoXsdTypesResponse;

import java.util.HashMap;
import java.util.Map;

public abstract class ServerStaxerSampleServiceWs implements ServerServiceWs {

    public static final XmlName XML_NAME_ECHO_XSD_TYPES = new XmlName("http://staxer.sample/", "echoXsdTypes");
    public static final XmlName XML_NAME_ECHO_XSD_TYPES_RESPONSE = new XmlName("http://staxer.sample/", "echoXsdTypesResponse");
    public static final XmlName XML_NAME_ECHO_CUSTOM_TYPES = new XmlName("http://staxer.sample/", "echoCustomTypes");
    public static final XmlName XML_NAME_ECHO_CUSTOM_TYPES_RESPONSE = new XmlName("http://staxer.sample/", "echoCustomTypesResponse");

    public static final Map<XmlName, Class<? extends StaxerReadXml>> STAXER_READ_XML_CLASSES;
    public static final Map<XmlName, Class<? extends ReadHttpParameters>> READ_HTTP_PARAMETERS_CLASSES;
    public static final Map<XmlName, String> METHOD_NAMES;
    public static final Map<XmlName, XmlName> RESPONSE_XML_NAMES;

    static {
        STAXER_READ_XML_CLASSES = new HashMap<XmlName, Class<? extends StaxerReadXml>>();
        READ_HTTP_PARAMETERS_CLASSES = new HashMap<XmlName, Class<? extends ReadHttpParameters>>();
        METHOD_NAMES = new HashMap<XmlName, String>();
        RESPONSE_XML_NAMES = new HashMap<XmlName, XmlName>();

        STAXER_READ_XML_CLASSES.put(XML_NAME_ECHO_XSD_TYPES, EchoXsdTypesRequest.class);
        METHOD_NAMES.put(XML_NAME_ECHO_XSD_TYPES, "echoXsdTypes");
        RESPONSE_XML_NAMES.put(XML_NAME_ECHO_XSD_TYPES, XML_NAME_ECHO_XSD_TYPES_RESPONSE);

        STAXER_READ_XML_CLASSES.put(XML_NAME_ECHO_CUSTOM_TYPES, EchoCustomTypesRequest.class);
        METHOD_NAMES.put(XML_NAME_ECHO_CUSTOM_TYPES, "echoCustomTypes");
        RESPONSE_XML_NAMES.put(XML_NAME_ECHO_CUSTOM_TYPES, XML_NAME_ECHO_CUSTOM_TYPES_RESPONSE);
    }

    public Class<? extends StaxerReadXml> getReadXmlClass(XmlName requestXmlName) {
        return STAXER_READ_XML_CLASSES.get(requestXmlName);
    }

    public Class<? extends ReadHttpParameters> getReadHttpParametersClass(XmlName requestXmlName) {
        return READ_HTTP_PARAMETERS_CLASSES.get(requestXmlName);
    }

    public String getMethodName(XmlName xmlName) {
        return METHOD_NAMES.get(xmlName);
    }

    public XmlName getResponseXmlName(XmlName requestXmlName) {
        return RESPONSE_XML_NAMES.get(requestXmlName);
    }

    public abstract EchoXsdTypesResponse echoXsdTypes(
            WsMessage<EchoXsdTypesRequest> wsMessage
    );

    public abstract EchoCustomTypesResponse echoCustomTypes(
            WsMessage<EchoCustomTypesRequest> wsMessage
    );

}
