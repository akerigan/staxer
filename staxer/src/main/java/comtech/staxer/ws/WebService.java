package comtech.staxer.ws;

import comtech.util.xml.XmlName;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-14 18:31 (Europe/Moscow)
 */
public class WebService {

    private String wsdlTargetNamespace;
    private String xsdTargetNamespace;
    private boolean xsdElementsQualified;
    private boolean xsdAttributesQualified;

    private Map<String, String> namespacesMap = new LinkedHashMap<String, String>();
    private Map<XmlName, WebServiceType> typesMap = new LinkedHashMap<XmlName, WebServiceType>();
    private Map<XmlName, WebServiceEnum> enumsMap = new LinkedHashMap<XmlName, WebServiceEnum>();
    private Map<XmlName, XmlName> globalTypeElementMap = new LinkedHashMap<XmlName, XmlName>();
    private Map<XmlName, XmlName> globalElementTypeMap = new LinkedHashMap<XmlName, XmlName>();
    private Map<XmlName, WebServiceMessage> messagesMap = new LinkedHashMap<XmlName, WebServiceMessage>();
    private Map<XmlName, WebServiceOperation> operationsMap = new LinkedHashMap<XmlName, WebServiceOperation>();
    private XmlName portTypeName;
    private XmlName bindingName;
    private String soapStyle;
    private String soapTransport;

    public String getWsdlTargetNamespace() {
        return wsdlTargetNamespace;
    }

    public void setWsdlTargetNamespace(String wsdlTargetNamespace) {
        this.wsdlTargetNamespace = wsdlTargetNamespace;
    }

    public String getXsdTargetNamespace() {
        return xsdTargetNamespace;
    }

    public void setXsdTargetNamespace(String xsdTargetNamespace) {
        this.xsdTargetNamespace = xsdTargetNamespace;
    }

    public boolean isXsdElementsQualified() {
        return xsdElementsQualified;
    }

    public void setXsdElementsQualified(boolean xsdElementsQualified) {
        this.xsdElementsQualified = xsdElementsQualified;
    }

    public boolean isXsdAttributesQualified() {
        return xsdAttributesQualified;
    }

    public void setXsdAttributesQualified(boolean xsdAttributesQualified) {
        this.xsdAttributesQualified = xsdAttributesQualified;
    }

    public Map<String, String> getNamespacesMap() {
        return namespacesMap;
    }

    public Map<XmlName, WebServiceType> getTypesMap() {
        return typesMap;
    }

    public Map<XmlName, WebServiceEnum> getEnumsMap() {
        return enumsMap;
    }

    public Map<XmlName, XmlName> getGlobalTypeElementMap() {
        return globalTypeElementMap;
    }

    public Map<XmlName, WebServiceMessage> getMessagesMap() {
        return messagesMap;
    }

    public Map<XmlName, XmlName> getGlobalElementTypeMap() {
        return globalElementTypeMap;
    }

    public void setGlobalElementTypeMap(Map<XmlName, XmlName> globalElementTypeMap) {
        this.globalElementTypeMap = globalElementTypeMap;
    }

    public Map<XmlName, WebServiceOperation> getOperationsMap() {
        return operationsMap;
    }

    public XmlName getPortTypeName() {
        return portTypeName;
    }

    public void setPortTypeName(XmlName portTypeName) {
        this.portTypeName = portTypeName;
    }

    public XmlName getBindingName() {
        return bindingName;
    }

    public void setBindingName(XmlName bindingName) {
        this.bindingName = bindingName;
    }

    public String getSoapStyle() {
        return soapStyle;
    }

    public void setSoapStyle(String soapStyle) {
        this.soapStyle = soapStyle;
    }

    public String getSoapTransport() {
        return soapTransport;
    }

