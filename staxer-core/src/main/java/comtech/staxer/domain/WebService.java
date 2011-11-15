package comtech.staxer.domain;

import comtech.staxer.StaxerUtils;
import comtech.util.ResourceUtils;
import comtech.util.StringUtils;
import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.*;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static comtech.util.StringUtils.capitalize3;
import static comtech.util.StringUtils.decapitalize;
import static comtech.util.xml.XmlConstants.*;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-14 18:31 (Europe/Moscow)
 */
public class WebService implements StaxerReadXml, StaxerWriteXml {

    private static final XmlName XML_NAME_TARGET_NAMESPACE = new XmlName("targetNamespace");
    private static final XmlName XML_NAME_NAME = new XmlName("name");
    private static final XmlName XML_NAME_ELEMENT = new XmlName("element");
    private static final XmlName XML_NAME_MESSAGE = new XmlName("message");
    private static final XmlName XML_NAME_STYLE = new XmlName("style");
    private static final XmlName XML_NAME_TRANSPORT = new XmlName("transport");
    private static final XmlName XML_NAME_SOAP_ACTION = new XmlName("soapAction");
    private static final XmlName XML_NAME_USE = new XmlName("use");
    private static final XmlName XML_NAME_LOCATION = new XmlName("location");
    private static final XmlName XML_NAME_NAMESPACE = new XmlName("namespace");

    private String wsdlTargetNamespace;

    private Map<String, String> namespacesMap = new LinkedHashMap<String, String>();

    private XmlSchema xmlSchema;

    private Map<XmlName, WebServiceMessage> messagesMap = new LinkedHashMap<XmlName, WebServiceMessage>();
    private Map<XmlName, WebServiceOperation> operationsMap = new LinkedHashMap<XmlName, WebServiceOperation>();
    private XmlName portTypeName;
    private XmlName bindingName;
    private String soapStyle;
    private String soapTransport;

    private URI baseUri;
    private String httpUser;
    private String httpPassword;
    private String wsdlCharset;

    public WebService() {
    }

    public WebService(URI baseUri, String httpUser, String httpPassword, String wsdlCharset) {
        this.baseUri = baseUri;
        this.httpUser = httpUser;
        this.httpPassword = httpPassword;
        this.wsdlCharset = wsdlCharset;
    }

    public Map<XmlName, XmlName> getGlobalTypeElementMap() {
        if (xmlSchema != null) {
            return xmlSchema.getGlobalTypeElementMap();
        } else {
            return Collections.emptyMap();
        }
    }

    public Map<XmlName, XmlName> getGlobalElementTypeMap() {
        if (xmlSchema != null) {
            return xmlSchema.getGlobalElementTypeMap();
        } else {
            return Collections.emptyMap();
        }
    }

    public Map<XmlName, XmlSchemaType> getTypesMap() {
        if (xmlSchema != null) {
            return xmlSchema.getTypesMap();
        } else {
            return Collections.emptyMap();
        }
    }

