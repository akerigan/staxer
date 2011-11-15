package comtech.staxer.domain;

import comtech.staxer.StaxerUtils;
import comtech.util.ResourceUtils;
import comtech.util.StringUtils;
import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.*;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import static comtech.util.StringUtils.*;
import static comtech.util.xml.XmlConstants.*;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-15 17:10 (Europe/Moscow)
 */
public class XmlSchema implements StaxerReadXml, StaxerWriteXml {

    private static final XmlName XML_NAME_TARGET_NAMESPACE = new XmlName("targetNamespace");
    private static final XmlName XML_NAME_ELEMENT_FORM_DEFAULT = new XmlName("elementFormDefault");
    private static final XmlName XML_NAME_ATTRIBUTE_FORM_DEFAULT = new XmlName("attributeFormDefault");
    private static final XmlName XML_NAME_TYPE = new XmlName("type");
    private static final XmlName XML_NAME_NAME = new XmlName("name");
    private static final XmlName XML_NAME_BASE = new XmlName("base");
    private static final XmlName XML_NAME_VALUE = new XmlName("value");
    private static final XmlName XML_NAME_NILLABLE = new XmlName("nillable");
    private static final XmlName XML_NAME_MIN_OCCURS = new XmlName("minOccurs");
    private static final XmlName XML_NAME_MAX_OCCURS = new XmlName("maxOccurs");
    private static final XmlName XML_NAME_USE = new XmlName("use");
    private static final XmlName XML_NAME_NAMESPACE = new XmlName("namespace");
    private static final XmlName XML_NAME_SCHEMA_LOCATION = new XmlName("schemaLocation");

    private Map<String, String> namespacesMap = new LinkedHashMap<String, String>();

    private String xsdTargetNamespace;
    private boolean xsdElementsQualified;
    private boolean xsdAttributesQualified;

    private Map<XmlName, XmlSchemaType> typesMap = new LinkedHashMap<XmlName, XmlSchemaType>();
    private Map<XmlName, XmlSchemaEnum> enumsMap = new LinkedHashMap<XmlName, XmlSchemaEnum>();
    private Map<XmlName, XmlName> globalTypeElementMap = new LinkedHashMap<XmlName, XmlName>();
    private Map<XmlName, XmlName> globalElementTypeMap = new LinkedHashMap<XmlName, XmlName>();

    private URI baseUri;
    private String httpUser;
    private String httpPassword;
    private String xmlCharset;

    public XmlSchema(String xsdTargetNamespace) {
        this.xsdTargetNamespace = xsdTargetNamespace;
    }

    public XmlSchema(String xsdTargetNamespace, URI uri, String httpUser, String httpPassword, String xmlCharset) {
        this.xsdTargetNamespace = xsdTargetNamespace;
        baseUri = uri;
        this.httpUser = httpUser;
        this.httpPassword = httpPassword;
        this.xmlCharset = xmlCharset;
    }

    public Map<String, String> getNamespacesMap() {
        return namespacesMap;
    }

    public String getXsdTargetNamespace() {
        return xsdTargetNamespace;
    }

    public boolean isXsdElementsQualified() {
        return xsdElementsQualified;
    }

    public boolean isXsdAttributesQualified() {
        return xsdAttributesQualified;
    }

    public Map<XmlName, XmlSchemaType> getTypesMap() {
        return typesMap;
    }

    public Map<XmlName, XmlSchemaEnum> getEnumsMap() {
        return enumsMap;
    }

    public Map<XmlName, XmlName> getGlobalTypeElementMap() {
        return globalTypeElementMap;
    }

