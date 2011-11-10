package comtech.staxer.domain;

import comtech.util.StringUtils;
import comtech.util.props.StringMapProperties;
import comtech.util.xml.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static comtech.util.StringUtils.*;
import static comtech.util.xml.XmlConstants.*;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-14 18:31 (Europe/Moscow)
 */
public class WebService implements StaxerXmlReader, StaxerXmlWriter {

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

    public Map<XmlName, XmlName> getGlobalTypeElementMap() {
        return globalTypeElementMap;
    }

    public Map<XmlName, XmlName> getGlobalElementTypeMap() {
        return globalElementTypeMap;
    }

    public Map<XmlName, WebServiceType> getTypesMap() {
        return typesMap;
    }

    public Map<XmlName, WebServiceEnum> getEnumsMap() {
        return enumsMap;
    }

    public Map<XmlName, WebServiceMessage> getMessagesMap() {
        return messagesMap;
    }

    public Map<XmlName, WebServiceOperation> getOperationsMap() {
        return operationsMap;
    }

    public XmlName getBindingName() {
        return bindingName;
    }

    public void readXmlAttributes(StringMapProperties attributes) throws StaxerXmlStreamException {
        wsdlTargetNamespace = attributes.get("targetNamespace");
        if (wsdlTargetNamespace == null) {
            wsdlTargetNamespace = "";
        }
    }