    public void setSoapTransport(String soapTransport) {
        this.soapTransport = soapTransport;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WebService>\n");
        sb.append("<wsdlTargetNamespace>");
        sb.append(wsdlTargetNamespace);
        sb.append("</wsdlTargetNamespace>\n");
        sb.append("<xsdTargetNamespace>");
        sb.append(xsdTargetNamespace);
        sb.append("</xsdTargetNamespace>\n");
        sb.append("<xsdElementsQualified>");
        sb.append(xsdElementsQualified);
        sb.append("</xsdElementsQualified>\n");
        sb.append("<xsdAttributesQualified>");
        sb.append(xsdAttributesQualified);
        sb.append("</xsdAttributesQualified>\n");
        if (namespacesMap != null) {
            sb.append("<namespacesMap>");
            for (Object key : namespacesMap.keySet()) {
                sb.append("<entry key=\"");
                sb.append(key);
                sb.append("\">");
                sb.append(namespacesMap.get(key));
                sb.append("</entry>\n");
            }
            sb.append("</namespacesMap>\n");
        } else {
            sb.append("<namespacesMap/>\n");
        }
        if (typesMap != null) {
            sb.append("<typesMap>");
            for (Object key : typesMap.keySet()) {
                sb.append("<entry key=\"");
                sb.append(key);
                sb.append("\">");
                sb.append(typesMap.get(key));
                sb.append("</entry>\n");
            }
            sb.append("</typesMap>\n");
        } else {
            sb.append("<typesMap/>\n");
        }
        if (enumsMap != null) {
            sb.append("<enumsMap>");
            for (Object key : enumsMap.keySet()) {
                sb.append("<entry key=\"");
                sb.append(key);
                sb.append("\">");
                sb.append(enumsMap.get(key));
                sb.append("</entry>\n");
            }
            sb.append("</enumsMap>\n");
        } else {
            sb.append("<enumsMap/>\n");
        }
        if (globalTypeElementMap != null) {
            sb.append("<globalTypeElementMap>");
            for (Object key : globalTypeElementMap.keySet()) {
                sb.append("<entry key=\"");
                sb.append(key);
                sb.append("\">");
                sb.append(globalTypeElementMap.get(key));
                sb.append("</entry>\n");
            }
            sb.append("</globalTypeElementMap>\n");
        } else {
            sb.append("<globalTypeElementMap/>\n");
        }
        if (globalElementTypeMap != null) {
            sb.append("<globalElementTypeMap>");
            for (Object key : globalElementTypeMap.keySet()) {
                sb.append("<entry key=\"");
                sb.append(key);
                sb.append("\">");
                sb.append(globalElementTypeMap.get(key));
                sb.append("</entry>\n");
            }
            sb.append("</globalElementTypeMap>\n");
        } else {
            sb.append("<globalElementTypeMap/>\n");
        }
        if (messagesMap != null) {
            sb.append("<messagesMap>");
            for (Object key : messagesMap.keySet()) {
                sb.append("<entry key=\"");
                sb.append(key);
                sb.append("\">");
                sb.append(messagesMap.get(key));
                sb.append("</entry>\n");
            }
            sb.append("</messagesMap>\n");
        } else {
            sb.append("<messagesMap/>\n");
        }
        if (operationsMap != null) {
            sb.append("<operationsMap>");
            for (Object key : operationsMap.keySet()) {
                sb.append("<entry key=\"");
                sb.append(key);
                sb.append("\">");
                sb.append(operationsMap.get(key));
                sb.append("</entry>\n");
            }
            sb.append("</operationsMap>\n");
        } else {
            sb.append("<operationsMap/>\n");
        }
        sb.append("<portTypeName>");
        sb.append(portTypeName);
        sb.append("</portTypeName>\n");
        sb.append("<bindingName>");
        sb.append(bindingName);
        sb.append("</bindingName>\n");
        sb.append("<soapStyle>");
        sb.append(soapStyle);
        sb.append("</soapStyle>\n");
        sb.append("<soapTransport>");
        sb.append(soapTransport);
        sb.append("</soapTransport>\n");
        sb.append("</WebService>\n");

        return sb.toString();
    }
}
