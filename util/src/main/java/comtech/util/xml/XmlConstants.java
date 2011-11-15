package comtech.util.xml;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-25 16:07 (Europe/Moscow)
 */
public class XmlConstants {

    public static final String NAMESPACE_URI_WSDL = "http://schemas.xmlsoap.org/wsdl/";
    public static final String NAMESPACE_URI_XSD = "http://www.w3.org/2001/XMLSchema";
    public static final String NAMESPACE_URI_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String NAMESPACE_URI_SOAP = "http://schemas.xmlsoap.org/wsdl/soap/";
    public static final String NAMESPACE_URI_SOAP_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String NAMESPACE_URI_WSS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

    public static final String NAMESPACE_PREFIX_XSI = "xsi";
    public static final String NAMESPACE_PREFIX_XSD = "xsd";
    public static final String NAMESPACE_PREFIX_WSDL = "wsdl";
    public static final String NAMESPACE_PREFIX_SOAP = "soap";
    public static final String NAMESPACE_PREFIX_SOAP_ENVELOP = "env";
    public static final String NAMESPACE_PREFIX_WSS = "wss";

    public static final XmlName XML_NAME_WSDL_DEFINITIONS = new XmlName(NAMESPACE_URI_WSDL, "definitions");
    public static final XmlName XML_NAME_WSDL_TYPES = new XmlName(NAMESPACE_URI_WSDL, "types");
    public static final XmlName XML_NAME_WSDL_MESSAGE = new XmlName(NAMESPACE_URI_WSDL, "message");
    public static final XmlName XML_NAME_WSDL_PART = new XmlName(NAMESPACE_URI_WSDL, "part");
    public static final XmlName XML_NAME_WSDL_PORT_TYPE = new XmlName(NAMESPACE_URI_WSDL, "portType");
    public static final XmlName XML_NAME_WSDL_OPERATION = new XmlName(NAMESPACE_URI_WSDL, "operation");
    public static final XmlName XML_NAME_WSDL_INPUT = new XmlName(NAMESPACE_URI_WSDL, "input");
    public static final XmlName XML_NAME_WSDL_OUTPUT = new XmlName(NAMESPACE_URI_WSDL, "output");
    public static final XmlName XML_NAME_WSDL_BINDING = new XmlName(NAMESPACE_URI_WSDL, "binding");
    public static final XmlName XML_NAME_WSDL_IMPORT = new XmlName(NAMESPACE_URI_WSDL, "import");

    public static final XmlName XML_NAME_XSD_SCHEMA = new XmlName(NAMESPACE_URI_XSD, "schema");
    public static final XmlName XML_NAME_XSD_COMPLEX_TYPE = new XmlName(NAMESPACE_URI_XSD, "complexType");
    public static final XmlName XML_NAME_XSD_ANNOTATION = new XmlName(NAMESPACE_URI_XSD, "annotation");
    public static final XmlName XML_NAME_XSD_DOCUMENTATION = new XmlName(NAMESPACE_URI_XSD, "documentation");
    public static final XmlName XML_NAME_XSD_SEQUENCE = new XmlName(NAMESPACE_URI_XSD, "sequence");
    public static final XmlName XML_NAME_XSD_ELEMENT = new XmlName(NAMESPACE_URI_XSD, "element");
    public static final XmlName XML_NAME_XSD_ATTRIBUTE = new XmlName(NAMESPACE_URI_XSD, "attribute");
    public static final XmlName XML_NAME_XSD_SIMPLE_TYPE = new XmlName(NAMESPACE_URI_XSD, "simpleType");
    public static final XmlName XML_NAME_XSD_RESTRICTION = new XmlName(NAMESPACE_URI_XSD, "restriction");
    public static final XmlName XML_NAME_XSD_ENUMERATION = new XmlName(NAMESPACE_URI_XSD, "enumeration");
    public static final XmlName XML_NAME_XSD_COMPLEX_CONTENT = new XmlName(NAMESPACE_URI_XSD, "complexContent");
    public static final XmlName XML_NAME_XSD_EXTENSION = new XmlName(NAMESPACE_URI_XSD, "extension");
    public static final XmlName XML_NAME_XSD_SIMPLE_CONTENT = new XmlName(NAMESPACE_URI_XSD, "simpleContent");
    public static final XmlName XML_NAME_XSD_IMPORT = new XmlName(NAMESPACE_URI_XSD, "import");
    public static final XmlName XML_NAME_XSD_INCLUDE = new XmlName(NAMESPACE_URI_XSD, "include");

