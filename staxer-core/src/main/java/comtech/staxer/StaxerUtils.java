package comtech.staxer;

import comtech.staxer.domain.*;
import comtech.util.StringUtils;
import comtech.util.file.FileUtils;
import comtech.util.xml.XmlConstants;
import comtech.util.xml.XmlName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static comtech.util.StringUtils.capitalize2;
import static comtech.util.xml.XmlConstants.NAMESPACE_URI_XSD;


/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-19 16:57 (Europe/Moscow)
 */
public class StaxerUtils {

    private static Logger log = LoggerFactory.getLogger(StaxerUtils.class);

    public static Map<XmlName, WebServiceXsdType> XSD_JAVA_TYPE_MAP;

    static {
        XSD_JAVA_TYPE_MAP = new HashMap<XmlName, WebServiceXsdType>();
        WebServiceXsdType javaXsdType = new WebServiceXsdType();
        javaXsdType.setJavaName("String");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "string"), javaXsdType);
        javaXsdType = new WebServiceXsdType();
        javaXsdType.setJavaName("BigDecimal");
        javaXsdType.getImports().add("java.math.BigDecimal");
        javaXsdType.getImports().add("comtech.util.NumberUtils");
        javaXsdType.setJavaConverter("NumberUtils.parseBigDecimal");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "decimal"), javaXsdType);
        javaXsdType = new WebServiceXsdType();
        javaXsdType.setJavaName("Integer");
        javaXsdType.getImports().add("comtech.util.NumberUtils");
        javaXsdType.setJavaConverter("NumberUtils.parseInteger");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "unsignedShort"), javaXsdType);
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "int"), javaXsdType);
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "integer"), javaXsdType);
        javaXsdType = new WebServiceXsdType();
        javaXsdType.setJavaName("Float");
        javaXsdType.getImports().add("comtech.util.NumberUtils");
        javaXsdType.setJavaConverter("NumberUtils.parseFloat");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "float"), javaXsdType);
        javaXsdType = new WebServiceXsdType();
        javaXsdType.setJavaName("Double");
        javaXsdType.getImports().add("comtech.util.NumberUtils");
        javaXsdType.setJavaConverter("NumberUtils.parseDouble");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "double"), javaXsdType);
        javaXsdType = new WebServiceXsdType();
        javaXsdType.setJavaName("Boolean");
        javaXsdType.setJavaConverter("Boolean.parseBoolean");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "boolean"), javaXsdType);
        javaXsdType = new WebServiceXsdType();
        javaXsdType.setJavaName("byte[]");
        javaXsdType.getImports().add("org.apache.commons.codec.binary.Base64");
        javaXsdType.setJavaConverter("Base64.decodeBase64");
        javaXsdType.setXmlConverter("Base64.encodeBase64String");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "base64Binary"), javaXsdType);
        javaXsdType = new WebServiceXsdType();
        javaXsdType.setJavaName("Date");
        javaXsdType.getImports().add("java.util.Date");
        javaXsdType.getImports().add("comtech.util.DateTimeUtils");
        javaXsdType.setJaxbXmlSchema("dateTime");
        javaXsdType.setJavaConverter("DateTimeUtils.parseXmlDate");
        javaXsdType.setXmlConverter("DateTimeUtils.formatXmlDate");
        XSD_JAVA_TYPE_MAP.put(new XmlName(NAMESPACE_URI_XSD, "dateTime"), javaXsdType);
    }

    public static void createJavaWebService(
            WebService webService, File sourceDir,
            String packageName, boolean writeJaxbAnnotations,
            boolean createClientService, boolean createServerService
    ) throws IOException {
        File destDir = new File(sourceDir, packageName.replaceAll("\\.", "/"));
        File beansDir = new File(destDir, "bean");
        FileUtils.createOrCleanupDir(beansDir, log);

        Map<XmlName, XmlName> globalTypeElementMap = webService.getGlobalTypeElementMap();
        Map<XmlName, XmlName> globalElementTypeMap = webService.getGlobalElementTypeMap();
        Map<XmlName, WebServiceMessage> webServiceMessagesMap = webService.getMessagesMap();
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

            writer.append("package ");
            writer.append(packageName);
            writer.append(".bean;");
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
            for (WebServiceEnumValue enumValue : enumType.getValues()) {
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
        }

        for (WebServiceType type : webServiceTypesMap.values()) {
            String typeJavaName = type.getJavaName();
            XmlName typeXmlName = type.getXmlName();

            File file = new File(beansDir, typeJavaName + ".java");
            Writer writer = new FileWriter(file);

            writer.append("package ");
            writer.append(packageName);
            writer.append(".bean;");
            writer.append("\n\n");

            TreeSet<String> imports = new TreeSet<String>();
            imports.add("comtech.util.xml.StaxerXmlReader");
            imports.add("comtech.util.xml.StaxerXmlWriter");
            imports.add("comtech.util.xml.StaxerXmlStreamReader");
            imports.add("comtech.util.xml.StaxerXmlStreamWriter");
            imports.add("comtech.util.props.StringMapProperties");
            imports.add("comtech.util.xml.StaxerXmlStreamException");

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
                WebServiceXsdType fieldXsdType = XSD_JAVA_TYPE_MAP.get(fieldXmlType);

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
                    WebServiceType fieldType = webServiceTypesMap.get(fieldXmlType);
                    if (fieldType != null) {
                        fieldTypeJavaName = fieldType.getJavaName();
                    } else {
                        WebServiceEnum enumFieldType = webServiceEnumsMap.get(fieldXmlType);
                        if (enumFieldType != null) {
                            fieldTypeJavaName = enumFieldType.getJavaName();
                            enumField = true;
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
                        imports.add("comtech.util.xml.XmlName");
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
                                    "        XmlName rootElementName = xmlReader.getLastStartedElement();\n" +
                                            "        while (xmlReader.readNext()) {\n" +
                                            "            if (xmlReader.elementEnded(rootElementName)) {\n" +
                                            "                break;\n"
                            );
                        }
                        if (fieldXsdType != null) {
                            readXmlElements.append("            } else if (xmlReader.elementStarted(");
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
                            readXmlElements.append("xmlReader.readCharacters(");
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
                            readXmlElements.append("            } else if (xmlReader.elementStarted(");
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
                            if (enumField) {
                                readXmlElements.append(fieldTypeJavaName);
                                readXmlElements.append(".getByCode(");
                                readXmlElements.append("xmlReader.readCharacters()");
                                readXmlElements.append(");\n");
                            } else {
                                imports.add("comtech.util.xml.XmlUtils");
                                readXmlElements.append("XmlUtils.readXml(xmlReader, ");
                                readXmlElements.append(fieldTypeJavaName);
                                readXmlElements.append(".class, ");
                                readXmlElements.append(constantName);
                                if (nillableField) {
                                    readXmlElements.append(", true");
                                } else {
                                    readXmlElements.append(", false");
                                }
                                readXmlElements.append(");\n");
                            }
                            if (arrayField) {
                                readXmlElements.append("                if (");
                                readXmlElements.append(fieldJavaName);
                                readXmlElements.append("Item != null) {\n");
                                readXmlElements.append("                    ");
                                readXmlElements.append(fieldJavaName);
                                readXmlElements.append(".add(");
                                readXmlElements.append(fieldJavaName);
                                readXmlElements.append("Item");
                                readXmlElements.append(");\n");
                                readXmlElements.append("                }\n");
                            }
                        }
                    } else if (valueField) {
                        readXmlValue.append("        value = ");
                        if (fieldJavaConverterNotEmpty) {
                            readXmlValue.append(fieldJavaConverter);
                            readXmlValue.append("(");
                        } else if (enumField) {
                            readXmlValue.append(fieldTypeJavaName);
                            readXmlValue.append(".getByCode(");
                        }
                        readXmlValue.append("xmlReader.readCharacters()");
                        if (fieldJavaConverterNotEmpty || enumField) {
                            readXmlValue.append(")");
                        }
                        readXmlValue.append(";\n");
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
                        readXmlAttributes.append(".toString())");
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
                            writeXmlValue.append("        if(");
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
                            writeXmlAttributes.append("        if(");
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
            }
            writer.append(" implements StaxerXmlReader, StaxerXmlWriter {\n\n");
            if (constants.length() > 0) {
                writer.append(constants.toString());
                writer.append("\n");
            }
            writer.append(fields.toString());
            writer.append(gettersSetters.toString());

            writer.append(
                    "    public void readXmlAttributes(\n" +
                            "            StringMapProperties attributes\n" +
                            "    ) throws StaxerXmlStreamException {\n"
            );
            if (superTypeJavaName != null) {
                writer.append("        super.readXmlAttributes(attributes);\n");
            }
            writer.append(readXmlAttributes.toString());
            writer.append(
                    "    }\n\n"
            );

            writer.append(
                    "    public void readXmlContent(\n" +
                            "            StaxerXmlStreamReader xmlReader\n" +
                            "    ) throws StaxerXmlStreamException {\n"
            );
            if (superTypeJavaName != null) {
                writer.append("        super.readXmlContent(xmlReader);\n");
            }
            if (readXmlElements.length() != 0) {
                writer.append(readXmlElements.toString());
                writer.append(
                        "            }\n" +
                                "        }\n"
                );
            }
            writer.append(readXmlValue.toString());
            writer.append(
                    "    }\n\n"
            );

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

            writer.append(toStringMethod.toString());
            writer.append("}\n");

            writer.flush();
            writer.close();
        }

        if (createClientService || createServerService) {
            String webServiceName = capitalize2(webService.getBindingName().getLocalPart(), false);
            if (!webServiceName.toUpperCase().endsWith("WS")) {
                webServiceName += "Ws";
            }
            String beanPackageName = packageName + ".bean";
            StringBuilder serviceXmlNames = new StringBuilder();
            StringBuilder clientServiceMethods = new StringBuilder();
            StringBuilder serverServiceStaticConstrustor = new StringBuilder();
            StringBuilder serverServiceMethods = new StringBuilder();

            for (WebServiceOperation method : webServiceOperationsMap.values()) {
                XmlName outputMessageName = method.getOutputMessage();
                WebServiceMessage outputMessage = webServiceMessagesMap.get(outputMessageName);
                List<WebServiceMessagePart> outputParts = outputMessage.getParts();
                if (outputParts.isEmpty()) {
                    continue;
                }
                WebServiceMessagePart firstOutputPart = outputParts.get(0);
                XmlName outputElement = firstOutputPart.getElement();
                XmlName outputTypeName = globalElementTypeMap.get(outputElement);
                WebServiceType outputType = webServiceTypesMap.get(outputTypeName);
                if (outputType == null) {
                    continue;
                }
                XmlName inputMessageName = method.getInputMessage();
                WebServiceMessage inputMessage = webServiceMessagesMap.get(inputMessageName);
                List<WebServiceMessagePart> inputParts = inputMessage.getParts();
                if (inputParts.isEmpty()) {
                    continue;
                }
                WebServiceMessagePart firstInputPart = inputParts.get(0);
                XmlName inputElement = firstInputPart.getElement();
                XmlName inputTypeName = globalElementTypeMap.get(inputElement);
                WebServiceType inputType = webServiceTypesMap.get(inputTypeName);
                if (inputType == null) {
                    continue;
                }
                String outputTypeJavaName = outputType.getJavaName();
                String inputTypeJavaName = inputType.getJavaName();
                String methodJavaName = method.getJavaName();

                String inputElementLocalPart = inputElement.getLocalPart();
                String inputTypeConstantName = "XML_NAME_" + StringUtils.toEnumName(inputElementLocalPart);
                serviceXmlNames.append("    public static final XmlName ");
                serviceXmlNames.append(inputTypeConstantName);
                serviceXmlNames.append(" = new XmlName(\"");
                serviceXmlNames.append(inputElement.getNamespaceURI());
                serviceXmlNames.append("\", \"");
                serviceXmlNames.append(inputElementLocalPart);
                serviceXmlNames.append("\");\n");

                if (createClientService) {
                    clientServiceMethods.append("    public ");
                    clientServiceMethods.append(outputTypeJavaName);
                    clientServiceMethods.append(" ");
                    clientServiceMethods.append(methodJavaName);
                    clientServiceMethods.append("(\n");
                    clientServiceMethods.append("            WsRequest wsRequest, ");
                    clientServiceMethods.append(inputTypeJavaName);
                    clientServiceMethods.append(" parameters\n");
                    clientServiceMethods.append("    ) throws WsClientException {\n");
                    clientServiceMethods.append("        return httpWsClient.processSoapQuery(\n");
                    clientServiceMethods.append("                wsRequest, parameters, ");
                    clientServiceMethods.append(inputTypeConstantName);
                    clientServiceMethods.append(", ");
                    clientServiceMethods.append(outputTypeJavaName);
                    clientServiceMethods.append(".class\n");
                    clientServiceMethods.append("        );\n");
                    clientServiceMethods.append("    }\n\n");
                }
                if (createServerService) {
                    serverServiceStaticConstrustor.append("\n        CLASSES.put(");
                    serverServiceStaticConstrustor.append(inputTypeConstantName);
                    serverServiceStaticConstrustor.append(", ");
                    serverServiceStaticConstrustor.append(inputTypeJavaName);
                    serverServiceStaticConstrustor.append(".class);\n");
                    serverServiceStaticConstrustor.append("        METHOD_NAMES.put(");
                    serverServiceStaticConstrustor.append(inputTypeConstantName);
                    serverServiceStaticConstrustor.append(", \"");
                    serverServiceStaticConstrustor.append(methodJavaName);
                    serverServiceStaticConstrustor.append("\");\n");

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
                writer.append("import comtech.staxer.client.HttpWsClient;\n");
                writer.append("import comtech.staxer.client.WsClientException;\n");
                writer.append("import comtech.staxer.client.WsRequest;\n");
                writer.append("import comtech.util.xml.XmlName;\n");
                writer.append("import ");
                writer.append(packageName);
                writer.append(".bean.*;\n\n");
                writer.append("\n");
                writer.append("public class ");
                writer.append(serviceName);
                writer.append(" {\n\n");
                writer.append(serviceXmlNames.toString());
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
                writer.append("import comtech.staxer.server.ServerServiceWs;\n");
                writer.append("import comtech.staxer.server.WsMessage;\n");
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
                writer.append(serviceXmlNames.toString());
                writer.append("\n");
                writer.append("    public static final Map<XmlName, Class> CLASSES;\n");
                writer.append("    public static final Map<XmlName, String> METHOD_NAMES;\n\n");
                writer.append("    static {\n");
                writer.append("        CLASSES = new HashMap<XmlName, Class>();\n");
                writer.append("        METHOD_NAMES = new HashMap<XmlName, String>();\n");
                writer.append(serverServiceStaticConstrustor.toString());
                writer.append("    }\n\n");
                writer.append("    public Class getClass(XmlName xmlName) {\n");
                writer.append("        return CLASSES.get(xmlName);\n");
                writer.append("    }\n\n");
                writer.append("    public String getMethodName(XmlName xmlName) {\n");
                writer.append("        return METHOD_NAMES.get(xmlName);\n");
                writer.append("    }\n\n");
                writer.append(serverServiceMethods.toString());
                writer.append("}\n");
                writer.close();
            }
        }
    }

}
