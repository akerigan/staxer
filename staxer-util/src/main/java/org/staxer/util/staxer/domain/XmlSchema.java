package org.staxer.util.staxer.domain;

import org.staxer.util.staxer.StaxerUtils;
import org.staxer.util.ResourceUtils;
import org.staxer.util.StringUtils;
import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.*;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.staxer.util.StringUtils.*;
import static org.staxer.util.xml.XmlConstants.*;

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
    private static final XmlName XML_NAME_FORM = new XmlName("form");
    private static final XmlName XML_NAME_MIN_OCCURS = new XmlName("minOccurs");
    private static final XmlName XML_NAME_MAX_OCCURS = new XmlName("maxOccurs");
    private static final XmlName XML_NAME_USE = new XmlName("use");
    private static final XmlName XML_NAME_NAMESPACE = new XmlName("namespace");
    private static final XmlName XML_NAME_SCHEMA_LOCATION = new XmlName("schemaLocation");
    private static final XmlName XML_NAME_FIELD_NAME = new XmlName("fieldName");
    private static final XmlName XML_NAME_REF = new XmlName("ref");

    private Map<String, String> namespacesMap = new LinkedHashMap<String, String>();

    private String xsdTargetNamespace;
    private boolean xsdElementsQualified;
    private boolean xsdAttributesQualified;

    private Map<XmlName, XmlSchemaType> typesMap = new LinkedHashMap<XmlName, XmlSchemaType>();
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

    public Map<XmlName, XmlName> getGlobalTypeElementMap() {
        return globalTypeElementMap;
    }

    public Map<XmlName, XmlName> getGlobalElementTypeMap() {
        return globalElementTypeMap;
    }

    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
        String namespace = StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_TARGET_NAMESPACE));
        if (namespace != null) {
            xsdTargetNamespace = namespace;
        }
        xsdElementsQualified = "qualified".equalsIgnoreCase(
                StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_ELEMENT_FORM_DEFAULT))
        );
        xsdAttributesQualified = "qualified".equalsIgnoreCase(
                StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_ATTRIBUTE_FORM_DEFAULT))
        );
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        xmlReader.updateNamespacesMap(namespacesMap);
        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_SCHEMA)) {
            if (xmlReader.elementStarted(XML_NAME_XSD_COMPLEX_TYPE)) {
                readXsdComplexType(xmlReader, null, null, null);
            } else if (xmlReader.elementStarted(XML_NAME_XSD_SIMPLE_TYPE)) {
                readXsdSimpleType(xmlReader, null, null, null);
            } else if (xmlReader.elementStarted(XML_NAME_XSD_ELEMENT)) {
                XmlNameMapProperties attributes = xmlReader.getAttributes();
                XmlName elementXmlName = new XmlName(
                        xsdTargetNamespace,
                        StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_NAME))
                );
                XmlName typeName = unpackXmlName(StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_TYPE)), namespacesMap);
                if (typeName == null) {
                    String documentation = null;
                    while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_ELEMENT)) {
                        if (xmlReader.elementStarted(XML_NAME_XSD_COMPLEX_TYPE)) {
                            typeName = readXsdComplexType(xmlReader, elementXmlName, null, documentation);
                        } else if (xmlReader.elementStarted(XML_NAME_XSD_SIMPLE_TYPE)) {
                            typeName = readXsdSimpleType(xmlReader, elementXmlName, null, documentation);
                        } else if (xmlReader.elementStarted(XML_NAME_XSD_DOCUMENTATION)) {
                            documentation = StringUtils.notEmptyTrimmedElseNull(
                                    xmlReader.readCharacters(XML_NAME_XSD_DOCUMENTATION)
                            );
                        }
                    }
                    if (!StringUtils.isEmpty(documentation)) {
                        XmlSchemaType xmlSchemaType = typesMap.get(typeName);
                        if (xmlSchemaType != null && StringUtils.isEmpty(xmlSchemaType.getDocumentation())) {
                            xmlSchemaType.setDocumentation(documentation);
                        }
                    }
                }
                globalTypeElementMap.put(typeName, elementXmlName);
                globalElementTypeMap.put(elementXmlName, typeName);
            } else if (xmlReader.elementStarted(XML_NAME_XSD_IMPORT) || xmlReader.elementStarted(XML_NAME_XSD_INCLUDE)) {
                XmlNameMapProperties attributes = xmlReader.getAttributes();
                String namespace;
                if (xmlReader.elementStarted(XML_NAME_XSD_IMPORT)) {
                    namespace = StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_NAMESPACE));
                } else {
                    namespace = xsdTargetNamespace;
                }
                String schemaLocation = attributes.get(XML_NAME_SCHEMA_LOCATION);
                if (!StringUtils.isEmpty(schemaLocation)) {
                    URI location = URI.create(StringUtils.notEmptyTrimmedElseNull(schemaLocation));
                    if (baseUri != null) {
                        location = baseUri.resolve(location);
                    }
                    try {
                        String xml = ResourceUtils.getUrlContentAsString(location, httpUser, httpPassword, xmlCharset);
                        if (xml != null) {
                            XmlSchema xmlSchema = StaxerUtils.readXmlSchema(
                                    namespace, location, httpUser, httpPassword, xmlCharset
                            );
                            if (xmlSchema != null) {
                                typesMap.putAll(xmlSchema.getTypesMap());
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
        for (XmlSchemaType xmlSchemaType : typesMap.values()) {
            if (xmlSchemaType instanceof XmlSchemaComplexType) {
                XmlSchemaComplexType xmlSchemaComplexType = (XmlSchemaComplexType) xmlSchemaType;
                XmlName superTypeXmlName = xmlSchemaComplexType.getSuperTypeXmlName();
                XmlSchemaType superType = typesMap.get(superTypeXmlName);
                if (superType != null && superType instanceof XmlSchemaSimpleType) {
                    XmlSchemaTypeField field = new XmlSchemaTypeField();
                    if (xmlSchemaComplexType.getJavaPackage() != null) {
                        field.setJavaName(xmlSchemaComplexType.getJavaPackage());
                        xmlSchemaComplexType.setJavaPackage(null);
                    } else {
                        field.setJavaName("value");
                    }
                    field.setValueField(true);
                    field.setXmlType(superTypeXmlName);
                    xmlSchemaComplexType.getFields().add(field);
                    xmlSchemaComplexType.setSuperTypeXmlName(null);
                }
            }
        }
    }

    private XmlName readXsdComplexType(
            StaxerXmlStreamReader xmlReader, XmlName containerElementName,
            String containerTypeName, String documentation
    ) throws StaxerXmlStreamException {
        XmlSchemaComplexType complexType = new XmlSchemaComplexType();
        XmlNameMapProperties attributes = xmlReader.getAttributes();
        String typeLocalName = StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_NAME));
        if (typeLocalName == null) {
            typeLocalName = StringUtils.join(
                    "", StringUtils.capitalize(containerTypeName),
                    StringUtils.capitalize(containerElementName.getLocalPart())
            );
        }
        XmlName xmlName = new XmlName(xsdTargetNamespace, typeLocalName);
        complexType.setXmlName(xmlName);
        complexType.setJavaName(capitalize3(typeLocalName));

        typesMap.put(complexType.getXmlName(), complexType);

        String typeDocumentation = documentation;

        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_COMPLEX_TYPE)) {
            if (xmlReader.elementStarted(XML_NAME_XSD_SIMPLE_CONTENT)) {
                XmlSchemaTypeField valueField = null;
                while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_SIMPLE_CONTENT)) {
                    if (xmlReader.elementStarted(XML_NAME_XSD_EXTENSION)) {
                        attributes = xmlReader.getAttributes();
                        valueField = new XmlSchemaTypeField();
                        XmlName xmlType = unpackXmlName(StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_BASE)), namespacesMap);
                        String fieldName = attributes.get(XML_NAME_FIELD_NAME);
                        if (NAMESPACE_URI_XSD.equals(xmlType.getNamespaceURI())) {
                            if (!StringUtils.isEmpty(fieldName)) {
                                valueField.setJavaName(fieldName);
                            } else {
                                valueField.setJavaName("value");
                            }
                            valueField.setValueField(true);
                            valueField.setXmlType(xmlType);
                        } else {
                            if (!StringUtils.isEmpty(fieldName)) {
                                complexType.setJavaPackage(fieldName);
                            }
                            complexType.setSuperTypeXmlName(xmlType);
                        }
                    } else if (xmlReader.elementStarted(XML_NAME_XSD_ATTRIBUTE)) {
                        attributes = xmlReader.getAttributes();
                        XmlName fieldXmlName = new XmlName(StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_NAME)));
                        String typeName = attributes.get(XML_NAME_TYPE);
                        XmlName xmlTypeName = null;
                        String fieldDocumentation = null;
                        if (typeName == null) {
                            while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_ATTRIBUTE)) {
                                if (xmlReader.elementStarted(XML_NAME_XSD_COMPLEX_TYPE)) {
                                    xmlTypeName = readXsdComplexType(
                                            xmlReader, fieldXmlName,
                                            complexType.getXmlName().getLocalPart(), null
                                    );
                                } else if (xmlReader.elementStarted(XML_NAME_XSD_SIMPLE_TYPE)) {
                                    xmlTypeName = readXsdSimpleType(
                                            xmlReader, fieldXmlName,
                                            complexType.getXmlName().getLocalPart(), null
                                    );
                                } else if (xmlReader.elementStarted(XML_NAME_XSD_DOCUMENTATION)) {
                                    fieldDocumentation = StringUtils.notEmptyTrimmedElseNull(
                                            xmlReader.readCharacters(XML_NAME_XSD_DOCUMENTATION)
                                    );
                                }
                            }
                        } else {
                            xmlTypeName = unpackXmlName(StringUtils.notEmptyTrimmedElseNull(typeName), namespacesMap);
                            while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_ATTRIBUTE)) {
                                if (xmlReader.elementStarted(XML_NAME_XSD_DOCUMENTATION)) {
                                    fieldDocumentation = StringUtils.notEmptyTrimmedElseNull(
                                            xmlReader.readCharacters(XML_NAME_XSD_DOCUMENTATION)
                                    );
                                }
                            }
                        }
                        XmlSchemaTypeField field = createField(attributes, fieldXmlName, xmlTypeName, false);
                        field.setDocumentation(fieldDocumentation);
                        complexType.getFields().add(field);
                    }
                }
                if (valueField != null) {
                    complexType.getFields().add(valueField);
                }
            } else if (xmlReader.elementStarted(XML_NAME_XSD_COMPLEX_CONTENT)) {
                while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_COMPLEX_CONTENT)) {
                    if (xmlReader.elementStarted(XML_NAME_XSD_EXTENSION)) {
                        attributes = xmlReader.getAttributes();
                        complexType.setSuperTypeXmlName(unpackXmlName(StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_BASE)), namespacesMap));
                    } else {
                        if (xmlReader.elementStarted(XML_NAME_XSD_SEQUENCE)) {
                            readXsdSequence(xmlReader, complexType);
                        } else if (xmlReader.elementStarted(XML_NAME_XSD_ATTRIBUTE)) {
                            attributes = xmlReader.getAttributes();
                            XmlName attributeXmlName = new XmlName(StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_NAME)));
                            String typeName = attributes.get(XML_NAME_TYPE);
                            XmlName xmlTypeName = null;
                            String fieldDocumentation = null;
                            if (typeName == null) {
                                while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_ATTRIBUTE)) {
                                    if (xmlReader.elementStarted(XML_NAME_XSD_COMPLEX_TYPE)) {
                                        xmlTypeName = readXsdComplexType(
                                                xmlReader, attributeXmlName,
                                                complexType.getXmlName().getLocalPart(), null
                                        );
                                    } else if (xmlReader.elementStarted(XML_NAME_XSD_SIMPLE_TYPE)) {
                                        xmlTypeName = readXsdSimpleType(
                                                xmlReader, attributeXmlName,
                                                complexType.getXmlName().getLocalPart(), null
                                        );
                                    } else if (xmlReader.elementStarted(XML_NAME_XSD_DOCUMENTATION)) {
                                        fieldDocumentation = StringUtils.notEmptyTrimmedElseNull(
                                                xmlReader.readCharacters(XML_NAME_XSD_DOCUMENTATION)
                                        );
                                    }
                                }
                            } else {
                                xmlTypeName = unpackXmlName(StringUtils.notEmptyTrimmedElseNull(typeName), namespacesMap);
                                while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_ATTRIBUTE)) {
                                    if (xmlReader.elementStarted(XML_NAME_XSD_DOCUMENTATION)) {
                                        fieldDocumentation = StringUtils.notEmptyTrimmedElseNull(
                                                xmlReader.readCharacters(XML_NAME_XSD_DOCUMENTATION)
                                        );
                                    }
                                }
                            }
                            XmlSchemaTypeField field = createField(attributes, attributeXmlName, xmlTypeName, false);
                            field.setDocumentation(fieldDocumentation);
                            complexType.getFields().add(field);
                        }
                    }
                }
            } else if (xmlReader.elementStarted(XML_NAME_XSD_DOCUMENTATION)) {
                typeDocumentation = StringUtils.notEmptyTrimmedElseNull(
                        xmlReader.readCharacters(XML_NAME_XSD_DOCUMENTATION)
                );
            } else {
                if (xmlReader.elementStarted(XML_NAME_XSD_SEQUENCE)) {
                    readXsdSequence(xmlReader, complexType);
                } else if (xmlReader.elementStarted(XML_NAME_XSD_ATTRIBUTE)) {
                    attributes = xmlReader.getAttributes();
                    XmlName attributeXmlName = new XmlName(StringUtils.notEmptyTrimmedElseNull(
                            attributes.get(XML_NAME_NAME)
                    ));
                    String typeName = attributes.get(XML_NAME_TYPE);
                    XmlName xmlTypeName = null;
                    String fieldDocumentation = null;
                    if (typeName == null) {
                        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_ATTRIBUTE)) {
                            if (xmlReader.elementStarted(XML_NAME_XSD_COMPLEX_TYPE)) {
                                xmlTypeName = readXsdComplexType(
                                        xmlReader, attributeXmlName,
                                        complexType.getXmlName().getLocalPart(), null
                                );
                            } else if (xmlReader.elementStarted(XML_NAME_XSD_SIMPLE_TYPE)) {
                                xmlTypeName = readXsdSimpleType(
                                        xmlReader, attributeXmlName,
                                        complexType.getXmlName().getLocalPart(), null
                                );
                            }
                        }
                    } else {
                        xmlTypeName = unpackXmlName(StringUtils.notEmptyTrimmedElseNull(typeName), namespacesMap);
                        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_ATTRIBUTE)) {
                            if (xmlReader.elementStarted(XML_NAME_XSD_DOCUMENTATION)) {
                                fieldDocumentation = StringUtils.notEmptyTrimmedElseNull(
                                        xmlReader.readCharacters(XML_NAME_XSD_DOCUMENTATION)
                                );
                            }
                        }
                    }
                    XmlSchemaTypeField field = createField(attributes, attributeXmlName, xmlTypeName, false);
                    field.setDocumentation(fieldDocumentation);
                    complexType.getFields().add(field);
                }
            }
            complexType.setDocumentation(typeDocumentation);
        }
        return xmlName;
    }

    private XmlName readXsdSimpleType(
            StaxerXmlStreamReader xmlReader, XmlName containerElementName,
            String containerTypeName, String documentation
    ) throws StaxerXmlStreamException {
        XmlSchemaSimpleType simpleType = new XmlSchemaSimpleType();
        XmlNameMapProperties attributes = xmlReader.getAttributes();
        String localName = StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_NAME));
        if (localName == null) {
            localName = StringUtils.join(
                    "", StringUtils.capitalize(containerTypeName),
                    StringUtils.capitalize(containerElementName.getLocalPart())
            );
        }
        XmlName xmlName = new XmlName(xsdTargetNamespace, localName);
        simpleType.setXmlName(xmlName);
        simpleType.setJavaName(capitalize3(localName));
        typesMap.put(simpleType.getXmlName(), simpleType);
        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_SIMPLE_TYPE)) {
            if (xmlReader.elementStarted(XML_NAME_XSD_RESTRICTION)) {
                attributes = xmlReader.getAttributes();
                simpleType.setSuperTypeXmlName(unpackXmlName(
                        StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_BASE)),
                        namespacesMap
                ));
                while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_RESTRICTION)) {
                    if (xmlReader.elementStarted(XML_NAME_XSD_ENUMERATION)) {
                        attributes = xmlReader.getAttributes();
                        String value = StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_VALUE));
                        XmlSchemaEnumValue enumValue = new XmlSchemaEnumValue();
                        enumValue.setValue(value);
                        String fieldName = attributes.get(XML_NAME_FIELD_NAME);
                        if (!StringUtils.isEmpty(fieldName)) {
                            enumValue.setJavaName(fieldName);
                        } else {
                            char first = value.charAt(0);
                            if (first >= '0' && first <= '9') {
                                enumValue.setJavaName(toEnumName("value_" + value));
                            } else {
                                enumValue.setJavaName(toEnumName(value));
                            }
                        }
                        simpleType.getValues().add(enumValue);
                    }
                }
            } else if (xmlReader.elementStarted(XML_NAME_XSD_DOCUMENTATION)) {
                documentation = StringUtils.notEmptyTrimmedElseNull(
                        xmlReader.readCharacters(XML_NAME_XSD_DOCUMENTATION)
                );
            }
        }
        simpleType.setDocumentation(documentation);
        return xmlName;
    }

    private void readXsdSequence(
            StaxerXmlStreamReader xmlReader, XmlSchemaComplexType complexType
    ) throws StaxerXmlStreamException {
        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_SEQUENCE)) {
            if (xmlReader.elementStarted(XML_NAME_XSD_ELEMENT)) {
                XmlNameMapProperties attributes = xmlReader.getAttributes();
                XmlName elementXmlName = new XmlName(StringUtils.notEmptyTrimmedElseNull(attributes.get(XML_NAME_NAME)));
                String typeName = attributes.get(XML_NAME_TYPE);
                XmlName xmlTypeName = null;
                String documentation = null;
                if (typeName == null) {
                    String ref = attributes.get(XML_NAME_REF);
                    if (!StringUtils.isEmpty(ref)) {
                        elementXmlName = unpackXmlName(ref, namespacesMap);
                        xmlTypeName = globalElementTypeMap.get(elementXmlName);
                    } else {
                        while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_ELEMENT)) {
                            if (xmlReader.elementStarted(XML_NAME_XSD_COMPLEX_TYPE)) {
                                xmlTypeName = readXsdComplexType(
                                        xmlReader, elementXmlName,
                                        complexType.getXmlName().getLocalPart(), null
                                );
                            } else if (xmlReader.elementStarted(XML_NAME_XSD_SIMPLE_TYPE)) {
                                xmlTypeName = readXsdSimpleType(
                                        xmlReader, elementXmlName,
                                        complexType.getXmlName().getLocalPart(), null
                                );
                            } else if (xmlReader.elementStarted(XML_NAME_XSD_DOCUMENTATION)) {
                                documentation = StringUtils.notEmptyTrimmedElseNull(
                                        xmlReader.readCharacters(XML_NAME_XSD_DOCUMENTATION)
                                );
                            }
                        }
                    }
                } else {
                    while (xmlReader.readNext() && !xmlReader.elementEnded(XML_NAME_XSD_ELEMENT)) {
                        if (xmlReader.elementStarted(XML_NAME_XSD_DOCUMENTATION)) {
                            documentation = StringUtils.notEmptyTrimmedElseNull(
                                    xmlReader.readCharacters(XML_NAME_XSD_DOCUMENTATION)
                            );
                        }
                    }
                    xmlTypeName = unpackXmlName(StringUtils.notEmptyTrimmedElseNull(typeName), namespacesMap);
                }
                XmlSchemaTypeField field = createField(attributes, elementXmlName, xmlTypeName, true);
                field.setDocumentation(documentation);
                complexType.getFields().add(field);
            }
        }
    }

    private XmlSchemaTypeField createField(
            XmlNameMapProperties attributes, XmlName fieldXmlName,
            XmlName xmlTypeName, boolean elementField
    ) {
        XmlSchemaTypeField result = new XmlSchemaTypeField();
        boolean thisElementQualified = "qualified".equalsIgnoreCase(attributes.get(XML_NAME_FORM));
        if (fieldXmlName.getNamespaceURI() == null
                && (elementField && (xsdElementsQualified || thisElementQualified)
                || !elementField && xsdAttributesQualified)) {
            fieldXmlName.setNamespaceURI(xsdTargetNamespace);
        }
        result.setXmlName(fieldXmlName);
        String fieldName = attributes.get(XML_NAME_FIELD_NAME);
        if (!StringUtils.isEmpty(fieldName)) {
            result.setJavaName(fieldName);
        } else {
            if ("return".equals(fieldXmlName.getLocalPart())) {
                result.setJavaName("result");
            } else if ("class".equals(fieldXmlName.getLocalPart())) {
                result.setJavaName("cls");
            } else if ("package".equals(fieldXmlName.getLocalPart())) {
                result.setJavaName("pkg");
            } else {
                result.setJavaName(decapitalize(capitalize3(fieldXmlName.getLocalPart())));
            }
        }
        result.setXmlType(xmlTypeName);
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
