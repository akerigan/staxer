package staxer.sample;

import comtech.staxer.server.ServerServiceWs;
import comtech.staxer.server.WsMessage;
import comtech.util.xml.XmlName;
import staxer.sample.bean.EchoCustomTypesRequest;
import staxer.sample.bean.EchoCustomTypesResponse;
import staxer.sample.bean.EchoXsdTypesRequest;
import staxer.sample.bean.EchoXsdTypesResponse;

import java.util.HashMap;
import java.util.Map;

public abstract class ServerStaxerSampleServiceWs implements ServerServiceWs {

    public static final XmlName XML_NAME_ECHO_XSD_TYPES = new XmlName("http://staxer.sample/", "echoXsdTypes");
    public static final XmlName XML_NAME_ECHO_CUSTOM_TYPES = new XmlName("http://staxer.sample/", "echoCustomTypes");

    public static final Map<XmlName, Class> CLASSES;
    public static final Map<XmlName, String> METHOD_NAMES;

    static {
        CLASSES = new HashMap<XmlName, Class>();
        METHOD_NAMES = new HashMap<XmlName, String>();

        CLASSES.put(XML_NAME_ECHO_XSD_TYPES, EchoXsdTypesRequest.class);
        METHOD_NAMES.put(XML_NAME_ECHO_XSD_TYPES, "echoXsdTypes");

        CLASSES.put(XML_NAME_ECHO_CUSTOM_TYPES, EchoCustomTypesRequest.class);
        METHOD_NAMES.put(XML_NAME_ECHO_CUSTOM_TYPES, "echoCustomTypes");
    }

    public Class getClass(XmlName xmlName) {
        return CLASSES.get(xmlName);
    }

    public String getMethodName(XmlName xmlName) {
        return METHOD_NAMES.get(xmlName);
    }

    public abstract EchoXsdTypesResponse echoXsdTypes(
            WsMessage<EchoXsdTypesRequest> wsMessage
    );

    public abstract EchoCustomTypesResponse echoCustomTypes(
            WsMessage<EchoCustomTypesRequest> wsMessage
    );

}