    public Map<XmlName, XmlSchemaEnum> getEnumsMap() {
        if (xmlSchema != null) {
            return xmlSchema.getEnumsMap();
        } else {
            return Collections.emptyMap();
        }
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

    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
        wsdlTargetNamespace = attributes.get(XML_NAME_TARGET_NAMESPACE);
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        xmlReader.updateNamespacesMap(namespacesMap);
        XmlName currentElementName = xmlReader.getLastStartedElement();
        while (xmlReader.readNext() && !xmlReader.elementEnded(currentElementName)) {
            if (xmlReader.elementStarted(XML_NAME_WSDL_TYPES)) {
                readWsdlTypes(xmlReader);
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_MESSAGE)) {
                readWsdlMessage(xmlReader);
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_PORT_TYPE)) {
                readWsdlPortType(xmlReader);
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_BINDING)) {
                readWsdlBinding(xmlReader);
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_IMPORT)) {
                XmlNameMapProperties attributes = xmlReader.getAttributes();
                URI location = URI.create(attributes.get(XML_NAME_LOCATION));
                if (baseUri != null) {
                    location = baseUri.resolve(location);
                }
                try {
                    String xml = ResourceUtils.getUrlContentAsString(location, httpUser, httpPassword, wsdlCharset);
                    if (xml != null) {
                        WebService webService = StaxerUtils.readWebService(location, httpUser, httpPassword, wsdlCharset);
                        if (webService != null) {
                            namespacesMap.putAll(webService.namespacesMap);
                            XmlSchema otherXmlSchema = webService.xmlSchema;
                            if (xmlSchema != null && otherXmlSchema != null) {
                                xmlSchema.getTypesMap().putAll(otherXmlSchema.getTypesMap());
                                xmlSchema.getEnumsMap().putAll(otherXmlSchema.getEnumsMap());
                                xmlSchema.getGlobalElementTypeMap().putAll(otherXmlSchema.getGlobalElementTypeMap());
                                xmlSchema.getGlobalTypeElementMap().putAll(otherXmlSchema.getGlobalTypeElementMap());
                            } else if (otherXmlSchema != null) {
                                xmlSchema = otherXmlSchema;
                            }
                            messagesMap.putAll(webService.getMessagesMap());
                            operationsMap.putAll(webService.getOperationsMap());
                            if (portTypeName != null) {
                                portTypeName = webService.portTypeName;
                            }
                            if (bindingName != null) {
                                bindingName = webService.bindingName;
                            }
                            if (soapStyle != null) {
                                soapStyle = webService.soapStyle;
                            }
                            if (soapTransport != null) {
                                soapTransport = webService.soapTransport;
                            }
                        }
                    }
                } catch (StaxerXmlStreamException e) {
                    throw e;
                } catch (Exception e) {
                    throw new StaxerXmlStreamException(e);
                }
            }
        }
    }

    private void readWsdlTypes(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_WSDL_TYPES)) {
            if (xmlReader.elementStarted(XML_NAME_XSD_SCHEMA)) {
                xmlSchema = new XmlSchema(wsdlTargetNamespace, baseUri, httpUser, httpPassword, wsdlCharset);
                xmlSchema.readXmlAttributes(xmlReader.getAttributes());
                xmlSchema.readXmlContent(xmlReader);
                namespacesMap.putAll(xmlSchema.getNamespacesMap());
            }
        }
    }

    private void readWsdlMessage(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        XmlNameMapProperties attributes = xmlReader.getAttributes();
        WebServiceMessage message = new WebServiceMessage();
        message.setName(new XmlName(wsdlTargetNamespace, attributes.get(XML_NAME_NAME)));
        messagesMap.put(message.getName(), message);
        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_WSDL_MESSAGE)) {
            if (xmlReader.elementStarted(XML_NAME_WSDL_PART)) {
                attributes = xmlReader.getAttributes();
                WebServiceMessagePart part = new WebServiceMessagePart();
                part.setName(new XmlName(wsdlTargetNamespace, attributes.get(XML_NAME_NAME)));
                part.setElement(unpackXmlName(attributes.get(XML_NAME_ELEMENT), namespacesMap));
                message.getParts().add(part);
            }
        }
    }

    private void readWsdlPortType(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        XmlNameMapProperties attributes = xmlReader.getAttributes();
        portTypeName = new XmlName(wsdlTargetNamespace, attributes.get(XML_NAME_NAME));
        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_WSDL_PORT_TYPE)) {
            if (xmlReader.elementStarted(XML_NAME_WSDL_OPERATION)) {
                readWsdlPortOperation(xmlReader);
            }
        }
    }

    private void readWsdlPortOperation(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        XmlNameMapProperties attributes = xmlReader.getAttributes();
        WebServiceOperation operation = new WebServiceOperation();
        String localName = attributes.get(XML_NAME_NAME);
        operation.setName(new XmlName(wsdlTargetNamespace, localName));
        operation.setJavaName(decapitalize(capitalize3(localName)));
        operationsMap.put(operation.getName(), operation);
        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_WSDL_OPERATION)) {
            if (xmlReader.elementStarted(XML_NAME_WSDL_INPUT)) {
                attributes = xmlReader.getAttributes();
                operation.setInputName(new XmlName(wsdlTargetNamespace, attributes.get(XML_NAME_NAME)));
                operation.setInputMessage(unpackXmlName(attributes.get(XML_NAME_MESSAGE), namespacesMap));
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_OUTPUT)) {
                attributes = xmlReader.getAttributes();
                operation.setOutputName(new XmlName(wsdlTargetNamespace, attributes.get(XML_NAME_NAME)));
                operation.setOutputMessage(unpackXmlName(attributes.get(XML_NAME_MESSAGE), namespacesMap));
            }
        }
    }

    private void readWsdlBinding(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        XmlNameMapProperties attributes = xmlReader.getAttributes();
        bindingName = new XmlName(wsdlTargetNamespace, attributes.get(XML_NAME_NAME));
        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_WSDL_BINDING)) {
            if (xmlReader.elementStarted(XML_NAME_SOAP_BINDING)) {
                attributes = xmlReader.getAttributes();
                soapStyle = attributes.get(XML_NAME_STYLE);
                soapTransport = attributes.get(XML_NAME_TRANSPORT);
            } else if (xmlReader.elementStarted(XML_NAME_WSDL_OPERATION)) {
                readWsdlBindingOperation(xmlReader);
            }
        }
    }

    private void readWsdlBindingOperation(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        XmlNameMapProperties attributes = xmlReader.getAttributes();
        WebServiceOperation operation = operationsMap.get(new XmlName(wsdlTargetNamespace, attributes.get(XML_NAME_NAME)));
        if (operation != null) {
            while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_WSDL_OPERATION)) {
                if (xmlReader.elementStarted(XML_NAME_SOAP_OPERATION)) {
                    attributes = xmlReader.getAttributes();
                    operation.setSoapAction(attributes.get(XML_NAME_SOAP_ACTION));
                } else if (xmlReader.elementStarted(XML_NAME_WSDL_INPUT)) {
                    while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_WSDL_INPUT)) {
                        if (xmlReader.elementStarted(XML_NAME_SOAP_BODY)) {
                            attributes = xmlReader.getAttributes();
                            operation.setInputSoapBody(attributes.get(XML_NAME_USE));
                        }
                    }
                } else if (xmlReader.elementStarted(XML_NAME_WSDL_OUTPUT)) {
                    while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_WSDL_OUTPUT)) {
                        if (xmlReader.elementStarted(XML_NAME_SOAP_BODY)) {
                            attributes = xmlReader.getAttributes();
                            operation.setOutputSoapBody(attributes.get(XML_NAME_USE));
                        }
                    }
                }
            }
        }
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
        sb.append("<xmlSchema>");
        sb.append(xmlSchema);
        sb.append("</xmlSchema>\n");
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
