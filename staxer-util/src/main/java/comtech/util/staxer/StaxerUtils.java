package comtech.util.staxer;

import comtech.util.staxer.domain.*;
import comtech.util.ResourceUtils;
import comtech.util.StringUtils;
import comtech.util.file.FileUtils;
import comtech.util.xml.StaxerXmlStreamReader;
import comtech.util.xml.XmlConstants;
import comtech.util.xml.XmlName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.*;

import static comtech.util.StringUtils.capitalize2;
import static comtech.util.xml.XmlConstants.NAMESPACE_URI_XSD;


/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-19 16:57 (Europe/Moscow)
 */
public class StaxerUtils {

    private static Logger log = LoggerFactory.getLogger(StaxerUtils.class);

    public static Map<XmlName, XmlSchemaXsdType> XSD_JAVA_TYPE_MAP;

    static {
        XSD_JAVA_TYPE_MAP = new HashMap<XmlName, XmlSchemaXsdType>();
        XmlSchemaXsdType javaXsdType = new XmlSchemaXsdType();
        javaXsdType.setJavaName("String");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "string"), javaXsdType);
        javaXsdType = new XmlSchemaXsdType();
        javaXsdType.setJavaName("BigDecimal");
        javaXsdType.getImports().add("java.math.BigDecimal");
        javaXsdType.getImports().add("comtech.util.NumberUtils");
        javaXsdType.setJavaConverter("NumberUtils.parseBigDecimal");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "decimal"), javaXsdType);
        javaXsdType = new XmlSchemaXsdType();
        javaXsdType.setJavaName("Integer");
        javaXsdType.getImports().add("comtech.util.NumberUtils");
        javaXsdType.setJavaConverter("NumberUtils.parseInteger");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "unsignedShort"), javaXsdType);
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "int"), javaXsdType);
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "integer"), javaXsdType);
        javaXsdType = new XmlSchemaXsdType();
        javaXsdType.setJavaName("Long");
        javaXsdType.getImports().add("comtech.util.NumberUtils");
        javaXsdType.setJavaConverter("NumberUtils.parseLong");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "long"), javaXsdType);
        javaXsdType = new XmlSchemaXsdType();
        javaXsdType.setJavaName("Float");
        javaXsdType.getImports().add("comtech.util.NumberUtils");
        javaXsdType.setJavaConverter("NumberUtils.parseFloat");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "float"), javaXsdType);
        javaXsdType = new XmlSchemaXsdType();
        javaXsdType.setJavaName("Double");
        javaXsdType.getImports().add("comtech.util.NumberUtils");
        javaXsdType.setJavaConverter("NumberUtils.parseDouble");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "double"), javaXsdType);
        javaXsdType = new XmlSchemaXsdType();
        javaXsdType.setJavaName("Boolean");
        javaXsdType.getImports().add("comtech.util.StringUtils");
        javaXsdType.setJavaConverter("StringUtils.parseBooleanInstance");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "boolean"), javaXsdType);
        javaXsdType = new XmlSchemaXsdType();
        javaXsdType.setJavaName("byte[]");
        javaXsdType.getImports().add("org.apache.commons.codec.binary.Base64");
        javaXsdType.setJavaConverter("Base64.decodeBase64");
        javaXsdType.setXmlConverter("Base64.encodeBase64String");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "base64Binary"), javaXsdType);
        javaXsdType = new XmlSchemaXsdType();
        javaXsdType.setJavaName("Date");
        javaXsdType.getImports().add("java.util.Date");
        javaXsdType.getImports().add("comtech.util.date.DateTimeUtils");
        javaXsdType.setJaxbXmlSchema("dateTime");
        javaXsdType.setJavaConverter("DateTimeUtils.parseXmlDate");
        javaXsdType.setXmlConverter("DateTimeUtils.formatXmlDate");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "dateTime"), javaXsdType);
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "date"), javaXsdType);
    }

    public static void createJavaWebService(
            WebService webService, File sourceDir,
            String packageName, boolean writeJaxbAnnotations,
            boolean createClientService, boolean createServerService
    ) throws IOException {
        File destDir = new File(sourceDir, packageName.replaceAll("\\.", "/"));
        File beansDir = new File(destDir, "bean");
        FileUtils.createOrCleanupDir(beansDir, log);

        String beanPackageName = packageName + ".bean";

        Map<XmlName, WebServiceMessage> webServiceMessagesMap = webService.getMessagesMap();
        Map<XmlName, WebServiceOperation> webServiceOperationsMap = webService.getOperationsMap();
        Map<XmlName, WebServiceMessage> messagesMap = webService.getMessagesMap();

        Map<XmlName, XmlSchemaType> webServiceTypesMap = new HashMap<XmlName, XmlSchemaType>();
        Map<XmlName, XmlName> globalElementTypeMap = new HashMap<XmlName, XmlName>();

        List<XmlSchema> xmlSchemaList = webService.getXmlSchemaList();
        for (XmlSchema xmlSchema : xmlSchemaList) {
            globalElementTypeMap.putAll(xmlSchema.getGlobalElementTypeMap());
            webServiceTypesMap.putAll(xmlSchema.getTypesMap());
        }

        for (WebServiceOperation operation : webServiceOperationsMap.values()) {
            XmlName operationXmlName = operation.getName();
            log.info("Pre processing ws-operation:" + operationXmlName);
            XmlName inputMessageName = operation.getInputMessage();
            if (inputMessageName == null) {
                continue;
            }
            WebServiceMessage inputMessage = messagesMap.get(inputMessageName);
            if (inputMessage == null) {
                continue;
            }
            for (WebServiceMessagePart part : inputMessage.getParts()) {
                XmlName xmlTypeName = globalElementTypeMap.get(part.getElement());
                if (xmlTypeName == null) {
                    continue;
                }
                XmlSchemaType xmlSchemaType = webServiceTypesMap.get(xmlTypeName);
                if (xmlSchemaType == null) {
                    continue;
                }
                String typeJavaName = xmlSchemaType.getJavaName();
                if (StringUtils.isEmpty(typeJavaName)) {
                    continue;
                }
                if (!typeJavaName.endsWith("Request")) {
                    if (typeJavaName.endsWith("Req")) {
                        xmlSchemaType.setJavaName(typeJavaName + "uest");
                    } else {
                        xmlSchemaType.setJavaName(typeJavaName + "Request");
                    }
                }
            }
        }

        Set<XmlName> readHttpParametersTypes = createXsdBeansPrivate(
                xmlSchemaList, beansDir, beanPackageName, writeJaxbAnnotations
        );

        if (createClientService || createServerService) {
            String webServiceName = capitalize2(webService.getName(), false);
            if (webServiceName.toUpperCase().endsWith("SERVICEWSSERVICE")) {
                webServiceName = StringUtils.substring(webServiceName, 0, -7);
            } else if (!webServiceName.toUpperCase().endsWith("WS")) {
                webServiceName += "Ws";
            }
            log.info("Web service name: " + webServiceName);
            StringBuilder clientXmlNames = new StringBuilder();
            StringBuilder serverXmlNames = new StringBuilder();
            StringBuilder clientServiceMethods = new StringBuilder();
            StringBuilder serverServiceStaticConstrustor = new StringBuilder();
            StringBuilder serverServiceMethods = new StringBuilder();

            for (WebServiceOperation operation : webServiceOperationsMap.values()) {
                XmlName operationXmlName = operation.getName();
                log.info("Processing ws-operation:" + operationXmlName);
                XmlName outputMessageName = operation.getOutputMessage();
                WebServiceMessage outputMessage = webServiceMessagesMap.get(outputMessageName);
                List<WebServiceMessagePart> outputParts = outputMessage.getParts();
                if (outputParts.isEmpty()) {
                    continue;
                }
                WebServiceMessagePart firstOutputPart = outputParts.get(0);
                XmlName outputElement = firstOutputPart.getElement();
                XmlName outputTypeName = globalElementTypeMap.get(outputElement);
                XmlSchemaType outputComplexType = webServiceTypesMap.get(outputTypeName);
                if (outputComplexType == null) {
                    continue;
                }
                XmlName inputMessageName = operation.getInputMessage();
                WebServiceMessage inputMessage = webServiceMessagesMap.get(inputMessageName);
                List<WebServiceMessagePart> inputParts = inputMessage.getParts();
                if (inputParts.isEmpty()) {
                    continue;
                }
                WebServiceMessagePart firstInputPart = inputParts.get(0);
                XmlName inputElement = firstInputPart.getElement();
                XmlName inputTypeName = globalElementTypeMap.get(inputElement);
                XmlSchemaType inputComplexType = webServiceTypesMap.get(inputTypeName);
                if (inputComplexType == null) {
                    continue;
                }
                String outputTypeJavaName = outputComplexType.getJavaName();
                String inputTypeJavaName = inputComplexType.getJavaName();
                String methodJavaName = operation.getJavaName();

                String inputElementLocalPart = inputElement.getLocalPart();
                String inputTypeConstantName = "XML_NAME_" + StringUtils.toEnumName(inputElementLocalPart);
                String outputElementLocalPart = outputElement.getLocalPart();
                String outputTypeConstantName = "XML_NAME_" + StringUtils.toEnumName(outputElementLocalPart);
                clientXmlNames.append("    public static final XmlName ");
                clientXmlNames.append(inputTypeConstantName);
                clientXmlNames.append(" = new XmlName(\"");
                clientXmlNames.append(inputElement.getNamespaceURI());
                clientXmlNames.append("\", \"");
                clientXmlNames.append(inputElementLocalPart);
                clientXmlNames.append("\");\n");
                serverXmlNames.append("    public static final XmlName ");
                serverXmlNames.append(inputTypeConstantName);
                serverXmlNames.append(" = new XmlName(\"");
                serverXmlNames.append(inputElement.getNamespaceURI());
                serverXmlNames.append("\", \"");
                serverXmlNames.append(inputElementLocalPart);
                serverXmlNames.append("\");\n");
                serverXmlNames.append("    public static final XmlName ");
                serverXmlNames.append(outputTypeConstantName);
                serverXmlNames.append(" = new XmlName(\"");
                serverXmlNames.append(outputElement.getNamespaceURI());
                serverXmlNames.append("\", \"");
                serverXmlNames.append(outputElementLocalPart);
                serverXmlNames.append("\");\n");

                if (createClientService) {
                    clientServiceMethods.append("    public ");
                    clientServiceMethods.append(outputTypeJavaName);
                    clientServiceMethods.append(" ");
                    clientServiceMethods.append(methodJavaName);
                    clientServiceMethods.append("(\n");
                    clientServiceMethods.append("            WsRequestHeader wsRequestHeader, ");
                    clientServiceMethods.append(inputTypeJavaName);
                    clientServiceMethods.append(" request, int readTimeout\n");
                    clientServiceMethods.append("    ) throws WsClientException {\n");
                    clientServiceMethods.append("        return httpWsClient.processSoapQuery(\n");
                    clientServiceMethods.append("                wsRequestHeader, request, ");
                    clientServiceMethods.append(inputTypeConstantName);
                    clientServiceMethods.append(", ");
                    clientServiceMethods.append(outputTypeJavaName);
                    clientServiceMethods.append(".class, readTimeout\n");
                    clientServiceMethods.append("        );\n");
                    clientServiceMethods.append("    }\n\n");
                }
                if (createServerService) {
                    serverServiceStaticConstrustor.append("\n        STAXER_READ_XML_CLASSES.put(");
                    serverServiceStaticConstrustor.append(inputTypeConstantName);
                    serverServiceStaticConstrustor.append(", ");
                    serverServiceStaticConstrustor.append(inputTypeJavaName);
                    serverServiceStaticConstrustor.append(".class);\n");
                    if (readHttpParametersTypes.contains(inputTypeName)) {
                        serverServiceStaticConstrustor.append("        READ_HTTP_PARAMETERS_CLASSES.put(");
                        serverServiceStaticConstrustor.append(inputTypeConstantName);
                        serverServiceStaticConstrustor.append(", ");
                        serverServiceStaticConstrustor.append(inputTypeJavaName);
                        serverServiceStaticConstrustor.append(".class);\n");
                    }
                    serverServiceStaticConstrustor.append("        METHOD_NAMES.put(");
                    serverServiceStaticConstrustor.append(inputTypeConstantName);
                    serverServiceStaticConstrustor.append(", \"");
                    serverServiceStaticConstrustor.append(methodJavaName);
                    serverServiceStaticConstrustor.append("\");\n");
                    serverServiceStaticConstrustor.append("        RESPONSE_XML_NAMES.put(");
                    serverServiceStaticConstrustor.append(inputTypeConstantName);
                    serverServiceStaticConstrustor.append(", ");
                    serverServiceStaticConstrustor.append(outputTypeConstantName);
                    serverServiceStaticConstrustor.append(");\n");

                    serverServiceMethods.append("    public abstract ");
                    serverServiceMethods.append(outputTypeJavaName);
                    serverServiceMethods.append(" ");
                    serverServiceMethods.append(methodJavaName);
                    serverServiceMethods.append("(\n");
                    serverServiceMethods.append("            WsMessage<");
                    serverServiceMethods.append(inputTypeJavaName);
                    serverServiceMethods.append("> wsMessage\n");
                    serverServiceMethods.append("    );\n");
                    serverServiceMethods.append("\n");

                }
            }

            if (createClientService) {
                String serviceName = "Client" + webServiceName;
                File file = new File(destDir, serviceName + ".java");
                Writer writer = new FileWriter(file);
                writer.append("package ");
                writer.append(packageName);
                writer.append(";\n\n");
                writer.append("import comtech.util.staxer.client.HttpWsClient;\n");
                writer.append("import comtech.util.staxer.client.WsClientException;\n");
                writer.append("import comtech.util.staxer.client.WsRequestHeader;\n");
                writer.append("import comtech.util.xml.XmlName;\n");
                writer.append("import ");
                writer.append(packageName);
                writer.append(".bean.*;\n\n");
                writer.append("\n");
                writer.append("public class ");
                writer.append(serviceName);
                writer.append(" {\n\n");
                writer.append(clientXmlNames.toString());
                writer.append("\n");
                writer.append("    private HttpWsClient httpWsClient;\n");
                writer.append("\n");
                writer.append("    public HttpWsClient getHttpWsClient() {\n");
                writer.append("        return httpWsClient;\n");
                writer.append("    }\n");
                writer.append("\n");
                writer.append("    public void setHttpWsClient(HttpWsClient httpWsClient) {\n");
                writer.append("        this.httpWsClient = httpWsClient;\n");
                writer.append("    }\n");
                writer.append("\n");
                writer.append("    // service related methods\n");
                writer.append("\n");
                writer.append(clientServiceMethods.toString());
                writer.append("}\n");
                writer.close();
            }
            if (createServerService) {
                String serviceName = "Server" + webServiceName;
                File file = new File(destDir, serviceName + ".java");
                Writer writer = new FileWriter(file);
                writer.append("package ");
                writer.append(packageName);
                writer.append(";\n");
                writer.append("\n");
                writer.append("import comtech.util.staxer.server.ServerServiceWs;\n");
                writer.append("import comtech.util.staxer.server.WsMessage;\n");
                writer.append("import comtech.util.http.helper.ReadHttpParameters;\n");
                writer.append("import comtech.util.xml.StaxerReadXml;\n");
                writer.append("import comtech.util.xml.XmlName;\n");
                writer.append("import ");
                writer.append(beanPackageName);
                writer.append(".*;\n");
                writer.append("\n");
                writer.append("import java.util.HashMap;\n");
                writer.append("import java.util.Map;\n");
                writer.append("\n");
                writer.append("public abstract class ");
                writer.append(serviceName);
                writer.append(" implements ServerServiceWs {\n\n");
                writer.append(serverXmlNames.toString());
                writer.append("\n");
                writer.append("    public static final Map<XmlName, Class<? extends StaxerReadXml>> STAXER_READ_XML_CLASSES;\n");
                writer.append("    public static final Map<XmlName, Class<? extends ReadHttpParameters>> READ_HTTP_PARAMETERS_CLASSES;\n");
                writer.append("    public static final Map<XmlName, String> METHOD_NAMES;\n");
                writer.append("    public static final Map<XmlName, XmlName> RESPONSE_XML_NAMES;\n\n");
                writer.append("    static {\n");
                writer.append("        STAXER_READ_XML_CLASSES = new HashMap<XmlName, Class<? extends StaxerReadXml>>();\n");
                writer.append("        READ_HTTP_PARAMETERS_CLASSES = new HashMap<XmlName, Class<? extends ReadHttpParameters>>();\n");
                writer.append("        METHOD_NAMES = new HashMap<XmlName, String>();\n");
                writer.append("        RESPONSE_XML_NAMES = new HashMap<XmlName, XmlName>();\n");
                writer.append(serverServiceStaticConstrustor.toString());
                writer.append("    }\n\n");
                writer.append("    public Class<? extends StaxerReadXml> getReadXmlClass(XmlName requestXmlName) {\n");
                writer.append("        return STAXER_READ_XML_CLASSES.get(requestXmlName);\n");
                writer.append("    }\n\n");
                writer.append("    public Class<? extends ReadHttpParameters> getReadHttpParametersClass(XmlName requestXmlName) {\n");
                writer.append("        return READ_HTTP_PARAMETERS_CLASSES.get(requestXmlName);\n");
                writer.append("    }\n\n");
                writer.append("    public String getMethodName(XmlName xmlName) {\n");
                writer.append("        return METHOD_NAMES.get(xmlName);\n");
                writer.append("    }\n\n");
                writer.append("    public XmlName getResponseXmlName(XmlName requestXmlName) {\n");
                writer.append("        return RESPONSE_XML_NAMES.get(requestXmlName);\n");
                writer.append("    }\n\n");
                writer.append(serverServiceMethods.toString());
                writer.append("}\n");
                writer.close();
            }
        }
    }

    public static void createXsdBeans(
            XmlSchema xmlSchema, File sourceDir,
            String packageName, boolean writeJaxbAnnotations
    ) throws IOException {
        if (xmlSchema != null) {
            File destDir = new File(sourceDir, packageName.replaceAll("\\.", "/"));
            FileUtils.createOrCleanupDir(destDir, log);
            createXsdBeansPrivate(Arrays.asList(xmlSchema), destDir, packageName, writeJaxbAnnotations);
        }
    }

    private static Set<XmlName> createXsdBeansPrivate(
            List<XmlSchema> xmlSchemaList, File beansDir,
            String packageName, boolean writeJaxbAnnotations
    ) throws IOException {
        Map<XmlName, XmlName> globalTypeElementMap = new HashMap<XmlName, XmlName>();
        Map<XmlName, XmlSchemaType> webServiceTypesMap = new HashMap<XmlName, XmlSchemaType>();

        for (XmlSchema xmlSchema : xmlSchemaList) {
            globalTypeElementMap.putAll(xmlSchema.getGlobalTypeElementMap());
            webServiceTypesMap.putAll(xmlSchema.getTypesMap());
        }

        XmlSchemaComplexType xsdAnyComplexType = new XmlSchemaComplexType();
        xsdAnyComplexType.setXmlName(XmlConstants.XML_NAME_XSI_ANY_TYPE);
        xsdAnyComplexType.setJavaName("ListStaxerXmlElement");
        xsdAnyComplexType.setJavaPackage("comtech.util.xml");
        webServiceTypesMap.put(XmlConstants.XML_NAME_XSI_ANY_TYPE, xsdAnyComplexType);

        Set<XmlName> readHttpParametersTypes = new HashSet<XmlName>();

        for (XmlSchemaType xmlSchemaType : webServiceTypesMap.values()) {
            if (xmlSchemaType.isComplexType()) {
                XmlSchemaComplexType xmlSchemaComplexType = (XmlSchemaComplexType) xmlSchemaType;
                XmlName typeXmlName = xmlSchemaType.getXmlName();
                if (XmlConstants.XML_NAME_XSI_ANY_TYPE.equals(typeXmlName)) {
                    continue;
                }
                boolean httpRequestReadable = true;
                for (XmlSchemaTypeField field : xmlSchemaComplexType.getFields()) {
                    XmlName fieldXmlType = field.getXmlType();
                    XmlSchemaType fieldXmlSchemaType = webServiceTypesMap.get(fieldXmlType);
                    if (!(XSD_JAVA_TYPE_MAP.containsKey(fieldXmlType)
                            || (fieldXmlSchemaType != null && !fieldXmlSchemaType.isComplexType()))) {
                        httpRequestReadable = false;
                        break;
                    }
                }
                if (httpRequestReadable) {
                    readHttpParametersTypes.add(typeXmlName);
                }
            }
        }

        for (XmlSchemaType xmlSchemaType : webServiceTypesMap.values()) {
            if (!xmlSchemaType.isComplexType()) {
                XmlSchemaSimpleType xmlSchemaSimpleType = (XmlSchemaSimpleType) xmlSchemaType;
                if (xmlSchemaSimpleType.getValues().isEmpty()) {
                    continue;
                }
                XmlName enumXmlName = xmlSchemaSimpleType.getXmlName();
                log.info("Processing ws-enum:" + enumXmlName);
                String enumTypeJavaName = xmlSchemaSimpleType.getJavaName();

                File file = new File(beansDir, enumTypeJavaName + ".java");
                Writer writer = new FileWriter(file);

                writer.append("package ");
                writer.append(packageName);
                writer.append(";");
                writer.append("\n\n");
                writer.append(
                        "import javax.xml.bind.annotation.XmlEnum;\n" +
                                "import javax.xml.bind.annotation.XmlEnumValue;\n" +
                                "import java.util.HashMap;\n" +
                                "import java.util.Map;\n" +
                                "\n"
                );
                if (writeJaxbAnnotations) {
                    writer.append(
                            "@XmlEnum\n"
                    );
                }
                writer.append(
                        "public enum "
                );
                writer.append(enumTypeJavaName);
                writer.append(" {\n\n");
                int idx = 1;
                for (XmlSchemaEnumValue enumValue : xmlSchemaSimpleType.getValues()) {
                    if (idx > 1) {
                        writer.append(",\n");
                    }
                    String value = enumValue.getValue();
                    if (writeJaxbAnnotations) {
                        writer.append("    @XmlEnumValue(\"");
                        writer.append(value);
                        writer.append("\")\n");
                    }
                    writer.append("    ");
                    writer.append(enumValue.getJavaName());
                    writer.append("(\"");
                    writer.append(value);
                    writer.append("\")");
                    ++idx;
                }
                writer.append(";\n\n");
                writer.append(
                        "    private static Map<String, ");
                writer.append(enumTypeJavaName);
                writer.append("> map;\n" +
                        "    private String code;\n" +
                        "\n" +
                        "    static {\n" +
                        "        map = new HashMap<String, ");
                writer.append(enumTypeJavaName);
                writer.append(
                        ">();\n" +
                                "        for ("
                );
                writer.append(enumTypeJavaName);
                writer.append(" value : ");
                writer.append(enumTypeJavaName);
                writer.append(
                        ".values()) {\n" +
                                "            map.put(value.code, value);\n" +
                                "        }\n" +
                                "    }\n" +
                                "\n" +
                                "    "
                );
                writer.append(enumTypeJavaName);
                writer.append(
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
                writer.append(enumTypeJavaName);
                writer.append(
                        " getByCode(String code) {\n" +
                                "        return map.get(code);\n" +
                                "    }\n\n"
                );
                writer.append("}\n");

                writer.flush();
                writer.close();
            } else {
                XmlSchemaComplexType xmlSchemaComplexType = (XmlSchemaComplexType) xmlSchemaType;
                XmlName typeXmlName = xmlSchemaComplexType.getXmlName();
                if (XmlConstants.XML_NAME_XSI_ANY_TYPE.equals(typeXmlName)) {
                    continue;
                }
                log.info("Processing ws-type:" + typeXmlName);
                String typeJavaName = xmlSchemaComplexType.getJavaName();

                File file = new File(beansDir, typeJavaName + ".java");
                Writer writer = new FileWriter(file);

                writer.append("package ");
                writer.append(packageName);
                writer.append(";");
                writer.append("\n\n");

                TreeSet<String> imports = new TreeSet<String>();
                imports.add("comtech.util.xml.StaxerXmlStreamReader");
                imports.add("comtech.util.xml.StaxerXmlStreamWriter");
                imports.add("comtech.util.props.XmlNameMapProperties");
                imports.add("comtech.util.xml.StaxerXmlStreamException");
                imports.add("comtech.util.xml.XmlName");
                imports.add("comtech.util.xml.element.ListStaxerXmlElement");

                Map<XmlName, String> constantsMap = new HashMap<XmlName, String>();
                Set<String> writeXmlNamespaces = new TreeSet<String>();

                StringBuilder constants = new StringBuilder();
                StringBuilder fields = new StringBuilder();
                StringBuilder gettersSetters = new StringBuilder();
                StringBuilder readHttpParams = new StringBuilder();
                StringBuilder readXmlAttributes = new StringBuilder();
                StringBuilder readXmlContentElement = new StringBuilder();
                StringBuilder readXmlContent = new StringBuilder();
                StringBuilder writeXmlElements = new StringBuilder();
                StringBuilder writeXmlAttributes = new StringBuilder();
                StringBuilder writeXmlValue = new StringBuilder();
                StringBuilder toStringMethod = new StringBuilder();

                List<XmlSchemaTypeField> typeFields = xmlSchemaComplexType.getFields();

                boolean httpRequestReadable = readHttpParametersTypes.contains(typeXmlName);
                boolean superHttpRequestReadable = false;

                String superTypeJavaName = null;
                XmlName superTypeXmlName = xmlSchemaComplexType.getSuperTypeXmlName();
                if (superTypeXmlName != null) {
                    superHttpRequestReadable = readHttpParametersTypes.contains(superTypeXmlName);
                    XmlSchemaType superType = webServiceTypesMap.get(superTypeXmlName);
                    if (superType != null && superType.isComplexType()) {
                        superTypeJavaName = superType.getJavaName();
                    }
                }

                for (XmlSchemaTypeField field : typeFields) {
                    XmlName fieldXmlType = field.getXmlType();
                    XmlSchemaXsdType fieldXsdType = XSD_JAVA_TYPE_MAP.get(fieldXmlType);

                    String fieldTypeJavaName = null;
                    String fieldJaxbXmlSchema = null;
                    String fieldJavaConverter = null;
                    String fieldXmlConverter = null;
                    boolean enumField = false;
                    if (fieldXsdType != null) {
                        fieldJaxbXmlSchema = fieldXsdType.getJaxbXmlSchema();
                        fieldTypeJavaName = fieldXsdType.getJavaName();
                        imports.addAll(fieldXsdType.getImports());
                        fieldJavaConverter = fieldXsdType.getJavaConverter();
                        fieldXmlConverter = fieldXsdType.getXmlConverter();
                    }

                    if (StringUtils.isEmpty(fieldTypeJavaName)) {
                        XmlSchemaType fieldType = webServiceTypesMap.get(fieldXmlType);
                        if (fieldType != null) {
                            if (fieldType.isComplexType()) {
                                fieldTypeJavaName = fieldType.getJavaName();
                                String fieldTypeJavaPackage = fieldType.getJavaPackage();
                                if (fieldTypeJavaPackage != null) {
                                    imports.add(fieldTypeJavaPackage + "." + fieldTypeJavaName);
                                }
                            } else {
                                fieldTypeJavaName = fieldType.getJavaName();
                                enumField = fieldType.isEnumSimpleType();
                            }
                        }
                    }

                    if (!StringUtils.isEmpty(fieldTypeJavaName)) {
                        if (field.isArray()) {
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
                                    writeXmlNamespaces.add(XmlConstants.NAMESPACE_URI_XSI);
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
                        log.info("fieldJavaName = " + fieldJavaName);

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

                        // field xml name constant
                        String constantName = constantsMap.get(fieldXmlName);
                        if (StringUtils.isEmpty(constantName) && !valueField) {
                            constantName = "XML_NAME_" + StringUtils.toEnumName(fieldXmlNameLocalPart);
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
                        // read http parameters
                        if (fieldXsdType != null || enumField) {
                            if (arrayField) {
                                readHttpParams.append("        for (String value: httpRequestHelper.getRequestParameters(\"");
                                if (fieldXmlNameLocalPart != null) {
                                    readHttpParams.append(fieldXmlNameLocalPart);
                                } else {
                                    readHttpParams.append("value");
                                }
                                readHttpParams.append("\")) {\n");
                                readHttpParams.append("            ");
                                readHttpParams.append(fieldJavaName);
                                readHttpParams.append(".add(");
                                if (fieldJavaConverterNotEmpty) {
                                    readHttpParams.append(fieldJavaConverter);
                                    readHttpParams.append("(");
                                } else if (enumField) {
                                    readHttpParams.append(fieldTypeJavaName);
                                    readHttpParams.append(".getByCode(");
                                }
                                readHttpParams.append("value)");
                                if (fieldJavaConverterNotEmpty || enumField) {
                                    readHttpParams.append(")");
                                }
                                readHttpParams.append(";\n");
                                readHttpParams.append("        }\n");
                            } else {
                                readHttpParams.append("        ");
                                readHttpParams.append(fieldJavaName);
                                readHttpParams.append(" = ");
                                if (fieldJavaConverterNotEmpty) {
                                    readHttpParams.append(fieldJavaConverter);
                                    readHttpParams.append("(");
                                } else if (enumField) {
                                    readHttpParams.append(fieldTypeJavaName);
                                    readHttpParams.append(".getByCode(");
                                }
                                readHttpParams.append("httpRequestHelper.getRequestParameter(\"");
                                if (fieldXmlNameLocalPart != null) {
                                    readHttpParams.append(fieldXmlNameLocalPart);
                                } else {
                                    readHttpParams.append("value");
                                }
                                readHttpParams.append("\")");
                                if (fieldJavaConverterNotEmpty || enumField) {
                                    readHttpParams.append(")");
                                }
                                readHttpParams.append(";\n");
                            }
                        }

                        // readXml
                        if (elementField) {
                            if (readXmlContentElement.length() == 0) {
                                readXmlContent.append(
                                        "        XmlName rootElementName = xmlReader.getLastStartedElement();\n" +
                                                "        while (xmlReader.readNext() && !xmlReader.elementEnded(rootElementName)) {\n" +
                                                "            readXmlContentElement(xmlReader);\n" +
                                                "        }\n"
                                );
                            }
                            if (fieldXsdType != null) {
                                readXmlContentElement.append("        if (xmlReader.elementStarted(");
                                readXmlContentElement.append(constantName);
                                readXmlContentElement.append(")) {\n            ");
                                if (arrayField) {
                                    readXmlContentElement.append(fieldTypeJavaName);
                                    readXmlContentElement.append(" ");
                                    readXmlContentElement.append(fieldJavaName);
                                    readXmlContentElement.append("Item = ");
                                } else {
                                    readXmlContentElement.append(fieldJavaName);
                                    readXmlContentElement.append(" = ");
                                }
                                if (fieldJavaConverterNotEmpty) {
                                    readXmlContentElement.append(fieldJavaConverter);
                                    readXmlContentElement.append("(");
                                }
                                readXmlContentElement.append("xmlReader.readCharacters(");
                                readXmlContentElement.append(constantName);
                                if (fieldJavaConverterNotEmpty) {
                                    readXmlContentElement.append(")");
                                }
                                readXmlContentElement.append(");\n");
                                if (arrayField) {
                                    readXmlContentElement.append("            if (");
                                    readXmlContentElement.append(fieldJavaName);
                                    readXmlContentElement.append("Item != null) {\n");
                                    readXmlContentElement.append("                ");
                                    readXmlContentElement.append(fieldJavaName);
                                    readXmlContentElement.append(".add(");
                                    readXmlContentElement.append(fieldJavaName);
                                    readXmlContentElement.append("Item);\n");
                                    readXmlContentElement.append("            }\n");
                                }
                            } else {
                                readXmlContentElement.append("        if (xmlReader.elementStarted(");
                                readXmlContentElement.append(constantName);
                                readXmlContentElement.append(")) {\n            ");
                                if (arrayField) {
                                    readXmlContentElement.append(fieldTypeJavaName);
                                    readXmlContentElement.append(" ");
                                    readXmlContentElement.append(fieldJavaName);
                                    readXmlContentElement.append("Item = ");
                                } else {
                                    readXmlContentElement.append(fieldJavaName);
                                    readXmlContentElement.append(" = ");
                                }
                                if (enumField) {
                                    readXmlContentElement.append(fieldTypeJavaName);
                                    readXmlContentElement.append(".getByCode(");
                                    readXmlContentElement.append("xmlReader.readCharacters()");
                                    readXmlContentElement.append(");\n");
                                } else {
                                    imports.add("comtech.util.xml.XmlUtils");
                                    readXmlContentElement.append("XmlUtils.readXml(xmlReader, ");
                                    readXmlContentElement.append(fieldTypeJavaName);
                                    readXmlContentElement.append(".class, ");
                                    readXmlContentElement.append(constantName);
                                    if (nillableField) {
                                        readXmlContentElement.append(", true");
                                    } else {
                                        readXmlContentElement.append(", false");
                                    }
                                    readXmlContentElement.append(");\n");
                                }
                                if (arrayField) {
                                    readXmlContentElement.append("            if (");
                                    readXmlContentElement.append(fieldJavaName);
                                    readXmlContentElement.append("Item != null) {\n");
                                    readXmlContentElement.append("                ");
                                    readXmlContentElement.append(fieldJavaName);
                                    readXmlContentElement.append(".add(");
                                    readXmlContentElement.append(fieldJavaName);
                                    readXmlContentElement.append("Item");
                                    readXmlContentElement.append(");\n");
                                    readXmlContentElement.append("            }\n");
                                }
                            }
                            readXmlContentElement.append("            return true;\n        }\n");
                        } else if (valueField) {
                            readXmlContent.append("        ");
                            readXmlContent.append(fieldJavaName);
                            readXmlContent.append(" = ");
                            if (fieldJavaConverterNotEmpty) {
                                readXmlContent.append(fieldJavaConverter);
                                readXmlContent.append("(");
                            } else if (enumField) {
                                readXmlContent.append(fieldTypeJavaName);
                                readXmlContent.append(".getByCode(");
                            }
                            readXmlContent.append("xmlReader.readCharacters()");
                            if (fieldJavaConverterNotEmpty || enumField) {
                                readXmlContent.append(")");
                            }
                            readXmlContent.append(";\n");
                        } else {
                            readXmlAttributes.append("        ");
                            readXmlAttributes.append(fieldJavaName);
                            readXmlAttributes.append(" = ");
                            if (fieldJavaConverterNotEmpty) {
                                readXmlAttributes.append(fieldJavaConverter);
                                readXmlAttributes.append("(");
                            } else if (enumField) {
                                readXmlAttributes.append(fieldTypeJavaName);
                                readXmlAttributes.append(".getByCode(");
                            }
                            readXmlAttributes.append("attributes.get(");
                            readXmlAttributes.append(constantName);
                            readXmlAttributes.append(")");
                            if (fieldJavaConverterNotEmpty || enumField) {
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
                                    writeXmlElements.append("                xmlWriter.element(");
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
                                    if (nillableField) {
                                        writeXmlNamespaces.add(XmlConstants.NAMESPACE_URI_XSI);
                                        writeXmlElements.append(", true");
                                    } else {
                                        writeXmlElements.append(", false");
                                    }
                                    writeXmlElements.append(");\n");
                                } else if (enumField) {
                                    writeXmlElements.append("                String ");
                                    writeXmlElements.append(fieldJavaName);
                                    writeXmlElements.append("ItemCode = null;\n");
                                    writeXmlElements.append("                if (");
                                    writeXmlElements.append(fieldJavaName);
                                    writeXmlElements.append("Item != null) {\n");
                                    writeXmlElements.append("                    ");
                                    writeXmlElements.append(fieldJavaName);
                                    writeXmlElements.append("ItemCode = ");
                                    writeXmlElements.append(fieldJavaName);
                                    writeXmlElements.append("Item.getCode();\n");
                                    writeXmlElements.append("                }\n");
                                    writeXmlElements.append("                xmlWriter.element(");
                                    writeXmlElements.append(constantName);
                                    writeXmlElements.append(", ");
                                    writeXmlElements.append(fieldJavaName);
                                    writeXmlElements.append("ItemCode");
                                    if (nillableField) {
                                        writeXmlNamespaces.add(XmlConstants.NAMESPACE_URI_XSI);
                                        writeXmlElements.append(", true");
                                    } else {
                                        writeXmlElements.append(", false");
                                    }
                                    writeXmlElements.append(");\n");
                                } else {
                                    imports.add("comtech.util.xml.XmlUtils");
                                    writeXmlElements.append("                XmlUtils.writeXmlElement(xmlWriter, ");
                                    writeXmlElements.append(constantName);
                                    writeXmlElements.append(", ");
                                    writeXmlElements.append(fieldJavaName);
                                    writeXmlElements.append("Item");
                                    if (nillableField) {
                                        writeXmlNamespaces.add(XmlConstants.NAMESPACE_URI_XSI);
                                        writeXmlElements.append(", true");
                                    } else {
                                        writeXmlElements.append(", false");
                                    }
                                    writeXmlElements.append(");\n");
                                }
                                writeXmlElements.append("            }\n");
                                writeXmlElements.append("        }\n");
                            } else {
                                if (fieldXsdType != null) {
                                    writeXmlElements.append("        xmlWriter.element(");
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
                                    if (nillableField) {
                                        writeXmlNamespaces.add(XmlConstants.NAMESPACE_URI_XSI);
                                        writeXmlElements.append(", true");
                                    } else {
                                        writeXmlElements.append(", false");
                                    }
                                    writeXmlElements.append(");\n");
                                } else if (enumField) {
                                    writeXmlElements.append("        String ");
                                    writeXmlElements.append(fieldJavaName);
                                    writeXmlElements.append("Code = null;\n");
                                    writeXmlElements.append("        if (");
                                    writeXmlElements.append(fieldJavaName);
                                    writeXmlElements.append(" != null) {\n");
                                    writeXmlElements.append("            ");
                                    writeXmlElements.append(fieldJavaName);
                                    writeXmlElements.append("Code = ");
                                    writeXmlElements.append(fieldJavaName);
                                    writeXmlElements.append(".getCode();\n");
                                    writeXmlElements.append("        }\n");
                                    writeXmlElements.append("        xmlWriter.element(");
                                    writeXmlElements.append(constantName);
                                    writeXmlElements.append(", ");
                                    writeXmlElements.append(fieldJavaName);
                                    writeXmlElements.append("Code");
                                    if (nillableField) {
                                        writeXmlNamespaces.add(XmlConstants.NAMESPACE_URI_XSI);
                                        writeXmlElements.append(", true");
                                    } else {
                                        writeXmlElements.append(", false");
                                    }
                                    writeXmlElements.append(");\n");
                                } else {
                                    imports.add("comtech.util.xml.XmlUtils");
                                    writeXmlElements.append("        XmlUtils.writeXmlElement(xmlWriter, ");
                                    writeXmlElements.append(constantName);
                                    writeXmlElements.append(", ");
                                    writeXmlElements.append(fieldJavaName);
                                    if (nillableField) {
                                        writeXmlNamespaces.add(XmlConstants.NAMESPACE_URI_XSI);
                                        writeXmlElements.append(", true");
                                    } else {
                                        writeXmlElements.append(", false");
                                    }
                                    writeXmlElements.append(");\n");
                                }
                            }
                        } else if (valueField) {
                            if (enumField) {
                                writeXmlValue.append("        if (");
                                writeXmlValue.append(fieldJavaName);
                                writeXmlValue.append(" != null) {\n");
                                writeXmlValue.append("            xmlWriter.text(");
                                writeXmlValue.append(fieldJavaName);
                                writeXmlValue.append(".getCode());\n");
                                writeXmlValue.append("        }\n");
                            } else {
                                writeXmlValue.append("        xmlWriter.text(");
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
                            if (enumField) {
                                writeXmlAttributes.append("        if (");
                                writeXmlAttributes.append(fieldJavaName);
                                writeXmlAttributes.append(" != null) {\n");
                                writeXmlAttributes.append("            xmlWriter.attribute(");
                                writeXmlAttributes.append(constantName);
                                writeXmlAttributes.append(", ");
                                writeXmlAttributes.append(fieldJavaName);
                                writeXmlAttributes.append(".getCode());\n");
                                writeXmlAttributes.append("        }\n");
                            } else {
                                writeXmlAttributes.append("        xmlWriter.attribute(");
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

                if (superTypeJavaName == null) {
                    imports.add("comtech.util.xml.StaxerReadXml");
                    imports.add("comtech.util.xml.StaxerWriteXml");
                }

                if (httpRequestReadable) {
                    imports.add("comtech.util.http.helper.HttpRequestHelper");
                    imports.add("comtech.util.http.helper.ReadHttpParameters");
                }

                for (String className : imports) {
                    writer.append("import ");
                    writer.append(className);
                    writer.append(";\n");
                }

                writer.append("\n");

                writer.append(classJaxbAnnotations.toString());

                writer.append("public class ");
                writer.append(typeJavaName);
                if (superTypeJavaName != null) {
                    writer.append(" extends ");
                    writer.append(superTypeJavaName);
                    if (httpRequestReadable) {
                        writer.append(" implements ReadHttpParameters");
                    }
                } else {
                    writer.append(" implements StaxerReadXml, StaxerWriteXml");
                    if (httpRequestReadable) {
                        writer.append(", ReadHttpParameters");
                    }
                }
                writer.append(" {\n\n");
                if (constants.length() > 0) {
                    writer.append(constants.toString());
                    writer.append("\n");
                }
                writer.append(fields.toString());
                writer.append(gettersSetters.toString());

                if (httpRequestReadable) {
                    writer.append("    public void readHttpParameters(\n" +
                            "            HttpRequestHelper httpRequestHelper\n" +
                            "    ) {\n");
                    if (superHttpRequestReadable) {
                        writer.append("        super.readHttpParameters(httpRequestHelper);\n");
                    }
                    writer.append(readHttpParams.toString());
                    writer.append("    }\n\n");
                }

                if (superTypeJavaName != null) {
                    writer.append("    @Override\n");
                }
                writer.append(
                        "    public void readXmlAttributes(\n" +
                                "            XmlNameMapProperties attributes\n" +
                                "    ) throws StaxerXmlStreamException {\n"
                );
                if (superTypeJavaName != null) {
                    writer.append("        super.readXmlAttributes(attributes);\n");
                }
                writer.append(readXmlAttributes.toString());
                writer.append(
                        "    }\n\n"
                );

                if (superTypeJavaName == null) {
                    writer.append(
                            "    public void readXmlContent(\n" +
                                    "            StaxerXmlStreamReader xmlReader\n" +
                                    "    ) throws StaxerXmlStreamException {\n"
                    );
                    if (readXmlContent.length() > 0) {
                        writer.append(readXmlContent.toString());
                    } else {
                        writer.append(
                                "        XmlName rootElementName = xmlReader.getLastStartedElement();\n" +
                                        "        while (xmlReader.readNext() && !xmlReader.elementEnded(rootElementName)) {\n" +
                                        "            readXmlContentElement(xmlReader);\n" +
                                        "        }\n"
                        );
                    }
                    writer.append(
                            "    }\n\n"
                    );
                } else {
                    writer.append("    @Override\n");
                }
                writer.append("    public boolean readXmlContentElement(\n" +
                        "            StaxerXmlStreamReader xmlReader\n" +
                        "    ) throws StaxerXmlStreamException {\n");
                if (readXmlContentElement.length() != 0) {
                    writer.append(readXmlContentElement.toString());
                }
                if (superTypeJavaName != null) {
                    writer.append("        return super.readXmlContentElement(xmlReader);\n");
                } else {
                    writer.append("        return false;\n");
                }
                writer.append(
                        "    }\n\n"
                );

                if (superTypeJavaName != null) {
                    writer.append("    @Override\n");
                }
                writer.append(
                        "    public void writeXmlAttributes(\n" +
                                "            StaxerXmlStreamWriter xmlWriter\n" +
                                "    ) throws StaxerXmlStreamException {\n");
                for (String namespace : writeXmlNamespaces) {
                    writer.append("        xmlWriter.declareNamespace(\"");
                    writer.append(namespace);
                    writer.append("\");\n");
                }
                if (superTypeJavaName != null) {
                    writer.append("        super.writeXmlAttributes(xmlWriter);\n");
                }
                writer.append(writeXmlAttributes.toString());
                writer.append(
                        "    }\n\n"
                );

                if (superTypeJavaName != null) {
                    writer.append("    @Override\n");
                }
                writer.append(
                        "    public void writeXmlContent(\n" +
                                "            StaxerXmlStreamWriter xmlWriter\n" +
                                "    ) throws StaxerXmlStreamException {\n");
                if (superTypeJavaName != null) {
                    writer.append("        super.writeXmlContent(xmlWriter);\n");
                }
                writer.append(writeXmlElements.toString());
                writer.append(writeXmlValue.toString());
                writer.append(
                        "    }\n\n"
                );

                writer.append("    @Override\n");
                writer.append("    public String toString() {\n");
                writer.append("        final StringBuilder sb = new StringBuilder();\n");
                writer.append("        sb.append(\"<");
                writer.append(typeJavaName);
                writer.append(">\\n\");\n");
                writer.append("        toString(sb);\n");
                writer.append("        sb.append(\"</");
                writer.append(typeJavaName);
                writer.append(">\\n\");\n");
                writer.append("        return sb.toString();\n");
                writer.append("    }\n");
                writer.append("\n");
                writer.append("    public void toString(StringBuilder sb) {\n");
                if (superTypeJavaName != null) {
                    writer.append("        super.toString(sb);\n");
                }
                writer.append(toStringMethod.toString());
                writer.append("}\n");

                writer.flush();
                writer.close();
            }
        }
        return readHttpParametersTypes;
    }

    public static WebService readWebService(
            URI uri, String httpUser, String httpPassword, String xmlCharset
    ) throws Exception {
        log.info("Reading web service from: " + uri + ", xml charset: " + xmlCharset);
        String xml = ResourceUtils.getUrlContentAsString(uri, httpUser, httpPassword, xmlCharset);
        if (xml != null) {
            WebService webService = new WebService(uri, httpUser, httpPassword, xmlCharset);
            StaxerXmlStreamReader xmlReader = new StaxerXmlStreamReader(new StringReader(xml));
            if (xmlReader.readStartElement(XmlConstants.XML_NAME_WSDL_DEFINITIONS)) {
                webService.readXmlAttributes(xmlReader.getAttributes());
                webService.readXmlContent(xmlReader);
            }
            return webService;
        }
        return null;
    }

    public static XmlSchema readXmlSchema(
            String xsdTargetNamespace, URI uri, String httpUser,
            String httpPassword, String xmlCharset
    ) throws Exception {
        return readXmlSchema(xsdTargetNamespace, uri, httpUser, httpPassword, xmlCharset, null);
    }

    public static XmlSchema readXmlSchema(
            String xsdTargetNamespace, URI uri, String httpUser,
            String httpPassword, String xmlCharset, Map<String, String> namespacesMap
    ) throws Exception {
        log.info("Reading xml schema from: " + uri + ", xml charset: " + xmlCharset);
        String xml = ResourceUtils.getUrlContentAsString(uri, httpUser, httpPassword, xmlCharset);
        if (xml != null) {
            XmlSchema xmlSchema = new XmlSchema(xsdTargetNamespace, uri, httpUser, httpPassword, xmlCharset);
            if (namespacesMap != null && !namespacesMap.isEmpty()) {
                xmlSchema.getNamespacesMap().putAll(namespacesMap);
            }
            StaxerXmlStreamReader xmlReader = new StaxerXmlStreamReader(new StringReader(xml));
            if (xmlReader.readStartElement(XmlConstants.XML_NAME_XSD_SCHEMA)) {
                xmlSchema.readXmlAttributes(xmlReader.getAttributes());
                xmlSchema.readXmlContent(xmlReader);
            }
            return xmlSchema;
        }
        return null;
    }

}