    public Map<XmlName, XmlName> getGlobalElementTypeMap() {
        return globalElementTypeMap;
    }

    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
        String namespace = attributes.get(XML_NAME_TARGET_NAMESPACE);
        if (namespace != null) {
            xsdTargetNamespace = namespace;
        }
        xsdElementsQualified = "qualified".equalsIgnoreCase(
                attributes.get(XML_NAME_ELEMENT_FORM_DEFAULT)
        );
        xsdAttributesQualified = "qualified".equalsIgnoreCase(
                attributes.get(XML_NAME_ATTRIBUTE_FORM_DEFAULT)
        );
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        xmlReader.updateNamespacesMap(namespacesMap);
        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_SCHEMA)) {
            if (xmlReader.elementStarted(XML_NAME_XSD_COMPLEX_TYPE)) {
                readXsdComplexType(xmlReader);
            } else if (xmlReader.elementStarted(XML_NAME_XSD_SIMPLE_TYPE)) {
                readXsdSimpleType(xmlReader);
            } else if (xmlReader.elementStarted(XML_NAME_XSD_ELEMENT)) {
                XmlNameMapProperties attributes = xmlReader.getAttributes();
                XmlName typeName = unpackXmlName(attributes.get(XML_NAME_TYPE), namespacesMap);
                XmlName elementName = new XmlName(xsdTargetNamespace, attributes.get(XML_NAME_NAME));
                globalTypeElementMap.put(typeName, elementName);
                globalElementTypeMap.put(elementName, typeName);
            } else if (xmlReader.elementStarted(XML_NAME_XSD_IMPORT) || xmlReader.elementStarted(XML_NAME_XSD_INCLUDE)) {
                XmlNameMapProperties attributes = xmlReader.getAttributes();
                String namespace;
                if (xmlReader.elementStarted(XML_NAME_XSD_IMPORT)) {
                    namespace = attributes.get(XML_NAME_NAMESPACE);
                } else {
                    namespace = xsdTargetNamespace;
                }
                URI location = URI.create(attributes.get(XML_NAME_SCHEMA_LOCATION));
                if (baseUri != null) {
                    location = baseUri.resolve(location);
                }
                try {
                    String xml = ResourceUtils.getUrlContentAsString(location, httpUser, httpPassword, xmlCharset);
                    if (xml != null) {
                        XmlSchema xmlSchema = StaxerUtils.readXsdSchema(
                                namespace, location, httpUser, httpPassword, xmlCharset
                        );
                        if (xmlSchema != null) {
                            typesMap.putAll(xmlSchema.getTypesMap());
                            enumsMap.putAll(xmlSchema.getEnumsMap());
                            globalElementTypeMap.putAll(xmlSchema.getGlobalElementTypeMap());
                            globalTypeElementMap.putAll(xmlSchema.getGlobalTypeElementMap());
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

    private void readXsdComplexType(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        XmlSchemaType type = new XmlSchemaType();
        XmlNameMapProperties attributes = xmlReader.getAttributes();
        String localName = attributes.get(XML_NAME_NAME);
        XmlName xmlName = new XmlName(xsdTargetNamespace, localName);
        type.setXmlName(xmlName);
        type.setJavaName(capitalize3(localName));

        typesMap.put(type.getXmlName(), type);

        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_COMPLEX_TYPE)) {
            if (xmlReader.elementStarted(XML_NAME_XSD_SIMPLE_CONTENT)) {
                while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_SIMPLE_CONTENT)) {
                    if (xmlReader.elementStarted(XML_NAME_XSD_EXTENSION)) {
                        attributes = xmlReader.getAttributes();
                        XmlSchemaTypeField field = new XmlSchemaTypeField();
                        field.setJavaName("value");
                        field.setValueField(true);
                        field.setXmlType(unpackXmlName(attributes.get(XML_NAME_BASE), namespacesMap));
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
                while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_COMPLEX_CONTENT)) {
                    if (xmlReader.elementStarted(XML_NAME_XSD_EXTENSION)) {
                        attributes = xmlReader.getAttributes();
                        type.setSuperTypeXmlName(unpackXmlName(attributes.get(XML_NAME_BASE), namespacesMap));
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
        XmlSchemaEnum enumType = new XmlSchemaEnum();
        XmlNameMapProperties attributes = xmlReader.getAttributes();
        String localName = attributes.get(XML_NAME_NAME);
        XmlName xmlName = new XmlName(xsdTargetNamespace, localName);
        enumType.setXmlName(xmlName);
        enumType.setJavaName(capitalize3(localName));
        enumsMap.put(enumType.getXmlName(), enumType);
        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_SIMPLE_TYPE)) {
            if (xmlReader.elementStarted(XML_NAME_XSD_RESTRICTION)) {
                attributes = xmlReader.getAttributes();
                enumType.setXmlType(unpackXmlName(attributes.get(XML_NAME_BASE), namespacesMap));
                while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_RESTRICTION)) {
                    if (xmlReader.elementStarted(XML_NAME_XSD_ENUMERATION)) {
                        attributes = xmlReader.getAttributes();
                        String value = attributes.get(XML_NAME_VALUE);
                        XmlSchemaEnumValue enumValue = new XmlSchemaEnumValue();
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

    private void readXsdSequence(
            StaxerXmlStreamReader xmlReader, XmlSchemaType type,
            boolean xsdElementsQualified, boolean xsdAttributesQualified,
            String xsdTargetNamespace, Map<String, String> namespacesMap
    ) throws StaxerXmlStreamException {
        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_SEQUENCE)) {
            if (xmlReader.elementStarted(XML_NAME_XSD_ELEMENT)) {
                type.getFields().add(createField(
                        xmlReader.getAttributes(), true, xsdElementsQualified,
                        xsdAttributesQualified, xsdTargetNamespace, namespacesMap
                ));
            }
        }
    }

    private XmlSchemaTypeField createField(
            XmlNameMapProperties attributes, boolean elementField,
            boolean xsdElementsQualified, boolean xsdAttributesQualified,
            String xsdTargetNamespace, Map<String, String> namespacesMap
    ) {
        XmlSchemaTypeField result = new XmlSchemaTypeField();
        String localName = attributes.get(XML_NAME_NAME);
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
        result.setXmlType(unpackXmlName(attributes.get(XML_NAME_TYPE), namespacesMap));
        result.setNillable(attributes.getBoolean(XML_NAME_NILLABLE));
        result.setRequired(attributes.getInteger(XML_NAME_MIN_OCCURS) > 0
                           || "required".equals(attributes.get(XML_NAME_USE)));
        result.setArray(
                "unbounded".equals(attributes.get(XML_NAME_MAX_OCCURS))
                || attributes.getInteger(XML_NAME_MAX_OCCURS) > 1
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

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
    }

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<XmlSchema>\n");
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
        sb.append("<xsdTargetNamespace>");
        sb.append(xsdTargetNamespace);
        sb.append("</xsdTargetNamespace>\n");
        sb.append("<xsdElementsQualified>");
        sb.append(xsdElementsQualified);
        sb.append("</xsdElementsQualified>\n");
        sb.append("<xsdAttributesQualified>");
        sb.append(xsdAttributesQualified);
        sb.append("</xsdAttributesQualified>\n");
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
        sb.append("</XmlSchema>\n");

        return sb.toString();
    }
}