    public static final XmlName XML_NAME_XSI_NIL = new XmlName(NAMESPACE_URI_XSI, "nil");

    public static final XmlName XML_NAME_SOAP_BINDING = new XmlName(NAMESPACE_URI_SOAP, "binding");
    public static final XmlName XML_NAME_SOAP_OPERATION = new XmlName(NAMESPACE_URI_SOAP, "operation");
    public static final XmlName XML_NAME_SOAP_BODY = new XmlName(NAMESPACE_URI_SOAP, "body");

    public static final String SOAP_ENVELOPE = "Envelope";
    public static final String SOAP_HEADER = "Header";
    public static final String SOAP_BODY = "Body";
    public static final String SOAP_FAULT = "Fault";
    public static final String SOAP_FAULT_CODE = "faultcode";
    public static final String SOAP_FAULT_STRING = "faultstring";
    public static final String SOAP_FAULT_ACTOR = "faultactor";

    public static final XmlName XML_NAME_SOAP_ENVELOPE = new XmlName(NAMESPACE_URI_SOAP_ENVELOPE, SOAP_ENVELOPE);
    public static final XmlName XML_NAME_SOAP_ENVELOPE_HEADER = new XmlName(NAMESPACE_URI_SOAP_ENVELOPE, SOAP_HEADER);
    public static final XmlName XML_NAME_SOAP_ENVELOPE_BODY = new XmlName(NAMESPACE_URI_SOAP_ENVELOPE, SOAP_BODY);
    public static final XmlName XML_NAME_SOAP_ENVELOPE_FAULT = new XmlName(NAMESPACE_URI_SOAP_ENVELOPE, SOAP_FAULT);
    public static final XmlName XML_NAME_SOAP_ENVELOPE_FAULT_CODE = new XmlName(NAMESPACE_URI_SOAP_ENVELOPE, SOAP_FAULT_CODE);
    public static final XmlName XML_NAME_SOAP_ENVELOPE_FAULT_STRING = new XmlName(NAMESPACE_URI_SOAP_ENVELOPE, SOAP_FAULT_STRING);
    public static final XmlName XML_NAME_SOAP_ENVELOPE_FAULT_ACTOR = new XmlName(NAMESPACE_URI_SOAP_ENVELOPE, SOAP_FAULT_ACTOR);

    public static final XmlName XML_NAME_WSS_SECURITY = new XmlName(NAMESPACE_URI_WSS, "Security");
    public static final XmlName XML_NAME_WSS_USERNAME_TOKEN = new XmlName(NAMESPACE_URI_WSS, "UsernameToken");

    public static final Map<String, String> DEFAULT_NAMESPACES_PREFIXES;

    static {
        DEFAULT_NAMESPACES_PREFIXES = new HashMap<String, String>();
        DEFAULT_NAMESPACES_PREFIXES.put(NAMESPACE_URI_WSDL, NAMESPACE_PREFIX_WSDL);
        DEFAULT_NAMESPACES_PREFIXES.put(NAMESPACE_URI_XSD, NAMESPACE_PREFIX_XSD);
        DEFAULT_NAMESPACES_PREFIXES.put(NAMESPACE_URI_XSI, NAMESPACE_PREFIX_XSI);
        DEFAULT_NAMESPACES_PREFIXES.put(NAMESPACE_URI_SOAP, NAMESPACE_PREFIX_SOAP);
        DEFAULT_NAMESPACES_PREFIXES.put(NAMESPACE_URI_SOAP_ENVELOPE, NAMESPACE_PREFIX_SOAP_ENVELOP);
        DEFAULT_NAMESPACES_PREFIXES.put(NAMESPACE_URI_WSS, NAMESPACE_PREFIX_WSS);
    }
}