    public void readXmlContent(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        XmlName currentElementName = xmlReader.getLastStartedElement();
        xmlReader.updateNamespacesMap(namespacesMap);
        while (xmlReader.readNext()) {
            if (xmlReader.elementEnded(currentElementName)) {
                break;
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_TYPES)) {
                readWsdlTypes(xmlReader);
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_MESSAGE)) {
                readWsdlMessage(xmlReader);
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_PORT_TYPE)) {
                readWsdlPortType(xmlReader);
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_BINDING)) {
                readWsdlBinding(xmlReader);
            }
        }
    }

    private void readWsdlTypes(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        while (xmlReader.readNext()) {
            if (xmlReader.elementEnded(XML_NAME_WSDL_TYPES)) {
                break;
            } else if (xmlReader.elementStarted(XML_NAME_XSD_SCHEMA)) {
                readXsdSchema(xmlReader);
            }
        }
    }

    private void readXsdSchema(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        xmlReader.updateNamespacesMap(namespacesMap);
        StringMapProperties attributes = xmlReader.getAttributes();
        xsdTargetNamespace = attributes.get("targetNamespace");
        if (xsdTargetNamespace == null) {
            xsdTargetNamespace = wsdlTargetNamespace;
        }
        xsdElementsQualified = "qualified".equalsIgnoreCase(
                attributes.get("elementFormDefault")
        );
        xsdAttributesQualified = "qualified".equalsIgnoreCase(
                attributes.get("attributeFormDefault")
        );
        while (xmlReader.readNext()) {
            if (xmlReader.elementEnded(XML_NAME_XSD_SCHEMA)) {
                break;
            } else if (xmlReader.elementStarted(XML_NAME_XSD_COMPLEX_TYPE)) {
                readXsdComplexType(xmlReader);
            } else if (xmlReader.elementStarted(XML_NAME_XSD_SIMPLE_TYPE)) {
                readXsdSimpleType(xmlReader);
            } else if (xmlReader.elementStarted(XML_NAME_XSD_ELEMENT)) {
                attributes = xmlReader.getAttributes();
                XmlName typeName = unpackXmlName(attributes.get("type"), namespacesMap);
                XmlName elementName = new XmlName(xsdTargetNamespace, attributes.get("name"));
                globalTypeElementMap.put(typeName, elementName);
                globalElementTypeMap.put(elementName, typeName);
            }
        }
    }

    private void readXsdComplexType(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        WebServiceType type = new WebServiceType();
        StringMapProperties attributes = xmlReader.getAttributes();
        String localName = attributes.get("name");
        XmlName xmlName = new XmlName(xsdTargetNamespace, localName);
        type.setXmlName(xmlName);
        type.setJavaName(capitalize3(localName));

        typesMap.put(type.getXmlName(), type);

        while (xmlReader.readNext()) {
            if (xmlReader.elementEnded(XML_NAME_XSD_COMPLEX_TYPE)) {
                break;
            } else if (xmlReader.elementStarted(XML_NAME_XSD_SIMPLE_CONTENT)) {
                while (xmlReader.readNext()) {
                    if (xmlReader.elementEnded(XML_NAME_XSD_SIMPLE_CONTENT)) {
                        break;
                    } else if (xmlReader.elementStarted(XML_NAME_XSD_EXTENSION)) {
                        attributes = xmlReader.getAttributes();
                        WebServiceTypeField field = new WebServiceTypeField();
                        field.setJavaName("value");
                        field.setValueField(true);
                        field.setXmlType(unpackXmlName(attributes.get("base"), namespacesMap));
                        type.getFields().add(field);
                    } else if (xmlReader.elementStarted(XML_NAME_XSD_ATTRIBUTE)) {
                        type.getFields().add(createField(
                                xmlReader.getAttributes(), false,
                                xsdElementsQualified,
                                xsdAttributesQualified,
                                xsdTargetNamespace,
                                namespacesMap
                        ));
                    }
                }
            } else if (xmlReader.elementStarted(XML_NAME_XSD_COMPLEX_CONTENT)) {
                while (xmlReader.readNext()) {
                    if (xmlReader.elementEnded(XML_NAME_XSD_COMPLEX_CONTENT)) {
                        break;
                    } else if (xmlReader.elementStarted(XML_NAME_XSD_EXTENSION)) {
                        attributes = xmlReader.getAttributes();
                        type.setSuperTypeXmlName(unpackXmlName(attributes.get("base"), namespacesMap));
                    } else {
                        if (xmlReader.elementStarted(XML_NAME_XSD_SEQUENCE)) {
                            readXsdSequence(
                                    xmlReader, type,
                                    xsdElementsQualified,
                                    xsdAttributesQualified,
                                    xsdTargetNamespace,
                                    namespacesMap
                            );
                        } else if (xmlReader.elementStarted(XML_NAME_XSD_ATTRIBUTE)) {
                            type.getFields().add(createField(
                                    xmlReader.getAttributes(), false,
                                    xsdElementsQualified,
                                    xsdAttributesQualified,
                                    xsdTargetNamespace,
                                    namespacesMap
                            ));
                        }
                    }
                }
            } else {
                if (xmlReader.elementStarted(XML_NAME_XSD_SEQUENCE)) {
                    readXsdSequence(
                            xmlReader, type,
                            xsdElementsQualified,
                            xsdAttributesQualified,
                            xsdTargetNamespace,
                            namespacesMap
                    );
                } else if (xmlReader.elementStarted(XML_NAME_XSD_ATTRIBUTE)) {
                    type.getFields().add(createField(
                            xmlReader.getAttributes(), false,
                            xsdElementsQualified,
                            xsdAttributesQualified,
                            xsdTargetNamespace,
                            namespacesMap
                    ));
                }
            }
        }
    }

    private void readXsdSimpleType(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        WebServiceEnum enumType = new WebServiceEnum();
        StringMapProperties attributes = xmlReader.getAttributes();
        String localName = attributes.get("name");
        XmlName xmlName = new XmlName(xsdTargetNamespace, localName);
        enumType.setXmlName(xmlName);
        enumType.setJavaName(capitalize3(localName));
        enumsMap.put(enumType.getXmlName(), enumType);
        while (xmlReader.readNext()) {
            if (xmlReader.elementEnded(XML_NAME_XSD_SIMPLE_TYPE)) {
                break;
            } else if (xmlReader.elementStarted(XML_NAME_XSD_RESTRICTION)) {
                attributes = xmlReader.getAttributes();
                enumType.setXmlType(unpackXmlName(attributes.get("base"), namespacesMap));
                while (xmlReader.readNext()) {
                    if (xmlReader.elementEnded(XML_NAME_XSD_RESTRICTION)) {
                        break;
                    } else {
                        if (xmlReader.elementStarted(XML_NAME_XSD_ENUMERATION)) {
                            attributes = xmlReader.getAttributes();
                            String value = attributes.get("value");
                            WebServiceEnumValue enumValue = new WebServiceEnumValue();
                            enumValue.setValue(value);
                            char first = value.charAt(0);
                            if (first >= '0' && first <= '9') {
                                enumValue.setJavaName(toEnumName("value_" + value));
                            } else {
                                enumValue.setJavaName(toEnumName(value));
                            }
                            enumType.getValues().add(enumValue);
                        }
                    }
                }
            }
        }
    }

    private void readXsdSequence(
            StaxerXmlStreamReader xmlReader, WebServiceType type,
            boolean xsdElementsQualified, boolean xsdAttributesQualified,
            String xsdTargetNamespace, Map<String, String> namespacesMap
    ) throws StaxerXmlStreamException {
        while (xmlReader.readNext()) {
            if (xmlReader.elementEnded(XML_NAME_XSD_SEQUENCE)) {
                break;
            } else if (xmlReader.elementStarted(XML_NAME_XSD_ELEMENT)) {
                type.getFields().add(createField(
                        xmlReader.getAttributes(), true, xsdElementsQualified,
                        xsdAttributesQualified, xsdTargetNamespace, namespacesMap
                ));
            }
        }
    }

    private void readWsdlMessage(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        StringMapProperties attributes = xmlReader.getAttributes();
        WebServiceMessage message = new WebServiceMessage();
        message.setName(new XmlName(wsdlTargetNamespace, attributes.get("name")));
        messagesMap.put(message.getName(), message);
        while (xmlReader.readNext()) {
            if (xmlReader.elementEnded(XML_NAME_WSDL_MESSAGE)) {
                break;
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_PART)) {
                attributes = xmlReader.getAttributes();
                WebServiceMessagePart part = new WebServiceMessagePart();
                part.setName(new XmlName(wsdlTargetNamespace, attributes.get("name")));
                part.setElement(unpackXmlName(attributes.get("element"), namespacesMap));
                message.getParts().add(part);
            }
        }
    }

    private void readWsdlPortType(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        StringMapProperties attributes = xmlReader.getAttributes();
        portTypeName = new XmlName(wsdlTargetNamespace, attributes.get("name"));
        while (xmlReader.readNext()) {
            if (xmlReader.elementEnded(XML_NAME_WSDL_PORT_TYPE)) {
                break;
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_OPERATION)) {
                readWsdlPortOperation(xmlReader);
            }
        }
    }

    private void readWsdlPortOperation(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        StringMapProperties attributes = xmlReader.getAttributes();
        WebServiceOperation operation = new WebServiceOperation();
        String localName = attributes.get("name");
        operation.setName(new XmlName(wsdlTargetNamespace, localName));
        operation.setJavaName(decapitalize(capitalize3(localName)));
        operationsMap.put(operation.getName(), operation);
        while (xmlReader.readNext()) {
            if (xmlReader.elementEnded(XML_NAME_WSDL_OPERATION)) {
                break;
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_INPUT)) {
                attributes = xmlReader.getAttributes();
                operation.setInputName(new XmlName(wsdlTargetNamespace, attributes.get("name")));
                operation.setInputMessage(unpackXmlName(attributes.get("message"), namespacesMap));
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_OUTPUT)) {
                attributes = xmlReader.getAttributes();
                operation.setOutputName(new XmlName(wsdlTargetNamespace, attributes.get("name")));
                operation.setOutputMessage(unpackXmlName(attributes.get("message"), namespacesMap));
            }
        }
    }

    private void readWsdlBinding(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        StringMapProperties attributes = xmlReader.getAttributes();
        bindingName = new XmlName(wsdlTargetNamespace, attributes.get("name"));
        while (xmlReader.readNext()) {
            if (xmlReader.elementEnded(XML_NAME_WSDL_BINDING)) {
                break;
            } else if (xmlReader.elementStarted(XML_NAME_SOAP_BINDING)) {
                attributes = xmlReader.getAttributes();
                soapStyle = attributes.get("style");
                soapTransport = attributes.get("transport");
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_OPERATION)) {
                readWsdlBindingOperation(xmlReader);
            }
        }
    }

    private void readWsdlBindingOperation(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        StringMapProperties attributes = xmlReader.getAttributes();
        WebServiceOperation operation = operationsMap.get(new XmlName(wsdlTargetNamespace, attributes.get("name")));
        if (operation != null) {
            while (xmlReader.readNext()) {
                if (xmlReader.elementEnded(XML_NAME_WSDL_OPERATION)) {
                    break;
                } else if (xmlReader.elementStarted(XML_NAME_SOAP_OPERATION)) {
                    attributes = xmlReader.getAttributes();
                    operation.setSoapAction(attributes.get("soapAction"));
                } else if (xmlReader.elementStarted(XML_NAME_WSDL_INPUT)) {
                    while (xmlReader.readNext()) {
                        if (xmlReader.elementEnded(XML_NAME_WSDL_INPUT)) {
                            break;
                        } else if (xmlReader.elementStarted(XML_NAME_SOAP_BODY)) {
                            attributes = xmlReader.getAttributes();
                            operation.setInputSoapBody(attributes.get("use"));
                        }
                    }
                } else if (xmlReader.elementStarted(XML_NAME_WSDL_OUTPUT)) {
                    while (xmlReader.readNext()) {
                        if (xmlReader.elementEnded(XML_NAME_WSDL_OUTPUT)) {
                            break;
                        } else if (xmlReader.elementStarted(XML_NAME_SOAP_BODY)) {
                            attributes = xmlReader.getAttributes();
                            operation.setOutputSoapBody(attributes.get("use"));
                        }
                    }
                }
            }
        }
    }

    private WebServiceTypeField createField(
            StringMapProperties attributes, boolean elementField,
            boolean xsdElementsQualified, boolean xsdAttributesQualified,
            String xsdTargetNamespace, Map<String, String> namespacesMap
    ) {
        WebServiceTypeField result = new WebServiceTypeField();
        String localName = attributes.get("name");
        if (elementField && xsdElementsQualified || !elementField && xsdAttributesQualified) {
            result.setXmlName(new XmlName(xsdTargetNamespace, localName));
        } else {
            result.setXmlName(new XmlName(localName));
        }
        if ("return".equals(localName)) {
            localName = "result";
        } else if ("class".equals(localName)) {
            localName = "cls";
        } else if ("package".equals(localName)) {
            localName = "pkg";
        }
        result.setJavaName(decapitalize(capitalize3(localName)));
        result.setXmlType(unpackXmlName(attributes.get("type"), namespacesMap));
        result.setNillable(attributes.getBoolean("nillable"));
        result.setRequired(attributes.getInteger("minOccurs") > 0
                           || "required".equals(attributes.get("use")));
        result.setArray(
                "unbounded".equals(attributes.get("maxOccurs"))
                || attributes.getInteger("maxOccurs") > 1
        );
        result.setElementField(elementField);
        return result;
    }

    private XmlName unpackXmlName(
            String packedName, Map<String, String> namespacesMap
    ) {
        if (!StringUtils.isEmpty(packedName)) {
            String[] splitted = packedName.split(":");
            if (splitted.length > 1) {
                return new XmlName(namespacesMap.get(splitted[0]), splitted[1]);
            } else if (splitted.length > 0) {
                return new XmlName(packedName);
            }
        }
        return null;
    }

    public void writeXmlAttributes(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        // do nothing
    }

    public void writeXmlContent(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        // do nothing
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
