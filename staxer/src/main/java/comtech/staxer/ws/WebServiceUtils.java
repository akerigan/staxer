package comtech.staxer.ws;

import comtech.util.StringUtils;
import comtech.util.file.FileUtils;
import comtech.util.props.StringMapProperties;
import comtech.util.xml.XmlConstants;
import comtech.util.xml.XmlName;
import comtech.util.xml.read.DocumentXmlStreamReader2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.*;

import static comtech.util.StringUtils.*;
import static comtech.util.xml.XmlConstants.*;


/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-19 16:57 (Europe/Moscow)
 */
public class WebServiceUtils {

    private static Log log = LogFactory.getLog(WebServiceUtils.class);

    public static Map<XmlName, XsdWebServiceType> XSD_JAVA_TYPE_MAP;

    static {
        XSD_JAVA_TYPE_MAP = new HashMap<XmlName, XsdWebServiceType>();
        XsdWebServiceType javaType = new XsdWebServiceType();
        javaType.setJavaName("String");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "string"), javaType);
        javaType = new XsdWebServiceType();
        javaType.setJavaName("BigDecimal");
        javaType.getImports().add("java.math.BigDecimal");
        javaType.getImports().add("comtech.util.NumberUtils");
        javaType.setJavaConverter("NumberUtils.parseBigDecimal");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "decimal"), javaType);
        javaType = new XsdWebServiceType();
        javaType.setJavaName("Integer");
        javaType.getImports().add("comtech.util.NumberUtils");
        javaType.setJavaConverter("NumberUtils.parseInteger");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "unsignedShort"), javaType);
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "int"), javaType);
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "integer"), javaType);
        javaType = new XsdWebServiceType();
        javaType.setJavaName("Float");
        javaType.getImports().add("comtech.util.NumberUtils");
        javaType.setJavaConverter("NumberUtils.parseFloat");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "float"), javaType);
        javaType = new XsdWebServiceType();
        javaType.setJavaName("Double");
        javaType.getImports().add("comtech.util.NumberUtils");
        javaType.setJavaConverter("NumberUtils.parseDouble");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "double"), javaType);
        javaType = new XsdWebServiceType();
        javaType.setJavaName("Boolean");
        javaType.setJavaConverter("Boolean.parseBoolean");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "boolean"), javaType);
        javaType = new XsdWebServiceType();
        javaType.setJavaName("byte[]");
        javaType.getImports().add("org.apache.commons.codec.binary.Base64");
        javaType.setJavaConverter("Base64.decodeBase64");
        javaType.setXmlConverter("Base64.encodeBase64String");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "base64Binary"), javaType);
        javaType = new XsdWebServiceType();
        javaType.setJavaName("Date");
        javaType.getImports().add("java.util.Date");
        javaType.getImports().add("comtech.util.DateTimeUtils");
        javaType.setJaxbXmlSchema("dateTime");
        javaType.setJavaConverter("DateTimeUtils.parseXmlDate");
        javaType.setXmlConverter("DateTimeUtils.formatXmlDate");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "dateTime"), javaType);
    }

    public static WebService parseWebService(
            InputStream inputStream, String charset
    ) throws UnsupportedEncodingException, XMLStreamException {
        DocumentXmlStreamReader2 reader = new DocumentXmlStreamReader2(new InputStreamReader(
                inputStream, charset
        ));
        if (reader.readStartElement(XML_NAME_WSDL_DEFINITIONS)) {
            WebService result = new WebService();
            readXml(reader, result);
            return result;
        } else {
            return null;
        }
    }

    public static void readXml(DocumentXmlStreamReader2 reader, WebService result) throws XMLStreamException {
        reader.updateNamespacesMap(result.getNamespacesMap());
        StringMapProperties attributes = reader.getAttributes();
        String wsdlTargetNamespace = attributes.get("targetNamespace");
        if (wsdlTargetNamespace == null) {
            wsdlTargetNamespace = "";
        }
        result.setWsdlTargetNamespace(wsdlTargetNamespace);
        while (reader.readNext()) {
            if (reader.elementEnded(XML_NAME_WSDL_DEFINITIONS)) {
                break;
            } else if (reader.elementStarted(XML_NAME_WSDL_TYPES)) {
                readWsdlTypes(reader, result);
            } else if (reader.elementStarted(XML_NAME_WSDL_MESSAGE)) {
                readWsdlMessage(reader, result);
            } else if (reader.elementStarted(XML_NAME_WSDL_PORT_TYPE)) {
                readWsdlPortType(reader, result);
            } else if (reader.elementStarted(XML_NAME_WSDL_BINDING)) {
                readWsdlBinding(reader, result);
            }
        }
    }

    private static void readWsdlTypes(DocumentXmlStreamReader2 reader, WebService result) throws XMLStreamException {
        while (reader.readNext()) {
            if (reader.elementEnded(XML_NAME_WSDL_TYPES)) {
                break;
            } else if (reader.elementStarted(XML_NAME_XSD_SCHEMA)) {
                readXsdSchema(reader, result);
            }
        }
    }

    private static void readXsdSchema(DocumentXmlStreamReader2 reader, WebService result) throws XMLStreamException {
        Map<String, String> namespacesMap = result.getNamespacesMap();
        reader.updateNamespacesMap(namespacesMap);
        StringMapProperties attributes = reader.getAttributes();
        String xsdTargetNamespace = attributes.get("targetNamespace");
        if (xsdTargetNamespace == null) {
            xsdTargetNamespace = result.getWsdlTargetNamespace();
        }
        result.setXsdTargetNamespace(xsdTargetNamespace);
        result.setXsdElementsQualified("qualified".equalsIgnoreCase(
                attributes.get("elementFormDefault")
        ));
        result.setXsdAttributesQualified("qualified".equalsIgnoreCase(
                attributes.get("attributeFormDefault")
        ));
        while (reader.readNext()) {
            if (reader.elementEnded(XML_NAME_XSD_SCHEMA)) {
                break;
            } else if (reader.elementStarted(XML_NAME_XSD_COMPLEX_TYPE)) {
                readXsdComplexType(reader, result);
            } else if (reader.elementStarted(XML_NAME_XSD_SIMPLE_TYPE)) {
                readXsdSimpleType(reader, result);
            } else if (reader.elementStarted(XML_NAME_XSD_ELEMENT)) {
                attributes = reader.getAttributes();
                XmlName typeName = unpackXmlName(attributes.get("type"), namespacesMap);
                XmlName elementName = new XmlName(xsdTargetNamespace, attributes.get("name"));
                result.getGlobalTypeElementMap().put(typeName, elementName);
                result.getGlobalElementTypeMap().put(elementName, typeName);
            }
        }
    }

    private static void readXsdComplexType(DocumentXmlStreamReader2 reader, WebService result) throws XMLStreamException {
        WebServiceType type = new WebServiceType();
        StringMapProperties attributes = reader.getAttributes();
        String localName = attributes.get("name");
        String xsdTargetNamespace = result.getXsdTargetNamespace();
        XmlName xmlName = new XmlName(xsdTargetNamespace, localName);
        type.setXmlName(xmlName);
        type.setJavaName(capitalize3(localName));

        result.getTypesMap().put(type.getXmlName(), type);

        boolean xsdElementsQualified = result.isXsdElementsQualified();
        boolean xsdAttributesQualified = result.isXsdAttributesQualified();
        Map<String, String> namespacesMap = result.getNamespacesMap();
        while (reader.readNext()) {
            if (reader.elementEnded(XML_NAME_XSD_COMPLEX_TYPE)) {
                break;
            } else if (reader.elementStarted(XML_NAME_XSD_SIMPLE_CONTENT)) {
                while (reader.readNext()) {
                    if (reader.elementEnded(XML_NAME_XSD_SIMPLE_CONTENT)) {
                        break;
                    } else if (reader.elementStarted(XML_NAME_XSD_EXTENSION)) {
                        attributes = reader.getAttributes();
                        WebServiceTypeField field = new WebServiceTypeField();
                        field.setJavaName("value");
                        field.setValueField(true);
                        field.setXmlType(unpackXmlName(attributes.get("base"), namespacesMap));
                        type.getFields().add(field);
                    } else if (reader.elementStarted(XML_NAME_XSD_ATTRIBUTE)) {
                        type.getFields().add(createField(
                                reader.getAttributes(), false,
                                xsdElementsQualified,
                                xsdAttributesQualified,
                                xsdTargetNamespace,
                                namespacesMap
                        ));
                    }
                }
            } else {
                if (reader.elementStarted(XML_NAME_XSD_SEQUENCE)) {
                    readXsdSequence(
                            reader, type,
                            xsdElementsQualified,
                            xsdAttributesQualified,
                            xsdTargetNamespace,
                            namespacesMap
                    );
                } else if (reader.elementStarted(XML_NAME_XSD_ATTRIBUTE)) {
                    type.getFields().add(createField(
                            reader.getAttributes(), false,
                            xsdElementsQualified,
                            xsdAttributesQualified,
                            xsdTargetNamespace,
                            namespacesMap
                    ));
                }
            }
        }
    }

    private static void readXsdSimpleType(DocumentXmlStreamReader2 reader, WebService result) throws XMLStreamException {
        WebServiceEnum enumType = new WebServiceEnum();
        StringMapProperties attributes = reader.getAttributes();
        String localName = attributes.get("name");
        String xsdTargetNamespace = result.getXsdTargetNamespace();
        XmlName xmlName = new XmlName(xsdTargetNamespace, localName);
        enumType.setXmlName(xmlName);
        enumType.setJavaName(capitalize3(localName));
        result.getEnumsMap().put(enumType.getXmlName(), enumType);
        Map<String, String> namespacesMap = result.getNamespacesMap();
        while (reader.readNext()) {
            if (reader.elementEnded(XML_NAME_XSD_SIMPLE_TYPE)) {
                break;
            } else if (reader.elementStarted(XML_NAME_XSD_RESTRICTION)) {
                attributes = reader.getAttributes();
                enumType.setXmlType(unpackXmlName(attributes.get("base"), namespacesMap));
                while (reader.readNext()) {
                    if (reader.elementEnded(XML_NAME_XSD_RESTRICTION)) {
                        break;
                    } else {
                        if (reader.elementStarted(XML_NAME_XSD_ENUMERATION)) {
                            attributes = reader.getAttributes();
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

    private static void readXsdSequence(
            DocumentXmlStreamReader2 reader, WebServiceType type,
            boolean xsdElementsQualified, boolean xsdAttributesQualified,
            String xsdTargetNamespace, Map<String, String> namespacesMap
    ) throws XMLStreamException {
        while (reader.readNext()) {
            if (reader.elementEnded(XML_NAME_XSD_SEQUENCE)) {
                break;
            } else if (reader.elementStarted(XML_NAME_XSD_ELEMENT)) {
                type.getFields().add(createField(
                        reader.getAttributes(), true, xsdElementsQualified,
                        xsdAttributesQualified, xsdTargetNamespace, namespacesMap
                ));
            }
        }
    }

    private static void readWsdlMessage(DocumentXmlStreamReader2 reader, WebService result) throws XMLStreamException {
        StringMapProperties attributes = reader.getAttributes();
        WebServiceMessage message = new WebServiceMessage();
        message.setName(new XmlName(result.getWsdlTargetNamespace(), attributes.get("name")));
        result.getMessagesMap().put(message.getName(), message);
        while (reader.readNext()) {
            if (reader.elementEnded(XML_NAME_WSDL_MESSAGE)) {
                break;
            } else if (reader.elementStarted(XML_NAME_WSDL_PART)) {
                attributes = reader.getAttributes();
                WebServiceMessagePart part = new WebServiceMessagePart();
                part.setName(new XmlName(result.getWsdlTargetNamespace(), attributes.get("name")));
                part.setElement(unpackXmlName(attributes.get("element"), result.getNamespacesMap()));
                message.getParts().add(part);
            }
        }
    }

    private static void readWsdlPortType(DocumentXmlStreamReader2 reader, WebService result) throws XMLStreamException {
        StringMapProperties attributes = reader.getAttributes();
        result.setPortTypeName(new XmlName(result.getWsdlTargetNamespace(), attributes.get("name")));
        while (reader.readNext()) {
            if (reader.elementEnded(XML_NAME_WSDL_PORT_TYPE)) {
                break;
            } else if (reader.elementStarted(XML_NAME_WSDL_OPERATION)) {
                readWsdlPortOperation(reader, result);
            }
        }
    }

    private static void readWsdlPortOperation(DocumentXmlStreamReader2 reader, WebService result) throws XMLStreamException {
        StringMapProperties attributes = reader.getAttributes();
        WebServiceOperation operation = new WebServiceOperation();
        String wsdlTargetNamespace = result.getWsdlTargetNamespace();
        operation.setName(new XmlName(wsdlTargetNamespace, attributes.get("name")));
        result.getOperationsMap().put(operation.getName(), operation);
        while (reader.readNext()) {
            if (reader.elementEnded(XML_NAME_WSDL_OPERATION)) {
                break;
            } else if (reader.elementStarted(XML_NAME_WSDL_INPUT)) {
                attributes = reader.getAttributes();
                operation.setInputName(new XmlName(wsdlTargetNamespace, attributes.get("name")));
                operation.setInputMessage(unpackXmlName(attributes.get("message"), result.getNamespacesMap()));
            } else if (reader.elementStarted(XML_NAME_WSDL_OUTPUT)) {
                attributes = reader.getAttributes();
                operation.setOutputName(new XmlName(wsdlTargetNamespace, attributes.get("name")));
                operation.setOutputMessage(unpackXmlName(attributes.get("message"), result.getNamespacesMap()));
            }
        }
    }

    private static void readWsdlBinding(DocumentXmlStreamReader2 reader, WebService result) throws XMLStreamException {
        StringMapProperties attributes = reader.getAttributes();
        String wsdlTargetNamespace = result.getWsdlTargetNamespace();
        result.setBindingName(new XmlName(wsdlTargetNamespace, attributes.get("name")));
        while (reader.readNext()) {
            if (reader.elementEnded(XML_NAME_WSDL_BINDING)) {
                break;
            } else if (reader.elementStarted(XML_NAME_SOAP_BINDING)) {
                attributes = reader.getAttributes();
                result.setSoapStyle(attributes.get("style"));
                result.setSoapTransport(attributes.get("transport"));
            } else if (reader.elementStarted(XML_NAME_WSDL_OPERATION)) {
                readWsdlBindingOperation(reader, result);
            }
        }
    }

    private static void readWsdlBindingOperation(DocumentXmlStreamReader2 reader, WebService result) throws XMLStreamException {
        StringMapProperties attributes = reader.getAttributes();
        Map<XmlName, WebServiceOperation> operationsMap = result.getOperationsMap();
        String wsdlTargetNamespace = result.getWsdlTargetNamespace();
        WebServiceOperation operation = operationsMap.get(new XmlName(wsdlTargetNamespace, attributes.get("name")));
        if (operation != null) {
            while (reader.readNext()) {
                if (reader.elementEnded(XML_NAME_WSDL_OPERATION)) {
                    break;
                } else if (reader.elementStarted(XML_NAME_SOAP_OPERATION)) {
                    attributes = reader.getAttributes();
                    operation.setSoapAction(attributes.get("soapAction"));
                } else if (reader.elementStarted(XML_NAME_WSDL_INPUT)) {
                    while (reader.readNext()) {
                        if (reader.elementEnded(XML_NAME_WSDL_INPUT)) {
                            break;
                        } else if (reader.elementStarted(XML_NAME_SOAP_BODY)) {
                            attributes = reader.getAttributes();
                            operation.setInputSoapBody(attributes.get("use"));
                        }
                    }
                } else if (reader.elementStarted(XML_NAME_WSDL_OUTPUT)) {
                    while (reader.readNext()) {
                        if (reader.elementEnded(XML_NAME_WSDL_OUTPUT)) {
                            break;
                        } else if (reader.elementStarted(XML_NAME_SOAP_BODY)) {
                            attributes = reader.getAttributes();
                            operation.setOutputSoapBody(attributes.get("use"));
                        }
                    }
                }
            }
        }
    }

    private static WebServiceTypeField createField(
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

    private static XmlName unpackXmlName(
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

    public static void createJavaWebService(
            WebService webService, File sourceDir,
            String packageName, boolean writeJaxbAnnotations,
            boolean writeClientService, boolean writeServerService
    ) throws IOException {
        File destDir = new File(sourceDir, packageName.replaceAll("\\.", "/"));
        File beansDir = new File(destDir, "bean");
        FileUtils.createOrCleanupDir(beansDir, log);

        Map<XmlName, XmlName> globalTypeElementMap = webService.getGlobalTypeElementMap();
        Map<XmlName, XmlName> globalElementTypeMap = webService.getGlobalElementTypeMap();
        Map<XmlName, WebServiceOperation> webServiceOperationsMap = webService.getOperationsMap();
        Map<XmlName, WebServiceMessage> messagesMap = webService.getMessagesMap();
        Map<XmlName, WebServiceEnum> webServiceEnumsMap = webService.getEnumsMap();
        Map<XmlName, WebServiceType> webServiceTypesMap = webService.getTypesMap();
        for (WebServiceOperation operation : webServiceOperationsMap.values()) {
            XmlName messageXmlName = operation.getInputMessage();
            if (messageXmlName == null) {
                continue;
            }
            WebServiceMessage message = messagesMap.get(messageXmlName);
            if (message == null) {
                continue;
            }
            for (WebServiceMessagePart part : message.getParts()) {
                XmlName xmlTypeName = globalElementTypeMap.get(part.getElement());
                if (xmlTypeName == null) {
                    continue;
                }
                WebServiceType webServiceType = webServiceTypesMap.get(xmlTypeName);
                if (webServiceType == null) {
                    continue;
                }
                String typeJavaName = webServiceType.getJavaName();
                if (StringUtils.isEmpty(typeJavaName)) {
                    continue;
                }
                if (!typeJavaName.endsWith("Request")) {
                    if (typeJavaName.endsWith("Req")) {
                        webServiceType.setJavaName(typeJavaName + "uest");
                    } else {
                        webServiceType.setJavaName(typeJavaName + "Request");
                    }
                }
            }
        }

        for (WebServiceEnum enumType : webServiceEnumsMap.values()) {
            String enumTypeJavaName = enumType.getJavaName();

            File file = new File(beansDir, enumTypeJavaName + ".java");
            Writer writer = new FileWriter(file);

            writer.write("package ");
            writer.write(packageName);
            writer.write(".bean;");
            writer.write("\n\n");
            writer.write(
                    "import javax.xml.bind.annotation.XmlEnum;\n" +
                            "import javax.xml.bind.annotation.XmlEnumValue;\n" +
                            "import java.util.HashMap;\n" +
                            "import java.util.Map;\n" +
                            "\n"
            );
            if (writeJaxbAnnotations) {
                writer.write(
                        "@XmlEnum\n"
                );
            }
            writer.write(
                    "public enum "
            );
            writer.write(enumTypeJavaName);
            writer.write(" {\n\n");
            int idx = 1;
            for (WebServiceEnumValue enumValue : enumType.getValues()) {
                if (idx > 1) {
                    writer.write(",\n");
                }
                String value = enumValue.getValue();
                if (writeJaxbAnnotations) {
                    writer.write("    @XmlEnumValue(\"");
                    writer.write(value);
                    writer.write("\")\n");
                }
                writer.write("    ");
                writer.write(enumValue.getJavaName());
                writer.write("(\"");
                writer.write(value);
                writer.write("\")");
                ++idx;
            }
            writer.write(";\n\n");
            writer.write(
                    "    private static Map<String, ");
            writer.write(enumTypeJavaName);
            writer.write("> map;\n" +
                    "    private String code;\n" +
                    "\n" +
                    "    static {\n" +
                    "        map = new HashMap<String, ");
            writer.write(enumTypeJavaName);
            writer.write(
                    ">();\n" +
                            "        for ("
            );
            writer.write(enumTypeJavaName);
            writer.write(" value : ");
            writer.write(enumTypeJavaName);
            writer.write(
                    ".values()) {\n" +
                            "            map.put(value.code, value);\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    "
            );
            writer.write(enumTypeJavaName);
            writer.write(
                    "(String code) {\n" +
                            "        this.code = code;\n" +
                            "    }\n" +
                            "\n" +
                            "    public String getCode() {\n" +
                            "        return code;\n" +
                            "    }\n" +
                            "\n" +
                            "    public static "
            );
            writer.write(enumTypeJavaName);
            writer.write(
                    " getByCode(String code) {\n" +
                            "        return map.get(code);\n" +
                            "    }\n\n"
            );
            writer.write("}\n");

            writer.flush();
            writer.close();
        }

        for (WebServiceType type : webServiceTypesMap.values()) {
            String typeJavaName = type.getJavaName();
            XmlName typeXmlName = type.getXmlName();

            File file = new File(beansDir, typeJavaName + ".java");
            Writer writer = new FileWriter(file);

            writer.write("package ");
            writer.write(packageName);
            writer.write(".bean;");
            writer.write("\n\n");

            TreeSet<String> imports = new TreeSet<String>();
            imports.add("comtech.util.xml.ReadXml");
            imports.add("comtech.util.xml.read.DocumentXmlStreamReader2");
            imports.add("comtech.util.xml.XmlName");
            imports.add("javax.xml.stream.XMLStreamException");
            imports.add("comtech.util.xml.WriteXml");
            imports.add("comtech.util.xml.write.DocumentXmlStreamWriter2");
            imports.add("java.io.IOException");

            Map<XmlName, String> constantsMap = new HashMap<XmlName, String>();
            Set<String> writeXmlNamespaces = new TreeSet<String>();

            StringBuilder constants = new StringBuilder();
            StringBuilder fields = new StringBuilder();
            StringBuilder gettersSetters = new StringBuilder();
            StringBuilder readXmlAttributes = new StringBuilder();
            StringBuilder readXmlElements = new StringBuilder();
            StringBuilder readXmlValue = new StringBuilder();
            StringBuilder writeXmlElements = new StringBuilder();
            StringBuilder writeXmlAttributes = new StringBuilder();
            StringBuilder writeXmlValue = new StringBuilder();
            StringBuilder toStringMethod = new StringBuilder();

            toStringMethod.append("    @Override\n");
            toStringMethod.append("    public String toString() {\n");
            toStringMethod.append("        final StringBuilder sb = new StringBuilder();\n");
            toStringMethod.append("        sb.append(\"<");
            toStringMethod.append(typeJavaName);
            toStringMethod.append(">\\n\");\n");
            toStringMethod.append("        toString(sb);\n");
            toStringMethod.append("        sb.append(\"</");
            toStringMethod.append(typeJavaName);
            toStringMethod.append(">\\n\");\n");
            toStringMethod.append("        return sb.toString();\n");
            toStringMethod.append("    }\n");
            toStringMethod.append("\n");
            toStringMethod.append("    public void toString(StringBuilder sb) {\n");

            String superTypeJavaName = null;
            XmlName superTypeXmlName = type.getSuperTypeXmlName();
            if (superTypeXmlName != null) {
                WebServiceType superType = webServiceTypesMap.get(superTypeXmlName);
                if (superType != null) {
                    superTypeJavaName = superType.getJavaName();
                }
            }

            if (!StringUtils.isEmpty(superTypeJavaName)) {
                toStringMethod.append("        super.toString(sb);\n");
            }

            List<WebServiceTypeField> typeFields = type.getFields();
            for (WebServiceTypeField field : typeFields) {
                XmlName fieldXmlType = field.getXmlType();
                XsdWebServiceType fieldXsdType = XSD_JAVA_TYPE_MAP.get(fieldXmlType);

                String fieldTypeJavaName = null;
                String fieldJaxbXmlSchema = null;
                String fieldJavaConverter = null;
                String fieldXmlConverter = null;
                boolean fieldTypeEnum = false;
                if (fieldXsdType != null) {
                    fieldJaxbXmlSchema = fieldXsdType.getJaxbXmlSchema();
                    fieldTypeJavaName = fieldXsdType.getJavaName();
                    imports.addAll(fieldXsdType.getImports());
                    fieldJavaConverter = fieldXsdType.getJavaConverter();
                    fieldXmlConverter = fieldXsdType.getXmlConverter();
                }

                if (StringUtils.isEmpty(fieldTypeJavaName)) {
                    WebServiceType fieldType = webServiceTypesMap.get(fieldXmlType);
                    if (fieldType != null) {
                        fieldTypeJavaName = fieldType.getJavaName();
                    } else {
                        WebServiceEnum enumFieldType = webServiceEnumsMap.get(fieldXmlType);
                        if (enumFieldType != null) {
                            fieldTypeJavaName = enumFieldType.getJavaName();
                            fieldTypeEnum = true;
                        }
                    }
                }

                if (!StringUtils.isEmpty(fieldTypeJavaName)) {
                    if (field.isArray()) {
//                        imports.add("import java.util.List;\n");
                        imports.add("java.util.ArrayList");
                    }

                    // fields
                    boolean valueField = field.isValueField();
                    boolean elementField = field.isElementField();
                    boolean arrayField = field.isArray();
                    boolean nillableField = field.isNillable();
                    XmlName fieldXmlName = field.getXmlName();
                    String fieldNamespaceURI = null;
                    String fieldXmlNameLocalPart = null;
                    if (fieldXmlName != null) {
                        fieldNamespaceURI = fieldXmlName.getNamespaceURI();
                        fieldXmlNameLocalPart = fieldXmlName.getLocalPart();
                    }

                    if (writeJaxbAnnotations) {
                        if (valueField) {
                            imports.add("javax.xml.bind.annotation.XmlValue");
                            fields.append("    @XmlValue\n");
                        } else {
                            if (elementField) {
                                imports.add("javax.xml.bind.annotation.XmlElement");
                                fields.append("    @XmlElement(name = \"");
                            } else {
                                imports.add("javax.xml.bind.annotation.XmlAttribute");
                                fields.append("    @XmlAttribute(name = \"");
                            }
                            fields.append(fieldXmlNameLocalPart);
                            fields.append('"');
                            if (!StringUtils.isEmpty(fieldNamespaceURI)) {
                                fields.append(", namespace = \"");
                                fields.append(fieldNamespaceURI);
                                fields.append('"');
                            }
                            if (nillableField) {
                                fields.append(", nillable = true");
                            }
                            if (field.isRequired()) {
                                fields.append(", required = true");
                            }
                            fields.append(")\n");
                        }
                        if (!StringUtils.isEmpty(fieldJaxbXmlSchema)) {
                            imports.add("javax.xml.bind.annotation.XmlSchemaType");
                            fields.append("    @XmlSchemaType(name = \"");
                            fields.append(fieldJaxbXmlSchema);
                            fields.append("\")\n");
                        }
                    }

                    String fieldJavaName = field.getJavaName();

                    if (!arrayField) {
                        fields.append("    private ");
                        fields.append(fieldTypeJavaName);
                        fields.append(' ');
                        fields.append(fieldJavaName);
                        fields.append(";\n\n");

                        // getters/settes
                        gettersSetters.append("    public ");
                        gettersSetters.append(fieldTypeJavaName);
                        gettersSetters.append(" get");
                        gettersSetters.append(StringUtils.capitalize(fieldJavaName));
                        gettersSetters.append("() {\n");
                        gettersSetters.append("        return ");
                        gettersSetters.append(fieldJavaName);
                        gettersSetters.append(";\n");
                        gettersSetters.append("    }\n\n");

                        gettersSetters.append("    public void set");
                        gettersSetters.append(StringUtils.capitalize(fieldJavaName));
                        gettersSetters.append('(');
                        gettersSetters.append(fieldTypeJavaName);
                        gettersSetters.append(' ');
                        gettersSetters.append(fieldJavaName);
                        gettersSetters.append(") {\n");
                        gettersSetters.append("        this.");
                        gettersSetters.append(fieldJavaName);
                        gettersSetters.append(" = ");
                        gettersSetters.append(fieldJavaName);
                        gettersSetters.append(";\n");
                        gettersSetters.append("    }\n\n");
                    } else {
                        fields.append("    private ArrayList<");
                        fields.append(fieldTypeJavaName);
                        fields.append("> ");
                        fields.append(fieldJavaName);
                        fields.append(" = new ArrayList<");
                        fields.append(fieldTypeJavaName);
                        fields.append(">();\n\n");

                        // getters/settes
                        gettersSetters.append("    public ArrayList<");
                        gettersSetters.append(fieldTypeJavaName);
                        gettersSetters.append("> get");
                        gettersSetters.append(StringUtils.capitalize(fieldJavaName));
                        gettersSetters.append("() {\n");
                        gettersSetters.append("        return ");
                        gettersSetters.append(fieldJavaName);
                        gettersSetters.append(";\n");
                        gettersSetters.append("    }\n\n");
                    }

                    // readXml

                    String constantName = constantsMap.get(fieldXmlName);
                    if (StringUtils.isEmpty(constantName) && !valueField) {
                        constantName = "XML_NAME_" + StringUtils.toEnumName(fieldJavaName);
                        constantsMap.put(fieldXmlName, constantName);
                        constants.append("    public static final XmlName ");
                        constants.append(constantName);
                        constants.append(" = new XmlName(\"");
                        if (fieldNamespaceURI != null) {
                            constants.append(fieldNamespaceURI);
                            constants.append("\", \"");
                        }
                        constants.append(fieldXmlNameLocalPart);
                        constants.append("\");\n");
                    }

                    boolean fieldJavaConverterNotEmpty = !StringUtils.isEmpty(fieldJavaConverter);
                    if (elementField) {
                        if (readXmlElements.length() == 0) {
                            readXmlElements.append(
                                    "        while (reader.readNext()) {\n" +
                                            "            if (reader.elementEnded(elementName)) {\n" +
                                            "                break;\n"
                            );
                        }
                        if (fieldXsdType != null) {
                            readXmlElements.append("            } else if (reader.elementStarted(");
                            readXmlElements.append(constantName);
                            readXmlElements.append(")) {\n                ");
                            if (arrayField) {
                                readXmlElements.append(fieldTypeJavaName);
                                readXmlElements.append(" ");
                                readXmlElements.append(fieldJavaName);
                                readXmlElements.append("Item = ");
                            } else {
                                readXmlElements.append(fieldJavaName);
                                readXmlElements.append(" = ");
                            }
                            if (fieldJavaConverterNotEmpty) {
                                readXmlElements.append(fieldJavaConverter);
                                readXmlElements.append("(");
                            }
                            readXmlElements.append("reader.readCharacters(");
                            readXmlElements.append(constantName);
                            if (fieldJavaConverterNotEmpty) {
                                readXmlElements.append(")");
                            }
                            readXmlElements.append(");\n");
                            if (arrayField) {
                                readXmlElements.append("                if (");
                                readXmlElements.append(fieldJavaName);
                                readXmlElements.append("Item != null) {\n");
                                readXmlElements.append("                    ");
                                readXmlElements.append(fieldJavaName);
                                readXmlElements.append(".add(");
                                readXmlElements.append(fieldJavaName);
                                readXmlElements.append("Item);\n");
                                readXmlElements.append("                }\n");
                            }
                        } else {
                            readXmlElements.append("            } else if (reader.elementStarted(");
                            readXmlElements.append(constantName);
                            readXmlElements.append(")) {\n                ");
                            if (arrayField) {
                                readXmlElements.append(fieldTypeJavaName);
                                readXmlElements.append(" ");
                                readXmlElements.append(fieldJavaName);
                                readXmlElements.append("Item = new ");
                            } else {
                                readXmlElements.append(fieldJavaName);
                                readXmlElements.append(" = new ");
                            }
                            readXmlElements.append(fieldTypeJavaName);
                            readXmlElements.append("();\n                ");
                            readXmlElements.append(fieldJavaName);
                            if (arrayField) {
                                readXmlElements.append("Item");
                            }
                            if (fieldTypeEnum) {
                                readXmlElements.append(" = ");
                                readXmlElements.append(fieldTypeJavaName);
                                readXmlElements.append(".getByCode(");
                                readXmlValue.append("reader.readCharacters(elementName)");
                                readXmlElements.append(");\n");
                            } else {
                                readXmlElements.append(".readXml(reader, ");
                                readXmlElements.append(constantName);
                                readXmlElements.append(");\n");
                            }
                            if (arrayField) {
                                readXmlElements.append("                ");
                                readXmlElements.append(fieldJavaName);
                                readXmlElements.append(".add(");
                                readXmlElements.append(fieldJavaName);
                                readXmlElements.append("Item");
                                readXmlElements.append(");\n");
                            }
                        }
                    } else if (valueField) {
                        readXmlValue.append("        value = ");
                        if (fieldJavaConverterNotEmpty) {
                            readXmlValue.append(fieldJavaConverter);
                            readXmlValue.append("(");
                        } else if (fieldTypeEnum) {
                            readXmlValue.append(fieldTypeJavaName);
                            readXmlValue.append(".getByCode(");
                        }
                        readXmlValue.append("reader.readCharacters(elementName)");
                        if (fieldJavaConverterNotEmpty || fieldTypeEnum) {
                            readXmlValue.append(")");
                        }
                        readXmlValue.append(";\n");
                    } else {
                        if (readXmlAttributes.length() == 0) {
                            imports.add("comtech.util.props.StringMapProperties");
                            readXmlAttributes.append("        StringMapProperties attributes = reader.getAttributes();\n");
                        }
                        readXmlAttributes.append("        ");
                        readXmlAttributes.append(fieldJavaName);
                        readXmlAttributes.append(" = ");
                        if (fieldJavaConverterNotEmpty) {
                            readXmlAttributes.append(fieldJavaConverter);
                            readXmlAttributes.append("(");
                        } else if (fieldTypeEnum) {
                            readXmlAttributes.append(fieldTypeJavaName);
                            readXmlAttributes.append(".getByCode(");
                        }
                        readXmlAttributes.append("attributes.get(");
                        readXmlAttributes.append(constantName);
                        readXmlAttributes.append(".toString())");
                        if (fieldJavaConverterNotEmpty || fieldTypeEnum) {
                            readXmlAttributes.append(")");
                        }
                        readXmlAttributes.append(";\n");
                    }

                    // writeXml
                    boolean fieldXmlConverterNotEmpty = !StringUtils.isEmpty(fieldXmlConverter);
                    if (elementField) {
                        if (arrayField) {
                            writeXmlElements.append("        if (!");
                            writeXmlElements.append(fieldJavaName);
                            writeXmlElements.append(".isEmpty()) {\n");
                            writeXmlElements.append("            for (");
                            writeXmlElements.append(fieldTypeJavaName);
                            writeXmlElements.append(" ");
                            writeXmlElements.append(fieldJavaName);
                            writeXmlElements.append("Item : ");
                            writeXmlElements.append(fieldJavaName);
                            writeXmlElements.append(") {\n");
                            if (fieldXsdType != null) {
                                writeXmlElements.append("                writer.element(");
                                writeXmlElements.append(constantName);
                                writeXmlElements.append(", ");
                                if (fieldXmlConverterNotEmpty) {
                                    writeXmlElements.append(fieldXmlConverter);
                                    writeXmlElements.append("(");
                                }
                                writeXmlElements.append(fieldJavaName);
                                writeXmlElements.append("Item");
                                if (fieldXmlConverterNotEmpty) {
                                    writeXmlElements.append(")");
                                }
                                writeXmlElements.append(");\n");
                            } else {
                                writeXmlElements.append("                if (");
                                writeXmlElements.append(fieldJavaName);
                                writeXmlElements.append(" != null) {\n");
                                writeXmlElements.append("                    ");
                                writeXmlElements.append(fieldJavaName);
                                writeXmlElements.append("Item.writeXml(writer, ");
                                writeXmlElements.append(constantName);
                                writeXmlElements.append(");\n");
                                writeXmlElements.append("                }\n");
                            }
                            writeXmlElements.append("            }\n");
                            if (nillableField) {
                                imports.add("comtech.util.xml.XmlConstants");
                                writeXmlNamespaces.add(XmlConstants.NAMESPACE_URI_XSI);
                                writeXmlElements.append("      } else {\n");
                                writeXmlElements.append("          writer.startElement(");
                                writeXmlElements.append(constantName);
                                writeXmlElements.append(");\n");
                                writeXmlElements.append("          writer.attribute(XmlConstants.XML_NAME_XSI_NIL, \"true\");\n");
                                writeXmlElements.append("          writer.endElement();\n");
                            }
                            writeXmlElements.append("        }\n");
                        } else {
                            writeXmlElements.append("        if (");
                            writeXmlElements.append(fieldJavaName);
                            writeXmlElements.append(" != null) {\n");
                            if (fieldXsdType != null) {
                                writeXmlElements.append("            writer.element(");
                                writeXmlElements.append(constantName);
                                writeXmlElements.append(", ");
                                if (fieldXmlConverterNotEmpty) {
                                    writeXmlElements.append(fieldXmlConverter);
                                    writeXmlElements.append("(");
                                }
                                writeXmlElements.append(fieldJavaName);
                                if (fieldXmlConverterNotEmpty) {
                                    writeXmlElements.append(")");
                                }
                                writeXmlElements.append(");\n");
                            } else {
                                writeXmlElements.append("            ");
                                writeXmlElements.append(fieldJavaName);
                                writeXmlElements.append(".writeXml(writer, ");
                                writeXmlElements.append(constantName);
                                writeXmlElements.append(");\n");
                            }
                            if (nillableField) {
                                imports.add("comtech.util.xml.XmlConstants");
                                writeXmlNamespaces.add(XmlConstants.NAMESPACE_URI_XSI);
                                writeXmlElements.append("        } else {\n");
                                writeXmlElements.append("            writer.startElement(");
                                writeXmlElements.append(constantName);
                                writeXmlElements.append(");\n");
                                writeXmlElements.append("            writer.attribute(XmlConstants.XML_NAME_XSI_NIL, \"true\");\n");
                                writeXmlElements.append("            writer.endElement();\n");
                            }
                            writeXmlElements.append("        }\n");
                        }
                    } else if (valueField) {
                        if (fieldTypeEnum) {
                            writeXmlValue.append("        if(");
                            writeXmlValue.append(fieldJavaName);
                            writeXmlValue.append(" != null) {\n");
                            writeXmlValue.append("            writer.text(");
                            writeXmlValue.append(fieldJavaName);
                            writeXmlValue.append(".getCode());\n");
                            writeXmlValue.append("        }\n");
                        } else {
                            writeXmlValue.append("        writer.text(");
                            if (fieldXmlConverterNotEmpty) {
                                writeXmlValue.append(fieldXmlConverter);
                                writeXmlValue.append("(");
                            }
                            writeXmlValue.append(fieldJavaName);
                            writeXmlValue.append(")");
                            if (fieldXmlConverterNotEmpty) {
                                writeXmlValue.append(")");
                            }
                            writeXmlValue.append(";\n");
                        }
                    } else {
                        if (fieldTypeEnum) {
                            writeXmlAttributes.append("        if(");
                            writeXmlAttributes.append(fieldJavaName);
                            writeXmlAttributes.append(" != null) {\n");
                            writeXmlAttributes.append("            writer.attribute(");
                            writeXmlAttributes.append(constantName);
                            writeXmlAttributes.append(", ");
                            writeXmlAttributes.append(fieldJavaName);
                            writeXmlAttributes.append(".getCode());\n");
                            writeXmlAttributes.append("        }\n");
                        } else {
                            writeXmlAttributes.append("        writer.attribute(");
                            writeXmlAttributes.append(constantName);
                            writeXmlAttributes.append(", ");
                            if (fieldXmlConverterNotEmpty) {
                                writeXmlAttributes.append(fieldXmlConverter);
                                writeXmlAttributes.append("(");
                            }
                            writeXmlAttributes.append(fieldJavaName);
                            writeXmlAttributes.append(")");
                            if (fieldXmlConverterNotEmpty) {
                                writeXmlAttributes.append(")");
                            }
                            writeXmlAttributes.append(";\n");
                        }
                    }

                    // toString method fields

                    if (field.isArray()) {
                        toStringMethod.append("        if (");
                        toStringMethod.append(fieldJavaName);
                        toStringMethod.append(" != null) {\n");
                        toStringMethod.append("            sb.append(\"<");
                        toStringMethod.append(fieldJavaName);
                        toStringMethod.append(">\");\n");
                        toStringMethod.append("            for (Object obj : ");
                        toStringMethod.append(fieldJavaName);
                        toStringMethod.append(") {\n");
                        toStringMethod.append("                sb.append(\"<item>\");\n");
                        toStringMethod.append("                sb.append(obj);\n");
                        toStringMethod.append("                sb.append(\"</item>\\n\");\n");
                        toStringMethod.append("            }\n");
                        toStringMethod.append("            sb.append(\"</");
                        toStringMethod.append(fieldJavaName);
                        toStringMethod.append(">\\n\");\n");
                        toStringMethod.append("        } else {\n");
                        toStringMethod.append("            sb.append(\"<");
                        toStringMethod.append(fieldJavaName);
                        toStringMethod.append("/>\\n\");\n");
                        toStringMethod.append("        }\n");
                    } else {
                        toStringMethod.append("        sb.append(\"<");
                        toStringMethod.append(fieldJavaName);
                        toStringMethod.append(">\");\n");
                        toStringMethod.append("        sb.append(");
                        toStringMethod.append(fieldJavaName);
                        toStringMethod.append(");\n");
                        toStringMethod.append("        sb.append(\"</");
                        toStringMethod.append(fieldJavaName);
                        toStringMethod.append(">\\n\");\n");
                    }
                }
            }

            toStringMethod.append("    }\n");
            toStringMethod.append("\n");

            StringBuilder classJaxbAnnotations = new StringBuilder();

            if (writeJaxbAnnotations) {
                XmlName rootElement = globalTypeElementMap.get(typeXmlName);
                if (rootElement != null) {
                    imports.add("javax.xml.bind.annotation.XmlRootElement");
                    classJaxbAnnotations.append("@XmlRootElement(name = \"");
                    classJaxbAnnotations.append(rootElement.getLocalPart());
                    classJaxbAnnotations.append('"');
                    String rootElementNamespaceURI = rootElement.getNamespaceURI();
                    if (!StringUtils.isEmpty(rootElementNamespaceURI)) {
                        classJaxbAnnotations.append(", namespace = \"");
                        classJaxbAnnotations.append(rootElementNamespaceURI);
                        classJaxbAnnotations.append('"');
                    }
                    classJaxbAnnotations.append(")\n");
                }
                imports.add("javax.xml.bind.annotation.XmlAccessorType");
                imports.add("javax.xml.bind.annotation.XmlAccessType");
                classJaxbAnnotations.append("@XmlAccessorType(XmlAccessType.FIELD)\n");
            }

            for (String className : imports) {
                writer.write("import ");
                writer.write(className);
                writer.write(";\n");
            }

            writer.write("\n");

            writer.write(classJaxbAnnotations.toString());

            writer.write("public class ");
            writer.write(typeJavaName);
            writer.write(" implements ReadXml, WriteXml {\n\n");
            if (constants.length() > 0) {
                writer.write(constants.toString());
                writer.write("\n");
            }
            writer.write(fields.toString());
            writer.write(gettersSetters.toString());

            writer.write(
                    "    public void readXml(\n" +
                            "            DocumentXmlStreamReader2 reader, XmlName elementName\n" +
                            "    ) throws XMLStreamException {\n"
            );
            writer.write(readXmlAttributes.toString());
            if (readXmlElements.length() != 0) {
                writer.write(readXmlElements.toString());
                writer.write(
                        "            }\n" +
                                "        }\n"
                );
            }
            writer.write(readXmlValue.toString());
            writer.write(
                    "    }\n\n"
            );

            writer.write(
                    "    public void writeXml(\n" +
                            "            DocumentXmlStreamWriter2 writer, XmlName elementName\n" +
                            "    ) throws IOException {\n");
            for (String namespace : writeXmlNamespaces) {
                writer.write("        writer.declareNamespace(\"");
                writer.write(namespace);
                writer.write("\");\n");
            }
            writer.write(
                    "        writer.startElement(elementName);\n"
            );

            writer.write(writeXmlAttributes.toString());
            writer.write(writeXmlElements.toString());
            writer.write(writeXmlValue.toString());

            writer.write(
                    "        writer.endElement();\n" +
                            "    }\n\n"
            );

            writer.write(toStringMethod.toString());
            writer.write("}\n");

            writer.flush();
            writer.close();
        }
    }

}
