package comtech.staxer.generator;

import comtech.staxer.domain.schema.*;
import comtech.staxer.domain.schema.Enumeration;
import comtech.staxer.domain.type.*;
import comtech.staxer.domain.wsdl.*;
import comtech.util.StringUtils;
import comtech.util.file.FileUtils;
import comtech.util.xml.XmlUtils;
import comtech.util.xml.read.DocumentXmlStreamReader;
import comtech.util.xml.write.DocumentXmlStreamWriter;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.*;

import static comtech.util.StringUtils.*;
import static comtech.util.xml.XmlUtils.deserialize;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 01.12.2009
 * Time: 15:21:08
 */
public class StubGenerator {

    private static Log log = LogFactory.getLog(StubGenerator.class);

    private static final String WSDL_PREFIX = "wsdl";
    private static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
    private static final String XSD_PREFIX = "xsd";
    private static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    private static final String SOAP_PREFIX = "soap";
    private static final String SOAP_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap/";

    public static final Set<String> XSD_TYPES = new HashSet<String>(
            Arrays.asList("string", "float", "int", "dateTime", "boolean", "base64Binary", "date")
    );

    public static void importWsdl(String wsdlUrl, File definitionFile) throws JAXBException, IOException, XMLStreamException {
        importWsdl(wsdlUrl, null, null, definitionFile);
    }

    public static void importWsdl(
            String wsdlUrl, String login, String password, File definitionFile
    ) throws JAXBException, IOException, XMLStreamException {
        log.info("importWsdl(" + wsdlUrl + ", " + login + ", " + password + ", " + definitionFile + ")");
        List<Wsdl> wsdlList = new LinkedList<Wsdl>();
        Wsdl wsdl = loadWsdl(wsdlUrl, login, password);
        wsdlList.add(wsdl);
        for (WsdlImport wsdlImport : wsdl.getImports()) {
            wsdlList.add(loadWsdl(wsdlImport.getLocation(), login, password));
        }
        WsDefinition wsDefinition = getDefinition(wsdlList);
        log.info("Serializing description to file: " + definitionFile.toString());
        FileUtils.mkdirs(definitionFile.getParentFile(), log);
        FileWriter writer = new FileWriter(definitionFile);
        XmlUtils.serialize(wsDefinition, writer, true);
        writer.flush();
        writer.close();
    }

    public static WsDefinition importWsdl2(
            String pathname, File definitionFile
    ) throws JAXBException, IOException, XMLStreamException {

        log.info("importWsdl(" + pathname + ", " + definitionFile + ")");

//        <wsdlPathname, wsdl>
        Map<String, Wsdl> wsdlMap = new HashMap<String, Wsdl>();

        Wsdl initialWsdl = loadWsdl2(pathname);
        wsdlMap.put(pathname, initialWsdl);

        for (WsdlImport wsdlImport : initialWsdl.getImports()) {
            String tempWsdlPathname = getFilePathname(pathname, wsdlImport.getLocation());
            Wsdl tempWsdl = loadWsdl2(tempWsdlPathname);
            wsdlMap.put(tempWsdlPathname, tempWsdl);
        }
        Wsdl resultWsdl = mergeWsdl(wsdlMap, pathname);

        // <targetNamespace, schema> set
        Map<String, Schema> tnsSchemaMap = new HashMap<String, Schema>();
        for (Schema schema : resultWsdl.getTypes().getSchemas()) {
            solveSchema(schema, tnsSchemaMap);
        }
        WsDefinition wsDefinition;
        if (tnsSchemaMap.size() == 1) {
            wsDefinition = getDefinition(Arrays.asList(resultWsdl));
        } else {
            resultWsdl.getTypes().getSchemas().clear();
            resultWsdl.getTypes().getSchemas().addAll(tnsSchemaMap.values());
            wsDefinition = getDefinition2(resultWsdl, tnsSchemaMap);
        }
        log.info("Serializing description to file: " + definitionFile.toString());
        FileUtils.mkdirs(definitionFile.getParentFile(), log);
        FileWriter writer = new FileWriter(definitionFile);
        XmlUtils.serialize(wsDefinition, writer, true);
        writer.flush();
        writer.close();
        return wsDefinition;
//        todo
    }

    private static Wsdl mergeWsdl(Map<String, Wsdl> wsdlMap, String pathname) throws JAXBException {
        if (wsdlMap != null && pathname != null) {
            Wsdl highlander = wsdlMap.remove(pathname);
            if (highlander.getTypes() != null) {
                for (Schema schema : highlander.getTypes().getSchemas()) {
                    schema.setPathname(pathname);
                }
            } else {
                highlander.setTypes(new WsdlTypes());
            }
            Iterator<Map.Entry<String, Wsdl>> iterator = wsdlMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Wsdl> nextWsdl = iterator.next();
                Wsdl wsdl = nextWsdl.getValue();
                String wsdlPath = nextWsdl.getKey();
                highlander.getPrefixXmlnsMap().putAll(wsdl.getPrefixXmlnsMap());
                if (wsdl.getTypes() != null) {
                    for (Schema schema : wsdl.getTypes().getSchemas()) {
                        schema.setPathname(wsdlPath);
                    }
                    highlander.getTypes().getSchemas().addAll(wsdl.getTypes().getSchemas());
                }
                highlander.getMessages().addAll(wsdl.getMessages());
                highlander.getPortTypes().addAll(wsdl.getPortTypes());
                iterator.remove();
            }
            highlander.getImports().clear();
            return highlander;
        } else {
            return null;
        }
    }

    /**
     * Handle schema includes and imports
     */
    private static void solveSchema(
            Schema schema, Map<String, Schema> tnsSchemaMap
    ) throws JAXBException, XMLStreamException, FileNotFoundException {
        // already processed includes data
        // <pathname, schema>
        Map<String, Schema> pathSchemaMap = new HashMap<String, Schema>();

        // handle schema includes: include data in current schema
        List<SchemaInclude> tempSchemaIncludes;
        while (schema.getSchemaIncludes().size() > 0) {

            tempSchemaIncludes = new ArrayList<SchemaInclude>();

            for (Iterator<SchemaInclude> includeIterator = schema.getSchemaIncludes().iterator(); includeIterator.hasNext(); ) {
                // load schema
                SchemaInclude schemaInclude = includeIterator.next();
                String incSchemaPathname = getFilePathname(schema.getPathname(), schemaInclude.getSchemaLocation());
                Schema incSchema = importSchema(incSchemaPathname);
                incSchema.setPathname(incSchemaPathname);
                // if new
                if (!pathSchemaMap.containsKey(incSchemaPathname)) {
                    // merge
                    if (StringUtils.isEmpty(schema.getElementFormDefault())) {
                        // qualified
                        schema.setElementFormDefault(incSchema.getElementFormDefault());
                    }
                    if (StringUtils.isEmpty(schema.getAttributeFormDefault())) {
                        // unqualified or gl
                        schema.setAttributeFormDefault(incSchema.getAttributeFormDefault());
                    }
                    // relocate import path, full pathname now
                    for (SchemaImport schemaImport : incSchema.getSchemaImports()) {
                        schemaImport.setSchemaLocation(getFilePathname(incSchemaPathname, schemaImport.getSchemaLocation()));
                    }
                    schema.getSchemaImports().addAll(incSchema.getSchemaImports());
                    schema.getComplexTypes().addAll(incSchema.getComplexTypes());
                    schema.getSimpleTypes().addAll(incSchema.getSimpleTypes());
                    schema.getElements().addAll(incSchema.getElements());
                    schema.getPrefixXmlnsMap().putAll(incSchema.getPrefixXmlnsMap());

                    tempSchemaIncludes.addAll(incSchema.getSchemaIncludes());

                    pathSchemaMap.put(incSchemaPathname, incSchema);
                }

                includeIterator.remove();
            }

            schema.getSchemaIncludes().addAll(tempSchemaIncludes);
        }

        // handle schema imports: create new schema if import not processed yet
        if (!tnsSchemaMap.containsKey(schema.getTargetNamespace())) {

            tnsSchemaMap.put(schema.getTargetNamespace(), schema);

            for (SchemaImport schemaImport : schema.getSchemaImports()) {
                Schema importedSchema = importSchema(schemaImport.getSchemaLocation());
                for (SchemaImport importedSchemaSchemaImport : importedSchema.getSchemaImports()) {
                    importedSchemaSchemaImport.setSchemaLocation(
                            getFilePathname(
                                    schemaImport.getSchemaLocation(), importedSchemaSchemaImport.getSchemaLocation()
                            )
                    );
                }
                importedSchema.setPathname(schemaImport.getSchemaLocation());
                // recursion
                solveSchema(importedSchema, tnsSchemaMap);
            }
        }
    }

    public static void importWsdl(
            Reader reader, File definitionFile
    ) throws JAXBException, IOException, XMLStreamException {
        Wsdl wsdl = loadWsdl(reader);
        WsDefinition wsDefinition = getDefinition(Arrays.asList(wsdl));
        log.info("Serializing description to file: " + definitionFile.toString());
        FileWriter writer = new FileWriter(definitionFile);
        XmlUtils.serialize(wsDefinition, writer, true);
        writer.flush();
        writer.close();
    }

    public static Wsdl loadWsdl(String wsdlUrl) throws JAXBException, XMLStreamException, FileNotFoundException {
        return loadWsdl(wsdlUrl, null, null);
    }

    public static Wsdl loadWsdl(String wsdlUrl, String login, String password) throws JAXBException, FileNotFoundException, XMLStreamException {
        log.info("loadWsdl(" + wsdlUrl + ", " + login + ", " + password + ")");
        HttpClient client = new HttpClient();

        client.getParams().setContentCharset("utf-8");

        if (login != null) {
            HttpClientParams clientParams = new HttpClientParams();
            clientParams.setAuthenticationPreemptive(true);

            client.setParams(clientParams);

            Credentials defaultcreds = new UsernamePasswordCredentials(login, password);
            client.getState().setCredentials(AuthScope.ANY, defaultcreds);
        }

        // Create a method instance.
        GetMethod method = new GetMethod(wsdlUrl);

        // Provide custom retry handler is necessary
/*
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));
*/

        String wsdlString = null;
        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                throw new IllegalStateException("Wsdl get method failed: " + method.getStatusLine());
            }

            // Read the response body.
            wsdlString = method.getResponseBodyAsString();
        } catch (HttpException e) {
            throw new IllegalStateException("Fatal protocol violation: ", e);
        } catch (IOException e) {
            throw new IllegalStateException("Fatal transport error: ", e);
        } finally {
            // Release the connection.
            method.releaseConnection();
        }

        if (wsdlString != null) {
            JAXBContext context = JAXBContext.newInstance(Wsdl.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Wsdl result = (Wsdl) unmarshaller.unmarshal(new StringReader(wsdlString));
            setWsdlNamespaces(result, new DocumentXmlStreamReader(new StringReader(wsdlString)));
            setSchemaNamespaces(result, new DocumentXmlStreamReader(new StringReader(wsdlString)));
            return result;
        } else {
            return null;
        }
    }

    public static Wsdl loadWsdl(Reader reader) throws JAXBException {
        if (reader != null) {
            JAXBContext context = JAXBContext.newInstance(Wsdl.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Wsdl) unmarshaller.unmarshal(reader);
        } else {
            return null;
        }
    }

    /**
     * Deserialize wsdl and handle all namespace used in it
     */
    public static Wsdl loadWsdl2(String pathname) throws JAXBException, XMLStreamException, FileNotFoundException {
        if (pathname != null) {
            Reader reader = new FileReader(pathname);
            JAXBContext context = JAXBContext.newInstance(Wsdl.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Wsdl result = (Wsdl) unmarshaller.unmarshal(reader);
            setWsdlNamespaces(result, new DocumentXmlStreamReader(new FileReader(pathname)));
            setSchemaNamespaces(result, new DocumentXmlStreamReader(new FileReader(pathname)));
            return result;
        } else {
            return null;
        }
    }

    /**
     * Handle namespace used in wsdl as Map<\String prefix, String namespace> field
     */
    private static void setWsdlNamespaces(Wsdl result, DocumentXmlStreamReader dxsr) throws XMLStreamException {
        if (result.getTypes() != null) {
            Map<String, String> result1;
            if (dxsr != null) {
                result1 = dxsr.getNamespacesMap("definitions", WSDL_NAMESPACE);
                dxsr.close();
            } else {
                result1 = null;
            }
            result.getPrefixXmlnsMap().putAll(result1);
        }
    }

    /**
     * Handle namespace used in schema as Map<\String prefix, String namespace> field
     */
    private static void setSchemaNamespaces(Wsdl result, DocumentXmlStreamReader dxsr) throws XMLStreamException {
        if (result.getTypes() != null) {
            Map<String, String> result1;
            if (dxsr != null) {
                result1 = dxsr.getNamespacesMap("schema", XMLConstants.W3C_XML_SCHEMA_NS_URI);
                dxsr.close();
            } else {
                result1 = null;
            }
            result.getTypes().getSchemas().get(0).getPrefixXmlnsMap().putAll(result1);
        }
    }

    /**
     * Handle namespace used in schema as Map<\String prefix, String namespace> field
     */
    private static void setSchemaNamespaces2(Schema schema, Reader reader) throws XMLStreamException {
        if (schema != null) {
            Map<String, String> result1;
            if (reader != null) {
                DocumentXmlStreamReader dxsr = new DocumentXmlStreamReader(reader);
                result1 = dxsr.getNamespacesMap("schema", XMLConstants.W3C_XML_SCHEMA_NS_URI);
                dxsr.close();
            } else {
                result1 = null;
            }
            schema.getPrefixXmlnsMap().putAll(result1);
        }
    }

    public static WsDefinition getDefinition(List<Wsdl> wsdlList) {
        WsDefinition result = new WsDefinition();

        Map<String, WsType> typesMap = new LinkedHashMap<String, WsType>();
        for (Wsdl wsdl : wsdlList) {
            WsdlTypes wsdlTypes = wsdl.getTypes();
            if (wsdlTypes != null) {
                for (Schema schema : wsdlTypes.getSchemas()) {
                    result.setTargetNamespace(schema.getTargetNamespace());
                    result.setElementFormDefault(schema.getElementFormDefault());
                    result.setAttributeFormDefault(schema.getAttributeFormDefault());

                    for (ComplexType complexType : schema.getComplexTypes()) {
                        for (WsType type : createTypes(complexType)) {
                            typesMap.put(type.getName(), type);
                        }
                    }
                    for (SimpleType simpleType : schema.getSimpleTypes()) {
                        WsType wsType = createEnum(simpleType);
                        if (wsType == null) {
                            wsType = new WsType();
                            wsType.setName(decapitalize(simpleType.getName()));
                            wsType.setJavaTypeName(capitalize3(simpleType.getName()));
                            wsType.setXmlElementName(simpleType.getName());
                            WsField field = new WsField();
                            field.setName("value");
                            field.setTypeName(cleanPrefix(simpleType.getRestriction().getBase()));
                            field.setValue(true);
                            wsType.getFields().add(field);
                        }
                        typesMap.put(wsType.getName(), wsType);
                    }
                    for (Element element : schema.getElements()) {
                        String elementName = element.getName();
                        if (element.getComplexType() != null) {
                            List<WsType> innerTypes = createTypes(element.getComplexType());
                            innerTypes.get(0).setXmlElementName(elementName);
                            for (WsType type : innerTypes) {
                                typesMap.put(type.getName(), type);
                            }
                        }
                        if (element.getSimpleType() != null) {
                            WsType enumType = createEnum(element.getSimpleType());
                            enumType.setXmlElementName(elementName);
                            typesMap.put(enumType.getName(), enumType);
                        }
                        String typeName = cleanPrefix(element.getType());
                        WsType type;
                        if ("anyType".equals(typeName)) {
                            type = typesMap.get(elementName);
                        } else {
                            type = typesMap.get(typeName);
                        }
                        if (type != null) {
                            type.setXmlElementName(elementName);
                            typesMap.put(elementName, type);
                        }
                    }
                }
            }
        }

        for (WsType type : typesMap.values()) {
            type.setJavaTypeName(capitalize3(type.getName()));
            for (WsField field : type.getFields()) {
                String fieldName = field.getName();
                if ("return".equals(fieldName)) {
                    fieldName = "result";
                } else if ("class".equals(fieldName)) {
                    fieldName = "cls";
                } else if ("package".equals(fieldName)) {
                    fieldName = "pkg";
                }
                field.setJavaName(decapitalize(capitalize3(fieldName)));
                field.setEnum(typesMap.get(field.getTypeName()) != null && typesMap.get(field.getTypeName()).isEnumType());
                field.setJavaTypeName(getJavaTypeName(field.getTypeName()));
            }
        }

        Map<String, WsMessage> messagesMap = new TreeMap<String, WsMessage>();
        for (Wsdl wsdl : wsdlList) {
            for (WsdlMessage wsdlMessage : wsdl.getMessages()) {
                WsMessage wsMessage = new WsMessage();
                wsMessage.setName(wsdlMessage.getName());
                wsMessage.setElementName(decapitalize(capitalize2(wsdlMessage.getPart().getName(), false)));
                wsMessage.setElementType(typesMap.get(cleanPrefix(wsdlMessage.getPart().getElement())));
                messagesMap.put(wsMessage.getName(), wsMessage);
            }
        }
//      TODO fix for list
        for (Wsdl wsdl : wsdlList) {
            for (WsdlPortType wsdlPortType : wsdl.getPortTypes()) {
                if (wsdlPortType != null) {
                    for (WsdlOperation wsdlOperation : wsdlPortType.getOperations()) {
                        WsMethod wsMethod = new WsMethod();
                        wsMethod.setName(decapitalize(capitalize2(wsdlOperation.getName(), false)));

                        WsMessage inMessage = messagesMap.get(cleanPrefix(wsdlOperation.getInput().getMessageName()));
                        wsMethod.setInName(decapitalize(capitalize2(inMessage.getElementName(), false)));
                        WsType requestType = inMessage.getElementType();
                        if (!requestType.getJavaTypeName().endsWith("Request")) {
                            if (requestType.getJavaTypeName().endsWith("Req")) {
                                requestType.setJavaTypeName(requestType.getJavaTypeName() + "uest");
                            } else {
                                requestType.setJavaTypeName(requestType.getJavaTypeName() + "Request");
                            }
                        }
                        wsMethod.setInType(requestType.getJavaTypeName());

                        WsMessage outMessage = messagesMap.get(cleanPrefix(wsdlOperation.getOutput().getMessageName()));
                        wsMethod.setOutType(outMessage.getElementType().getJavaTypeName());

                        result.getMethods().add(wsMethod);
                    }
                    result.setName(wsdlPortType.getName());
                }
            }
        }

        result.getTypes().addAll(typesMap.values());

        return result;
    }

    /**
     * Get definition for complex wsdl
     */
    public static WsDefinition getDefinition2(Wsdl wsdl, Map<String, Schema> tnsSchemaMap) {

        WsDefinition result = new WsDefinition();

        result.setTargetNamespace(wsdl.getTargetNamespace());

        result.setElementFormDefault(wsdl.getElementFormDefault());
        result.setAttributeFormDefault(wsdl.getAttributeFormDefault());

//        <xmlns, <typeName, WsType>>
        Map<String, Map<String, WsType>> xmlnsTypesMap = new LinkedHashMap<String, Map<String, WsType>>();

        for (Schema schema : wsdl.getTypes().getSchemas()) {

            Map<String, WsType> typesMap = new LinkedHashMap<String, WsType>();
            xmlnsTypesMap.put(schema.getTargetNamespace(), typesMap);

            for (ComplexType complexType : schema.getComplexTypes()) {
                for (WsType type : createTypes2(complexType, schema, xmlnsTypesMap, null)) {
                    typesMap.put(type.getName(), type);
                }
            }
            for (SimpleType simpleType : schema.getSimpleTypes()) {
                if ((simpleType.getRestriction() != null && simpleType.getRestriction().getEnumerations().size() > 0)
                    || simpleType.getUnion() != null) {
                    WsType wsType = createEnum2(simpleType, schema.getPrefixXmlnsMap(), xmlnsTypesMap);
                    if (wsType == null) {
                        wsType = new WsType();
                        wsType.setName(decapitalize(simpleType.getName()));
                        wsType.setJavaTypeName(capitalize3(simpleType.getName()));
                        wsType.setXmlElementName(simpleType.getName());
                        WsField field = new WsField();
                        field.setName("value");
                        field.setPrefix(getPrefix(simpleType.getRestriction().getBase()));
                        field.setNsURI(schema.getPrefixXmlnsMap().get(getPrefix(simpleType.getRestriction().getBase())));
                        field.setTypeName(cleanPrefix(simpleType.getRestriction().getBase()));
                        field.setValue(true);
                        wsType.getFields().add(field);
                    }
                    typesMap.put(wsType.getName(), wsType);
                } else if (simpleType.getRestriction() != null) {
                    WsType wsType = new WsType();
                    wsType.setPrefix(getPrefix(simpleType.getName()));
                    wsType.setNsURI(schema.getPrefixXmlnsMap().get(getPrefix(simpleType.getName())));
                    wsType.setName(cleanPrefix(simpleType.getName()));
                    wsType.setEnumType(false);
                    wsType.setJavaTypeName(cleanPrefix(simpleType.getRestriction().getBase()));
                    typesMap.put(wsType.getName(), wsType);
                }
            }
            for (Element element : schema.getElements()) {
                String elementName = element.getName() != null ? element.getName() : cleanPrefix(element.getRef());
                if (element.getComplexType() != null) {
                    List<WsType> innerTypes = createTypes2(element.getComplexType(), schema, xmlnsTypesMap, element.getName());
                    innerTypes.get(0).setXmlElementName(elementName);
                    for (WsType type : innerTypes) {
                        typesMap.put(type.getName(), type);
                    }
                }
                if (element.getSimpleType() != null) {
                    if ((element.getSimpleType().getRestriction() != null & element.getSimpleType().getRestriction().getEnumerations().size() > 0)
                        || element.getSimpleType().getUnion() != null) {
                        WsType enumType = createEnum2(element.getSimpleType(), schema.getPrefixXmlnsMap(), xmlnsTypesMap);
                        enumType.setXmlElementName(elementName);
                        typesMap.put(enumType.getName(), enumType);
                    } else if (element.getSimpleType().getRestriction() != null) {
                        WsType wsType = new WsType();
                        wsType.setPrefix(getPrefix(element.getSimpleType().getName()));
                        wsType.setNsURI(schema.getPrefixXmlnsMap().get(getPrefix(element.getSimpleType().getName())));
                        wsType.setName(cleanPrefix(element.getSimpleType().getName()));
                        wsType.setEnumType(false);
                        wsType.setJavaTypeName(cleanPrefix(element.getSimpleType().getRestriction().getBase()));
                        typesMap.put(wsType.getName(), wsType);
                    }
                }
                String typeName = cleanPrefix(element.getType());
                WsType type;
                if ("anyType".equals(typeName)) {
                    type = typesMap.get(elementName);
                } else {
                    type = typesMap.get(typeName);
                }
                if (type != null) {
                    type.setPrefix(getPrefix(element.getType()));
                    type.setNsURI(schema.getPrefixXmlnsMap().get(getPrefix(element.getType())));
                    type.setXmlElementName(elementName);
                    typesMap.put(elementName, type);
                }
            }
        }

        for (Map.Entry<String, Map<String, WsType>> tnsTypeEntry : xmlnsTypesMap.entrySet()) {
            for (WsType type : tnsTypeEntry.getValue().values()) {
                type.setJavaTypeName(capitalize3(type.getName()));
                for (WsField field : type.getFields()) {
                    String fieldName = field.getName();
                    if ("return".equals(fieldName)) {
                        fieldName = "result";
                    } else if ("class".equals(fieldName)) {
                        fieldName = "cls";
                    } else if ("package".equals(fieldName)) {
                        fieldName = "pkg";
                    }
                    field.setJavaName(decapitalize(capitalize3(fieldName)));
                    field.setEnum(
                            !XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(field.getNsURI()) &&
                            xmlnsTypesMap.get(field.getNsURI()).get(field.getTypeName()) != null &&
                            xmlnsTypesMap.get(field.getNsURI()).get(field.getTypeName()).isEnumType()
                    );
                    field.setJavaTypeName(getJavaTypeName(field.getTypeName()));
                }
            }
        }

        Map<String, WsMessage> messagesMap = new TreeMap<String, WsMessage>();
        for (WsdlMessage wsdlMessage : wsdl.getMessages()) {
            WsMessage wsMessage = new WsMessage();
            wsMessage.setName(wsdlMessage.getName());
            wsMessage.setElementName(decapitalize(capitalize2(wsdlMessage.getPart().getName(), false)));
            String nsURI = wsdl.getPrefixXmlnsMap().get(getPrefix(wsdlMessage.getPart().getElement()));
            wsMessage.setElementType(xmlnsTypesMap.get(nsURI).get(cleanPrefix(wsdlMessage.getPart().getElement())));

            messagesMap.put(wsMessage.getName(), wsMessage);
        }
        /**
         * travelport wsdl is kind of map one operation (named "service") to one portType ("portTypeSomeName")
         * but WsMessages created as if set of operations (renamed from "service" to their "portTypeSomeName" appropriately)
         * mapped to one portType (renamed to wsdl name, e.g. "AirService")
         */
        for (WsdlPortType wsdlPortType : wsdl.getPortTypes()) {
            if (wsdlPortType != null) {
                for (WsdlOperation wsdlOperation : wsdlPortType.getOperations()) {
                    WsMethod wsMethod = new WsMethod();
                    wsMethod.setName(decapitalize(capitalize2(wsdlPortType.getName(), false)));

                    WsMessage inMessage = messagesMap.get(cleanPrefix(wsdlOperation.getInput().getMessageName()));
                    wsMethod.setInName(decapitalize(capitalize2(inMessage.getElementName(), false)));
                    WsType requestType = inMessage.getElementType();
                    if (!requestType.getJavaTypeName().endsWith("Request")) {
                        requestType.setJavaTypeName(requestType.getJavaTypeName() + "Request");
                    }
                    wsMethod.setInType(requestType.getJavaTypeName());

                    WsMessage outMessage = messagesMap.get(cleanPrefix(wsdlOperation.getOutput().getMessageName()));
                    wsMethod.setOutType(outMessage.getElementType().getJavaTypeName());

                    result.getMethods().add(wsMethod);
                }
                result.setName(wsdl.getName());
            }
        }
        // WsDefinition.types
        List<WsType> types = new LinkedList<WsType>();
        for (Map<String, WsType> typeMap : xmlnsTypesMap.values()) {
            types.addAll(typeMap.values());
        }
        result.getTypes().addAll(types);

        // <xmlns, <typeName, WsType>>
        result.getXmlnsTypesMap().putAll(xmlnsTypesMap);

        // <packageNameLastPart, <prefix, xmlns>>
        for (Schema schema : wsdl.getTypes().getSchemas()) {
            String[] schemaTnsSplit = schema.getTargetNamespace().split("/");
            result.getPackageXmlnsMap().put(schemaTnsSplit[schemaTnsSplit.length - 1], schema.getPrefixXmlnsMap());
        }

        return result;
    }

    public static WsDefinition loadDefinition(File definitionFile) throws IOException, JAXBException {
        log.info("loadDefinition(" + definitionFile + ")");
        if (!definitionFile.exists()) {
            throw new IllegalStateException("definition file not found: " + definitionFile.toString());
        }
        log.info("Loading definition from: " + definitionFile.toString());
        return loadDefinition(new FileReader(definitionFile));
    }

    public static WsDefinition loadDefinition(Reader reader) throws IOException, JAXBException {
        return deserialize(WsDefinition.class, reader);
    }

    public static void generateStub(
            File definitionFile, File sourceDir, String packageName,
            boolean generateClientService, boolean generateServerService
    ) throws Exception {
        generateStubByStringBuilder(
                loadDefinition(definitionFile), sourceDir, packageName,
                generateClientService, generateServerService
        );
    }

    public static void generateStubByVelocity(
            WsDefinition wsDefinition, File sourceDir, String packageName,
            boolean generateClientService, boolean generateServerService
    ) throws Exception {
        log.info("generateStub(" + wsDefinition + ", " + sourceDir + ", " + packageName + ", "
                 + generateClientService + ", " + generateServerService + ")");
        if (wsDefinition == null) {
            throw new IllegalStateException("definition is empty");
        }

        File destDir = new File(sourceDir, packageName.replaceAll("\\.", "/"));
        File beansDir = new File(destDir, "bean");
        FileUtils.createOrCleanupDir(beansDir, log);

        // save current definiton
/*
        File defFile = new File(beansDir.toString(), "definition.xml");
        FileWriter defFileWriter = new FileWriter(defFile);
        XmlUtils.serialize(wsDefinition, defFileWriter, true);
        defFileWriter.flush();
        defFileWriter.close();
*/

        // initialize velocity template engine
        Properties properties = new Properties();
        InputStream stream = StubGenerator.class.getResourceAsStream("/velocity.properties");
        if (stream != null) {
            properties.load(stream);
        }
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init(properties);

        StringResourceRepository repository = StringResourceLoader.getRepository();

        for (String templatePath : new String[]{"/xmlBean.vm", "/clientService.vm", "/serverService.vm"}) {
            stream = StubGenerator.class.getResourceAsStream(templatePath);
            StringWriter writer = new StringWriter();
            IOUtils.copy(stream, writer);
            String templateName = templatePath.split("\\.|/")[1];
            repository.putStringResource(templateName, writer.toString());
        }

        // generate ws beans
        Map<String, String> typesElements = new HashMap<String, String>();
        for (WsType type : wsDefinition.getTypes()) {
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("namespace", wsDefinition.getTargetNamespace());
            velocityContext.put("elementFormQualified", !"unqualified".equals(wsDefinition.getElementFormDefault()));
            velocityContext.put("attributeFormQualified", "qualified".equals(wsDefinition.getAttributeFormDefault()));
            velocityContext.put("packageName", packageName + ".bean");
            velocityContext.put("type", type.getSuperTypeName() == null ? type : extendTypeFields(type, wsDefinition.getTypes()));
            Template template = velocityEngine.getTemplate("xmlBean");
            File file = new File(beansDir, type.getJavaTypeName() + ".java");
            Writer writer = new FileWriter(file);
            template.merge(velocityContext, writer);
            writer.flush();
            writer.close();
            String xmlElementName = type.getXmlElementName();
            if (!StringUtils.isEmpty(xmlElementName)) {
                typesElements.put(type.getJavaTypeName(), xmlElementName);
            }
        }

        // generate ws service
        if (generateClientService) {
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("packageName", packageName);
            String serviceName = "Client" + capitalize2(wsDefinition.getName(), false);
            String serverServiceName = "Server" + capitalize2(wsDefinition.getName(), false);
            if (!serviceName.toUpperCase().endsWith("WS")) {
                serviceName += "Ws";
            }
            velocityContext.put("serviceName", serviceName);
            velocityContext.put("serverServiceName", serverServiceName);
            velocityContext.put("methods", wsDefinition.getMethods());
            Template template = velocityEngine.getTemplate("clientService");

            File file = new File(destDir, serviceName + ".java");
            Writer writer = new FileWriter(file);
            template.merge(velocityContext, writer);
            writer.flush();
            writer.close();
        }

        if (generateServerService) {
            Template template = velocityEngine.getTemplate("serverService");
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("packageName", packageName);
            velocityContext.put("beanPackageName", packageName + ".bean");

            String serviceName = "Server" + capitalize2(wsDefinition.getName(), false);
            if (!serviceName.toUpperCase().endsWith("WS")) {
                serviceName += "Ws";
            }
            velocityContext.put("serviceName", serviceName);

            velocityContext.put("namespace", wsDefinition.getTargetNamespace());
            velocityContext.put("methods", wsDefinition.getMethods());
            velocityContext.put("typesElements", typesElements);

            File file = new File(destDir, serviceName + ".java");
            Writer writer = new FileWriter(file);
            template.merge(velocityContext, writer);
            writer.flush();
            writer.close();
        }
    }

    private static WsType extendTypeFields(WsType type, List<WsType> typesElements) {
        if (type != null && typesElements != null) {

            WsType result = new WsType();

            result.setName(type.getName());
            result.setJavaTypeName(type.getJavaTypeName());
            result.setXmlElementName(type.getXmlElementName());
            result.setSuperTypeName(type.getSuperTypeName());

            result.setEnumType(type.isEnumType());

            result.getEnumValues().addAll(type.getEnumValues());
            result.getFields().addAll(type.getFields());

            String superTypeName;
            do {
                superTypeName = type.getSuperTypeName();
                for (WsType wsType : typesElements) {
                    if (superTypeName.equals(wsType.getJavaTypeName())) {
                        superTypeName = wsType.getSuperTypeName();
                        result.getContainedFields().addAll(wsType.getFields());
                        break;
                    }
                }
            }
            while (superTypeName != null);
            return result;
        } else {
            return null;
        }
    }

    public static void generateStubByStringBuilder(
            WsDefinition wsDefinition, File sourceDir, String packageName,
            boolean generateClientService, boolean generateServerService
    ) throws Exception {
        log.info("generateStub(" + wsDefinition + ", " + sourceDir + ", " + packageName + ", "
                 + generateClientService + ", " + generateServerService + ")");
        if (wsDefinition == null) {
            throw new IllegalStateException("definition is empty");
        }

        File destDir = new File(sourceDir, packageName.replaceAll("\\.", "/"));
        File beansDir = new File(destDir, "bean");
        FileUtils.createOrCleanupDir(beansDir, log);

        // generate ws beans
        Map<String, String> typesElements = new HashMap<String, String>();
        for (WsType type : wsDefinition.getTypes()) {
            String namespace = wsDefinition.getTargetNamespace();
            boolean elementFormQualified = !"unqualified".equals(wsDefinition.getElementFormDefault());
            boolean attributeFormQualified = "qualified".equals(wsDefinition.getAttributeFormDefault());
            String beanPackageName;
            File file;
            String packageNameLastPart = null;
            if ("".equals(type.getNsURI())) {
                beanPackageName = packageName + ".bean";
                file = new File(beansDir, type.getJavaTypeName() + ".java");
            } else {
                packageNameLastPart = type.getNsURI().split("/")[type.getNsURI().split("/").length - 1];
                beanPackageName = packageName + ".bean." + packageNameLastPart;
                File packageFile = new File(beansDir, packageNameLastPart);
                if (!packageFile.exists()) {
                    boolean isPackageDirCreated = packageFile.mkdirs();
                }
                file = new File(packageFile, type.getJavaTypeName() + ".java");
            }

            StringBuilder sb;
            if (type.isEnumType()) {
                sb = generateWsBeanEnum(beanPackageName, type);
            } else {
                boolean isPackage = wsDefinition.getPackageXmlnsMap().size() > 0;
                List<String> imports = null;
                if (isPackage) {
                    imports = new ArrayList<String>();
                    for (String ns : wsDefinition.getPackageXmlnsMap().get(packageNameLastPart).values()) {
                        if (!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(ns) || !type.getNsURI().equals(ns)) {
                            imports.add(packageName + ".bean." + ns.split("/")[type.getNsURI().split("/").length - 1]);
                        }
                    }
                }
                sb = generateWsBeanClass(
                        type.getSuperTypeName() == null ? type : extendTypeFields(type, wsDefinition.getTypes()),
                        beanPackageName, namespace, elementFormQualified, attributeFormQualified,
                        isPackage, imports
                );
            }

            Writer writer = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.append(sb);
            bw.close();

            String xmlElementName = type.getXmlElementName();
            if (!StringUtils.isEmpty(xmlElementName)) {
                typesElements.put(type.getJavaTypeName(), xmlElementName);
            }
        }

        // generate package info
        for (Map.Entry<String, Map<String, String>> packageInfo : wsDefinition.getPackageXmlnsMap().entrySet()) {
            StringBuilder sb = generatePackageInfo(packageName + ".bean." + packageInfo.getKey(), packageInfo.getValue());
            Writer writer = new FileWriter(new File(new File(beansDir, packageInfo.getKey()), "package-info.java"));
            BufferedWriter bw = new BufferedWriter(writer);
            bw.append(sb);
            bw.close();
        }

        // generate ws service
        if (generateClientService) {
            String serviceName = "Client" + capitalize2(wsDefinition.getName(), false);
            if (!serviceName.toUpperCase().endsWith("WS")) {
                serviceName += "Ws";
            }
            StringBuilder sb = generateClientService(wsDefinition, packageName, serviceName);
            File file = new File(destDir, serviceName + ".java");
            Writer writer = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.append(sb);
            bw.close();
        }
        if (generateServerService) {
            String beanPackageName = packageName + ".bean";
            String serviceName = "Server" + capitalize2(wsDefinition.getName(), false);
            if (!serviceName.toUpperCase().endsWith("WS")) {
                serviceName += "Ws";
            }
            String namespace = wsDefinition.getTargetNamespace();
            List<WsMethod> methods = wsDefinition.getMethods();
            StringBuilder sb = generateServiceService(packageName, typesElements, beanPackageName, serviceName, namespace, methods);
            File file = new File(destDir, serviceName + ".java");
            Writer writer = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.append(sb);
            bw.close();
        }
    }

    private static StringBuilder generateWsBeanClass(
            WsType type, String packageName, String namespace,
            boolean elementFormQualified, boolean attributeFormQualified, boolean isPackage,
            List<String> imports
    ) {
        StringBuilder result = new StringBuilder();
        result.append("package ");
        result.append(packageName);
        result.append(";\n");
        result.append("\n");
        result.append("import comtech.util.date.DateTimeUtils;\n");
        result.append("import comtech.staxer.server.HttpParametersParser;\n");
        result.append("import comtech.util.servlet.helper.HttpHelper;\n");
        result.append("import comtech.staxer.soap.StaxerXmlHandler;\n");
        result.append("import comtech.util.xml.read.DocumentXmlStreamReader;\n");
        result.append("import comtech.util.xml.read.StartElement;\n");
        result.append("import comtech.util.xml.write.DocumentXmlStreamWriter;\n");
        result.append("import comtech.util.xml.XmlName;\n");
        result.append("import comtech.util.StringUtils;\n");
        result.append("import org.apache.commons.logging.Log;\n");
        result.append("import org.apache.commons.logging.LogFactory;\n");
        if (isPackage) {
            for (String anImport : imports) {
                result.append("import ").append(anImport).append(";\n");
            }
        }
        result.append("\n");
        result.append("import javax.xml.bind.annotation.*;\n");
        result.append("import javax.xml.datatype.*;\n");
        result.append("import javax.xml.stream.XMLStreamConstants;\n");
        result.append("import javax.xml.stream.XMLStreamException;\n");
        result.append("import java.text.ParseException;\n");
        result.append("import javax.xml.XMLConstants;\n");
        result.append("import java.io.StringReader;\n");
        result.append("import java.io.StringWriter;\n");
        result.append("import java.util.*;\n");
        result.append("import java.math.BigDecimal;\n");
        result.append("\n");

        String typeXmlElementName = type.getXmlElementName();
        String typeJavaTypeName = type.getJavaTypeName();
        String typeSuperTypeName = type.getSuperTypeName();

        if (typeXmlElementName != null) {
            result.append("@XmlRootElement(name = ");
            result.append(typeJavaTypeName);
            result.append(".ROOT_ELEMENT");
            if (!isPackage) {
                result.append(", namespace = ");
                result.append(typeJavaTypeName);
                result.append(".NAMESPACE_URI");
            }
            result.append(")\n");
        }
        result.append("@XmlAccessorType(XmlAccessType.FIELD)\n");
        // isComplexRequest
        boolean isComplexRequest = false;
        if (type.isRequest()) {
            for (WsField field : type.getContainedFields()) {
                String fieldJavaTypeName = field.getJavaTypeName();
                if (!"Integer".equals(fieldJavaTypeName)
                    && !"String".equals(fieldJavaTypeName)
                    && !"Boolean".equals(fieldJavaTypeName)
                    && !"Float".equals(fieldJavaTypeName)
                    && !"Double".equals(fieldJavaTypeName)
                    && !"BigDecimal".equals(fieldJavaTypeName)
                    && !"Character".equals(fieldJavaTypeName)
                    && !field.isDateTime()
                    && !field.isDate()
                    && !field.isEnum()) {

                    isComplexRequest = true;
                    break;

                }
            }
            if (!isComplexRequest && typeSuperTypeName != null) {
                result.append("public class ");
                result.append(typeJavaTypeName);
                result.append(" extends ");
                result.append(typeSuperTypeName);
                result.append(" implements StaxerXmlHandler, HttpParametersParser {\n");
            } else if (!isComplexRequest) {
                result.append("public class ");
                result.append(typeJavaTypeName);
                result.append(" implements StaxerXmlHandler, HttpParametersParser {\n");
            } else if (typeSuperTypeName != null) {
                result.append("public class ");
                result.append(typeJavaTypeName);
                result.append(" extends ");
                result.append(typeSuperTypeName);
                result.append(" implements StaxerXmlHandler {\n");
            } else {
                result.append("public class ");
                result.append(typeJavaTypeName);
                result.append(" implements StaxerXmlHandler {\n");
            }
        } else if (typeSuperTypeName != null) {
            result.append("public class ");
            result.append(typeJavaTypeName);
            result.append(" extends ");
            result.append(typeSuperTypeName);
            result.append(" implements StaxerXmlHandler {\n");
            result.append("\n");
        } else {
            result.append("public class ");
            result.append(typeJavaTypeName);
            result.append(" implements StaxerXmlHandler {\n");
        }
        result.append("\n");
        result.append("    private static final Log LOG = LogFactory.getLog(");
        result.append(typeJavaTypeName);
        result.append(".class);\n");
        result.append("\n");
        if (typeXmlElementName != null) {
            result.append("    public static final String NAMESPACE_URI = \"");
            result.append(namespace);
            result.append("\";\n");
            result.append("    public static final String NAMESPACE_PREFIX = \"cws\";\n");
            result.append("    public static final String ROOT_ELEMENT = \"");
            result.append(typeXmlElementName);
            result.append("\";\n");
            result.append("\n");
        }

        boolean isAttributesExists = false;
        boolean isElementListPresent = false;

        for (WsField field : type.getFields()) {
            String fieldName = field.getName();
            boolean fieldRequired = field.isRequired();
            boolean fieldNillable = field.isNillable();
            String fieldJavaTypeName = field.getJavaTypeName();
            if (field.isXmlElement()) {
                if (elementFormQualified) {
                    result.append("    @XmlElement(name = \"");
                    result.append(fieldName);
                    if (!isPackage) {
                        // without prefix care - staxer feature
                        result.append("\", namespace = \"");
                        result.append(namespace);
                    } else {
                        if (type.getPrefix() != null) {
                            result.append("\", namespace = \"");
                            result.append(type.getNsURI());
                        }
                    }
                    result.append("\", required = ");
                    result.append(fieldRequired);
                    result.append(", nillable = ");
                    result.append(fieldNillable);
                    result.append(")\n");
                } else {
                    result.append("    @XmlElement(name = \"");
                    result.append(fieldName);
                    if (!isPackage) {
                        // do nothing - staxer feature
                    } else {
                        if (type.getPrefix() != null) {
                            result.append("\", namespace = \"");
                            result.append(type.getNsURI());
                        }
                    }
                    result.append("\", required = ");
                    result.append(fieldRequired);
                    result.append(", nillable = ");
                    result.append(fieldNillable);
                    result.append(")\n");
                }
            } else if (field.isValue()) {
                result.append("    @XmlValue\n");
            } else {
                isAttributesExists = true;
                if (attributeFormQualified) {
                    result.append("    @XmlAttribute(name = \"");
                    result.append(fieldName);
                    result.append("\", namespace = \"");
                    result.append(namespace);
                    result.append("\", required = ");
                    result.append(fieldRequired);
                    result.append(")\n");
                } else {
                    result.append("    @XmlAttribute(name = \"");
                    result.append(fieldName);
                    result.append("\", required = ");
                    result.append(fieldRequired);
                    result.append(")\n");
                }
            }
            if (field.isList()) {
                isElementListPresent = true;
                result.append("    private List<");
                result.append(fieldJavaTypeName);
                result.append("> ");
                result.append(field.getJavaName());
                result.append(" = new ArrayList<");
                result.append(fieldJavaTypeName);
                result.append(">();\n");
            } else {
                if (field.isDateTime()) {
                    result.append("    @XmlSchemaType(name = \"dateTime\")\n");
                }
                result.append("    private ");
                result.append(fieldJavaTypeName);
                result.append(" ");
                result.append(field.getJavaName());
                result.append(";\n");
            }
            result.append("\n");
        }
        for (WsField field : type.getFields()) {
            String fieldJavaTypeName = field.getJavaTypeName();
            String fieldJavaNameCap = field.getJavaNameCap();
            String javaName = field.getJavaName();
            if (field.isList()) {
                result.append("    public List<");
                result.append(fieldJavaTypeName);
                result.append("> get");
                result.append(fieldJavaNameCap);
                result.append("() {\n");
                result.append("        return ");
                result.append(javaName);
                result.append(";\n");
                result.append("    }\n");
            } else {
                result.append("    public ");
                result.append(fieldJavaTypeName);
                result.append(" get");
                result.append(fieldJavaNameCap);
                result.append("() {\n");
                result.append("        return ");
                result.append(javaName);
                result.append(";\n");
                result.append("    }\n");
                result.append("\n");
                result.append("    public void set");
                result.append(fieldJavaNameCap);
                result.append("(");
                result.append(fieldJavaTypeName);
                result.append(" ");
                result.append(javaName);
                result.append(") {\n");
                result.append("        this.");
                result.append(javaName);
                result.append(" = ");
                result.append(javaName);
                result.append(";\n");
                result.append("    }\n");
            }
            result.append("\n");
        }
        //
        result.append("\n");
        result.append("    public void callWriteXml(DocumentXmlStreamWriter dxsr, StaxerXmlHandler contentObj, String nsPrefix, String nsUri) throws XMLStreamException {\n");
        result.append("        ");
        result.append(typeJavaTypeName);
        result.append(".writeXml(dxsr, (");
        result.append(typeJavaTypeName);
        result.append(") contentObj, nsPrefix, nsUri);\n");
        result.append("    }\n\n");
        result.append("    public void callWriteXmlAsSoapBody(StaxerXmlHandler contentObj, DocumentXmlStreamWriter dxsr) throws XMLStreamException {\n");
        if (typeXmlElementName != null) {
            result.append("        ");
            result.append(typeJavaTypeName);
            result.append(".writeXmlAsSoapBody((");
            result.append(typeJavaTypeName);
            result.append(") contentObj, dxsr);\n");
        } else {
            result.append("        // do nothing\n");
        }
        result.append("    }\n\n");
        result.append("    public void callWriteXmlAsSoapBody(StaxerXmlHandler contentObj, DocumentXmlStreamWriter dxsr, String nsPrefix, String rootName, String nsUri) throws XMLStreamException {\n");
        result.append("        ");
        result.append(typeJavaTypeName);
        result.append(".writeXmlAsSoapBody((");
        result.append(typeJavaTypeName);
        result.append(") contentObj, dxsr, nsPrefix, rootName, nsUri);\n");
        result.append("    }\n\n");
        result.append("    public String callWriteXmlAsRoot(StaxerXmlHandler contentObj) throws XMLStreamException {\n");
        if (typeXmlElementName != null) {
            result.append("        return ");
            result.append(typeJavaTypeName);
            result.append(".writeXmlAsRoot((");
            result.append(typeJavaTypeName);
            result.append(") contentObj);\n");
        } else {
            result.append("        // do nothing\n");
            result.append("        return null;\n");
        }
        result.append("    }\n\n");
        result.append("    public String callWriteXmlAsRoot(StaxerXmlHandler contentObj, String nsPrefix, String rootName, String nsUri) throws XMLStreamException {\n");
        result.append("        return ");
        result.append(typeJavaTypeName);
        result.append(".writeXmlAsRoot((");
        result.append(typeJavaTypeName);
        result.append(") contentObj, nsPrefix, rootName, nsUri);\n");
        result.append("    }\n");
        //
        result.append("\n");
        result.append("    public ");
        result.append(typeJavaTypeName);
        result.append(" callReadXml(DocumentXmlStreamReader dxsr) throws XMLStreamException {\n");
        result.append("        return ");
        result.append(typeJavaTypeName);
        result.append(".readXml(dxsr);\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    public ");
        result.append(typeJavaTypeName);
        result.append(" callReadXmlAsRoot(String responseXml) throws XMLStreamException {\n");
        if (typeXmlElementName != null) {
            result.append("        return ");
            result.append(typeJavaTypeName);
            result.append(".readXmlAsRoot(responseXml);\n");
        } else {
            result.append("        return null;\n");
        }
        result.append("    }\n");
        result.append("\n");
        result.append("    public ");
        result.append(typeJavaTypeName);
        result.append(" callReadXmlAsRoot(String responseXml, String rootName, String namespaceUri) throws XMLStreamException {\n");
        if (typeXmlElementName != null) {
            result.append("        return ");
            result.append(typeJavaTypeName);
            result.append(".readXmlAsRoot(responseXml, rootName, namespaceUri);\n");
        } else {
            result.append("        return null;\n");
        }
        result.append("    }\n");
        result.append("\n");
        // read Xml
        result.append("    public static ");
        result.append(typeJavaTypeName);
        result.append(" readXml(DocumentXmlStreamReader dxsr) throws XMLStreamException {\n");
        result.append("\n");
        if (isElementListPresent) {
            result.append("        ");
            result.append(typeJavaTypeName);
            result.append(" result = new ");
            result.append(typeJavaTypeName);
            result.append("();\n");
        } else {
            result.append("        ");
            result.append(typeJavaTypeName);
            result.append(" result = null;\n");
        }
        for (WsField field : type.getFields()) {
            boolean fieldDateTime = field.isDateTime();
            if (fieldDateTime) {
                result.append("        DatatypeFactory df = null;\n");
                result.append("        try {\n");
                result.append("            df = DatatypeFactory.newInstance();\n");
                result.append("        } catch (DatatypeConfigurationException e) {\n");
                result.append("            LOG.error(\"\", e);\n");
                result.append("        }\n");
                result.append("\n");
                break;
            }
        }
        if (isAttributesExists) {
            result.append("        Map<String, String> attributes = dxsr.getAttributes();\n");
            result.append("\n");
            result.append("        if (attributes != null) {\n");
            result.append("            for (Map.Entry<String, String> entry : attributes.entrySet()) {\n");
            boolean isFirstAttribute = true;
            for (WsField field : type.getFields()) {
                if (!field.isXmlElement() && !field.isValue()) {
                    String fieldName = field.getName();
                    if (isFirstAttribute) {
                        isFirstAttribute = false;
                        result.append("                    if (\"");
                        result.append(fieldName);
                        result.append("\".equals(entry.getKey())) {\n");
                    } else {
                        result.append(" else if (entry.getKey().equals(\"");
                        result.append(fieldName);
                        result.append("\")) {\n");
                    }
                    String javaTypeName = field.getJavaTypeName();
                    if ("String".equals(javaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                        if (result == null) {\n");
                            result.append("                            result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                        }\n");
                        }
                        result.append("                        result.set");
                        result.append(field.getJavaNameCap());
                        result.append("(entry.getValue());\n");
                    } else if ("Integer".equals(javaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                        if (result == null) {\n");
                            result.append("                            result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                        }\n");
                        }
                        result.append("                        result.set");
                        result.append(field.getJavaNameCap());
                        result.append("(Integer.valueOf(entry.getValue()));\n");
                    } else if (field.isDateTime()) {
                        if (!isElementListPresent) {
                            result.append("                        if (result == null) {\n");
                            result.append("                            result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                        }\n");
                        }
                        result.append("                        if (df != null) {\n");
                        result.append("                            result.set");
                        result.append(field.getJavaNameCap());
                        result.append("(df.newXMLGregorianCalendar(entry.getValue()));\n");
                        result.append("                        }\n");
                    } else if (field.isDate()) {
                        if (!isElementListPresent) {
                            result.append("                        if (result == null) {\n");
                            result.append("                            result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                        }\n");
                        }
                        result.append("                        try {\n");
                        result.append("                            result.set");
                        result.append(field.getJavaNameCap());
                        result.append("(DateTimeUtils.parseDate(entry.getValue()));\n");
                        result.append("                        } catch (ParseException e) {\n");
                        result.append("                            LOG.error(\"An error has been reached unexpectedly while parsing\", e);\n");
                        result.append("                        }\n");
                    } else if ("Float".equals(javaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                        if (result == null) {\n");
                            result.append("                            result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                        }\n");
                        }
                        result.append("                        result.set");
                        result.append(field.getJavaNameCap());
                        result.append("(Float.valueOf(entry.getValue()));\n");
                    } else if ("Double".equals(javaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                        if (result == null) {\n");
                            result.append("                            result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                        }\n");
                        }
                        result.append("                        result.set");
                        result.append(field.getJavaNameCap());
                        result.append("(Double.valueOf(entry.getValue()));\n");
                    } else if ("BigDecimal".equals(javaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                        if (result == null) {\n");
                            result.append("                            result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                        }\n");
                        }
                        result.append("                        result.set");
                        result.append(field.getJavaNameCap());
                        result.append("(new BigDecimal(entry.getValue()));\n");
                    } else if ("Character".equals(javaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                        if (result == null) {\n");
                            result.append("                            result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                        }\n");
                        }
                        result.append("                        result.set");
                        result.append(field.getJavaNameCap());
                        result.append("(entry.getValue().charAt(0));\n");
                    } else if (field.isEnum()) {
                        if (!isElementListPresent) {
                            result.append("                        if (result == null) {\n");
                            result.append("                            result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                        }\n");
                        }
                        result.append("                        result.set");
                        result.append(field.getJavaNameCap());
                        result.append("(");
                        result.append(javaTypeName);
                        result.append(".getByCode(entry.getValue()));\n");
                    } else if ("Boolean".equals(javaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                        if (result == null) {\n");
                            result.append("                            result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                        }\n");
                        }
                        result.append("                        result.set");
                        result.append(field.getJavaNameCap());
                        result.append("(StringUtils.parseBoolean(entry.getValue(), false));\n");
                    }
                    result.append("                    }");
                }
            }
            result.append("            }\n");
            result.append("        }\n");
        }
        result.append("        while (dxsr.hasNext()) {\n");
        result.append("\n");
        result.append("            int event = dxsr.next();\n");
        result.append("\n");
        result.append("            if (event == XMLStreamConstants.END_ELEMENT) {\n");
        result.append("                break;\n");
        result.append("            } else if (event == XMLStreamConstants.START_ELEMENT) {\n");
        boolean isFirstElement = true;
        for (WsField field : type.getFields()) {
            if (field.isXmlElement() || field.isValue()) {
                if (isFirstElement) {
                    isFirstElement = false;
                    result.append("\n");
                    result.append("                XmlName xmlName = dxsr.getXmlName();\n");
                    result.append("                String name = xmlName.getLocalPart();\n");
                    result.append("                if (\"");
                    result.append(field.getName());
                    result.append("\".equals(name)) {\n");
                } else {
                    result.append(" else if (\"");
                    result.append(field.getName());
                    result.append("\".equals(name)) {\n");
                }
                String fieldJavaNameCap = field.getJavaNameCap();
                String fieldJavaTypeName = field.getJavaTypeName();
                if (field.isList()) {
                    if (field.isEnum()) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        result.append("                    result.get");
                        result.append(fieldJavaNameCap);
                        result.append("().add(");
                        result.append(fieldJavaTypeName);
                        result.append(".getByCode(value));\n");
                    } else if ("String".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        result.append("                    result.get");
                        result.append(fieldJavaNameCap);
                        result.append("().add(value);\n");
                    } else if ("Integer".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        result.append("                    result.get");
                        result.append(fieldJavaNameCap);
                        result.append("().add(Integer.valueOf(value));\n");
                    } else if ("Boolean".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        result.append("                    result.get");
                        result.append(fieldJavaNameCap);
                        result.append(".add(StringUtils.parseBoolean(value, false));\n");
                    } else if (field.isDateTime()) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    if (df != null) {\n");
                        result.append("                        String value = dxsr.readEndElement(true);\n");
                        result.append("                        result.get");
                        result.append(fieldJavaNameCap);
                        result.append("().add(df.newXMLGregorianCalendar(value));\n");
                        result.append("                    }\n");
                    } else if (field.isDate()) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        result.append("                    try {\n");
                        result.append("                        result.get");
                        result.append(fieldJavaNameCap);
                        result.append("().add(DateTimeUtils.parseDate(value));\n");
                        result.append("                    } catch (ParseException e) {\n");
                        result.append("                        LOG.error(\"An error has been reached unexpectedly while parsing\", e);\n");
                        result.append("                    }\n");
                    } else if ("Float".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        result.append("                    result.get");
                        result.append(fieldJavaNameCap);
                        result.append("().add(Float.valueOf(value));\n");
                    } else if ("Double".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        result.append("                    result.get");
                        result.append(fieldJavaNameCap);
                        result.append("().add(Double.valueOf(value));\n");
                    } else if ("BigDecimal".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        result.append("                    result.get");
                        result.append(fieldJavaNameCap);
                        result.append("().add(new BigDecimal(value));\n");
                    } else if ("Character".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        result.append("                    result.get");
                        result.append(fieldJavaNameCap);
                        result.append("().add(value.charAt(0));\n");
                    } else {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    result.get");
                        result.append(fieldJavaNameCap);
                        result.append("().add(");
                        result.append(fieldJavaTypeName);
                        result.append(".readXml(dxsr));\n");
                    }
                } else {
                    if (field.isEnum()) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        if (field.isNillable()) {
                            result.append("                    if (!\"\".equals(value)) {\n");
                            result.append("                        result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(");
                            result.append(fieldJavaTypeName);
                            result.append(".getByCode(value));\n");
                            result.append("                    }\n");
                        } else {
                            result.append("                    result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(");
                            result.append(fieldJavaTypeName);
                            result.append(".getByCode(value));\n");
                        }

                    } else if ("String".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        if (field.isNillable()) {
                            result.append("                    if (!\"\".equals(value)) {\n");
                            result.append("                        result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(value);\n");
                            result.append("                    }\n");
                        } else {
                            result.append("                    result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(value);\n");
                        }
                    } else if ("Integer".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        if (field.isNillable()) {
                            result.append("                    if (!\"\".equals(value)) {\n");
                            result.append("                        result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(Integer.valueOf(value));\n");
                            result.append("                    }\n");
                        } else {
                            result.append("                    result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(Integer.valueOf(value));\n");
                        }
                    } else if (field.isDateTime()) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    if (df != null) {\n");
                        result.append("                        String value = dxsr.readEndElement(true);\n");
                        if (field.isNillable()) {
                            result.append("                        if (!\"\".equals(value)) {\n");
                            result.append("                            result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(df.newXMLGregorianCalendar(value));\n");
                            result.append("                        }\n");
                        } else {
                            result.append("                        result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(df.newXMLGregorianCalendar(value));\n");
                        }
                        result.append("                    }\n");
                    } else if (field.isDate()) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        if (field.isNillable()) {
                            result.append("                    if (!\"\".equals(value)) {\n");
                            result.append("                    try {\n");
                            result.append("                        result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(DateTimeUtils.parseDate(value));\n");
                            result.append("                    } catch (ParseException e) {\n");
                            result.append("                        LOG.error(\"An error has been reached unexpectedly while parsing\", e);\n");
                            result.append("                    }\n");
                            result.append("                    }\n");
                        } else {
                            result.append("                    try {\n");
                            result.append("                        result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(DateTimeUtils.parseDate(value));\n");
                            result.append("                    } catch (ParseException e) {\n");
                            result.append("                        LOG.error(\"An error has been reached unexpectedly while parsing\", e);\n");
                            result.append("                    }\n");
                        }
                    } else if ("Float".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        if (field.isNillable()) {
                            result.append("                    if (!\"\".equals(value)) {\n");
                            result.append("                        result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(Float.valueOf(value));\n");
                            result.append("                    }\n");
                        } else {
                            result.append("                    result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(Float.valueOf(value));\n");
                        }
                    } else if ("Double".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        if (field.isNillable()) {
                            result.append("                    if (!\"\".equals(value)) {\n");
                            result.append("                        result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(Double.valueOf(value));\n");
                            result.append("                    }\n");
                        } else {
                            result.append("                    result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(Double.valueOf(value));\n");
                        }
                    } else if ("BigDecimal".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        if (field.isNillable()) {
                            result.append("                    if (!\"\".equals(value)) {\n");
                            result.append("                        result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(new BigDecimal(value));\n");

                            result.append("                    }\n");
                        } else {
                            result.append("                    result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(new BigDecimal(value));\n");

                        }
                    } else if ("Character".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        if (field.isNillable()) {
                            result.append("                    if (!\"\".equals(value)) {\n");
                            result.append("                        result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(value.charAt(0));\n");
                            result.append("                    }\n");
                        } else {
                            result.append("                    result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(value.charAt(0));\n");
                        }
                    } else if ("byte[]".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        if (field.isNillable()) {
                            result.append("                    if (!\"\".equals(value)) {\n");
                            result.append("                        result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(value.getBytes());\n");
                            result.append("                    }\n");
                        } else {
                            result.append("                    result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(value.getBytes());\n");
                        }
                    } else if ("Boolean".equals(fieldJavaTypeName)) {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    String value = dxsr.readEndElement(true);\n");
                        if (field.isNillable()) {
                            result.append("                    if (!\"\".equals(value)) {\n");
                            result.append("                        result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(StringUtils.parseBoolean(value, false));\n");
                            result.append("                    }\n");
                        } else {
                            result.append("                    result.set");
                            result.append(fieldJavaNameCap);
                            result.append("(StringUtils.parseBoolean(value, false));\n");
                        }
                    } else {
                        if (!isElementListPresent) {
                            result.append("                    if (result == null) {\n");
                            result.append("                        result = new ");
                            result.append(typeJavaTypeName);
                            result.append("();\n");
                            result.append("                    }\n");
                        }
                        result.append("                    result.set");
                        result.append(fieldJavaNameCap);
                        result.append("(");
                        result.append(fieldJavaTypeName);
                        result.append(".readXml(dxsr));\n");
                    }
                }
                result.append("                }");
            }
        }
        if (isFirstElement) {
            result.append("                dxsr.readEndElement(false);\n");
        } else {
            result.append(" else {\n");
            result.append("                    dxsr.readEndElement(false);\n");
            result.append("                }\n");
        }
        result.append("            }\n");
        result.append("        }\n");
        result.append("        return result;\n");
        result.append("    }\n");
        result.append("\n");
        // WRITE XML
        result.append("    public static void writeXml(\n");
        result.append("            DocumentXmlStreamWriter dxsw, ");
        result.append(typeJavaTypeName);
        result.append(" contentObject, String nsPrefix, String nsUri\n");
        result.append("    ) throws XMLStreamException {\n");
        result.append("        if (contentObject != null) {\n");
        for (WsField field : type.getFields()) {
            if (!field.isXmlElement() && !field.isValue()) {
                result.append("            dxsw.attribute(\"");
                result.append(field.getName());
                result.append("\", contentObject.get");
                result.append(field.getJavaNameCap());
                result.append("());\n");
            }
        }
        boolean isNillabelElementsExists = false;
        for (WsField field : type.getFields()) {
            if (field.isXmlElement()) {
                String fieldJavaTypeName = field.getJavaTypeName();
                String fieldJavaNameCap = field.getJavaNameCap();
                if (field.isList()) {
                    if ("Integer".equals(fieldJavaTypeName)
                        || "String".equals(fieldJavaTypeName)
                        || "Boolean".equals(fieldJavaTypeName)
                        || "Float".equals(fieldJavaTypeName)
                        || "Double".equals(fieldJavaTypeName)
                        || "BigDecimal".equals(fieldJavaTypeName)
                        || "Character".equals(fieldJavaTypeName)
                        || field.isDateTime()
                        || field.isDate()
                        || field.isEnum()) {

                        result.append("            for (");
                        result.append(fieldJavaTypeName);
                        result.append(" data : contentObject.get");
                        result.append(fieldJavaNameCap);
                        result.append("()) {\n");
                        result.append("                dxsw.startElement(\"");
                        result.append(field.getName());
                        result.append("\");\n");
                        result.append("                dxsw.text(data);\n");
                        result.append("                dxsw.endElement();\n");
                        result.append("        }\n");
                    } else {
                        result.append("            for (");
                        result.append(fieldJavaTypeName);
                        result.append(" obj : contentObject.get");
                        result.append(fieldJavaNameCap);
                        result.append("()) {\n");
                        result.append("                dxsw.startElement(\"");
                        result.append(field.getName());
                        result.append("\");\n");
                        result.append("                ");
                        result.append(fieldJavaTypeName);
                        result.append(".writeXml(dxsw, obj, nsPrefix, nsUri);\n");
                        result.append("                dxsw.endElement();\n");
                        result.append("            }\n");
                    }
                } else {
                    if ("Integer".equals(fieldJavaTypeName)
                        || "String".equals(fieldJavaTypeName)
                        || "Boolean".equals(fieldJavaTypeName)
                        || "Float".equals(fieldJavaTypeName)
                        || "Double".equals(fieldJavaTypeName)
                        || "BigDecimal".equals(fieldJavaTypeName)
                        || "Character".equals(fieldJavaTypeName)
                        || "byte[]".equals(fieldJavaTypeName)
                        || field.isDateTime()
                        || field.isDate()
                        || field.isEnum()) {
                        result.append("            if (contentObject.get");
                        result.append(fieldJavaNameCap);
                        result.append("() != null) {\n");
                        result.append("                dxsw.startElement(\"");
                        result.append(field.getName());
                        result.append("\");\n");
                        result.append("                dxsw.text(contentObject.get");
                        result.append(fieldJavaNameCap);
                        result.append("());\n");
                        result.append("                dxsw.endElement();\n");
                        if (field.isNillable()) {
                            isNillabelElementsExists = true;
                            result.append("            } else {\n");
                            result.append("                dxsw.xsiNillElement(\"" + field.getName() + "\");\n");
                        }
                        result.append("            }\n");
                    } else {
                        result.append("            if (contentObject.get");
                        result.append(fieldJavaNameCap);
                        result.append("() != null) {\n");
                        result.append("                dxsw.startElement(\"");
                        result.append(field.getName());
                        result.append("\");\n");
                        result.append("            ");
                        result.append(fieldJavaTypeName);
                        result.append(".writeXml(dxsw, contentObject.get");
                        result.append(fieldJavaNameCap);
                        result.append("(), nsPrefix, nsUri);\n");
                        result.append("            dxsw.endElement();\n");
                        if (field.isNillable()) {
                            isNillabelElementsExists = true;
                            result.append("        } else {\n");
                            result.append("            dxsw.xsiNillElement(\"" + field.getName() + "\");\n");
                        }
                        result.append("        }\n");
                    }
                }
            }
        }
        for (WsField field : type.getFields()) {
            if (field.isValue()) {
                result.append("        if (contentObject.get");
                result.append(field.getJavaNameCap());
                result.append("() != null) {\n");
                result.append("            dxsw.text(contentObject.get");
                result.append(field.getJavaNameCap());
                result.append("());\n");
                result.append("    }\n");
            }
        }
        result.append("        }\n");
        result.append("    }\n");
        if (typeXmlElementName != null) {
            result.append("\n");
            result.append("    public static ");
            result.append(typeJavaTypeName);
            result.append(" readXmlAsRoot(String responseXml) throws XMLStreamException {\n");
            result.append("        StringReader sr = new StringReader(responseXml);\n");
            result.append("        DocumentXmlStreamReader dxsr = new DocumentXmlStreamReader(sr);\n");
            result.append("\n");
            result.append("        ");
            result.append(typeJavaTypeName);
            result.append(" result = null;\n");
            result.append("\n");
            result.append("        StartElement startElement = dxsr.getStartElement(ROOT_ELEMENT, NAMESPACE_URI);\n");
            result.append("        if (startElement != null) {\n");
            result.append("            result = ");
            result.append(typeJavaTypeName);
            result.append(".readXml(dxsr);\n");
            result.append("        }\n");
            result.append("        dxsr.close();\n");
            result.append("        return result;\n");
            result.append("    }\n");
            result.append("\n");
        }
        result.append("    public static ");
        result.append(typeJavaTypeName);
        result.append(" readXmlAsRoot(String responseXml, String rootName, String namespaceUri) throws XMLStreamException {\n");
        result.append("        StringReader sr = new StringReader(responseXml);\n");
        result.append("        DocumentXmlStreamReader dxsr = new DocumentXmlStreamReader(sr);\n");
        result.append("\n");
        result.append("        ");
        result.append(typeJavaTypeName);
        result.append(" result = null;\n");
        result.append("\n");
        result.append("        StartElement startElement = dxsr.getStartElement(rootName, namespaceUri);\n");
        result.append("        if (startElement != null) {\n");
        result.append("            result = ");
        result.append(typeJavaTypeName);
        result.append(".readXml(dxsr);\n");
        result.append("        }\n");
        result.append("        dxsr.close();\n");
        result.append("        return result;\n");
        result.append("\n");
        result.append("    }\n");
        if (typeXmlElementName != null) {
            result.append("\n");
            result.append("    public static void writeXmlAsSoapBody(\n");
            result.append("            ");
            result.append(typeJavaTypeName);
            result.append(" contentObject, DocumentXmlStreamWriter dxsw\n");
            result.append("    ) throws XMLStreamException {\n");
            result.append("        dxsw.startElement(NAMESPACE_PREFIX, ROOT_ELEMENT, NAMESPACE_URI);\n");
            result.append("        dxsw.namespace(NAMESPACE_PREFIX, NAMESPACE_URI);\n");
            if (isNillabelElementsExists) {
                result.append("        dxsw.namespace(\"xsi\", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);\n");
            }
            result.append("        ");
            result.append(typeJavaTypeName);
            result.append(".writeXml(dxsw, contentObject, NAMESPACE_PREFIX, NAMESPACE_URI);\n");
            result.append("        dxsw.endElement();\n");
            result.append("    }\n");
            result.append("\n");
        }
        result.append("    public static void writeXmlAsSoapBody(\n");
        result.append("            ");
        result.append(typeJavaTypeName);
        result.append(" contentObject, DocumentXmlStreamWriter dxsw,\n");
        result.append("            String nsPrefix, String rootName, String nsUri\n");
        result.append("    ) throws XMLStreamException {\n");
        result.append("        nsPrefix = nsPrefix != null ? nsPrefix : \"cws\";\n");
        result.append("        dxsw.startElement(nsPrefix, rootName, nsUri);\n");
        result.append("        dxsw.namespace(nsPrefix, nsUri);\n");
        if (isNillabelElementsExists) {
            result.append("        dxsw.namespace(\"xsi\", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);\n");
        }
        result.append("        ");
        result.append(typeJavaTypeName);
        result.append(".writeXml(dxsw, contentObject, nsPrefix, nsUri);\n");
        result.append("        dxsw.endElement();\n");
        result.append("    }\n");
        result.append("\n");

        if (typeXmlElementName != null) {
            result.append("\n");
            result.append("    public static String writeXmlAsRoot(\n");
            result.append("            ");
            result.append(typeJavaTypeName);
            result.append(" contentObject\n");
            result.append("    ) throws XMLStreamException {\n");
            result.append("        StringWriter sw = new StringWriter();\n");
            result.append("        DocumentXmlStreamWriter dxsw = new DocumentXmlStreamWriter(sw, true);\n");
            result.append("        dxsw.startDocument(\"UTF-8\", \"1.0\");\n");
            result.append("        dxsw.startElement(NAMESPACE_PREFIX, ROOT_ELEMENT, NAMESPACE_URI);\n");
            result.append("        dxsw.namespace(NAMESPACE_PREFIX, NAMESPACE_URI);\n");
            if (isNillabelElementsExists) {
                result.append("        dxsw.namespace(\"xsi\", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);\n");
            }
            result.append("        ");
            result.append(typeJavaTypeName);
            result.append(".writeXml(dxsw, contentObject, NAMESPACE_PREFIX, NAMESPACE_URI);\n");
            result.append("        dxsw.endElement();\n");
            result.append("        dxsw.endDocument();\n");
            result.append("        return sw.toString();\n");
            result.append("    }\n");
            result.append("\n");
        }
        result.append("    public static String writeXmlAsRoot(\n");
        result.append("            ");
        result.append(typeJavaTypeName);
        result.append(" contentObject, String nsPrefix, String rootName, String nsUri\n");
        result.append("    ) throws XMLStreamException {\n");
        result.append("        StringWriter sw = new StringWriter();\n");
        result.append("        DocumentXmlStreamWriter dxsw = new DocumentXmlStreamWriter(sw, true);\n");
        result.append("        nsPrefix = nsPrefix != null ? nsPrefix : \"cws\";\n");
        result.append("        dxsw.startDocument(\"UTF-8\", \"1.0\");\n");
        result.append("        dxsw.startElement(nsPrefix, rootName, nsUri);\n");
        result.append("        dxsw.namespace(nsPrefix, nsUri);\n");
        if (isNillabelElementsExists) {
            result.append("        dxsw.namespace(\"xsi\", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);\n");
        }
        result.append("        ");
        result.append(typeJavaTypeName);
        result.append(".writeXml(dxsw, contentObject, nsPrefix, nsUri);\n");
        result.append("        dxsw.endElement();\n");
        result.append("        dxsw.endDocument();\n");
        result.append("        return sw.toString();\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    @Override\n");
        result.append("    public String toString() {\n");
        result.append("        final StringBuilder sb = new StringBuilder();\n");
        result.append("        sb.append(\"<");
        result.append(typeJavaTypeName);
        result.append(">\\n\");\n");
        result.append("        toString(sb);\n");
        result.append("        sb.append(\"</");
        result.append(typeJavaTypeName);
        result.append(">\\n\");\n");
        result.append("        return sb.toString();\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    public void toString(StringBuilder sb) {\n");
        if (typeSuperTypeName != null) {
            result.append("        super.toString(sb);\n");
        }
        for (WsField field : type.getFields()) {
            String fieldJavaName = field.getJavaName();
            if (field.isList()) {
                result.append("        if (");
                result.append(fieldJavaName);
                result.append(" != null) {\n");
                result.append("            sb.append(\"<");
                result.append(fieldJavaName);
                result.append(">\");\n");
                result.append("            for (Object obj : ");
                result.append(fieldJavaName);
                result.append(") {\n");
                result.append("                sb.append(\"<item>\");\n");
                result.append("                sb.append(obj);\n");
                result.append("                sb.append(\"</item>\\n\");\n");
                result.append("            }\n");
                result.append("            sb.append(\"</");
                result.append(fieldJavaName);
                result.append(">\\n\");\n");
                result.append("        } else {\n");
                result.append("            sb.append(\"<");
                result.append(fieldJavaName);
                result.append("/>\\n\");\n");
                result.append("        }\n");
            } else {
                result.append("        sb.append(\"<");
                result.append(fieldJavaName);
                result.append(">\");\n");
                result.append("        sb.append(");
                result.append(fieldJavaName);
                result.append(");\n");
                result.append("        sb.append(\"</");
                result.append(fieldJavaName);
                result.append(">\\n\");\n");
            }
        }
        result.append("    }\n");
        result.append("\n");
        // parseHttpParameters(...)
        if (type.isRequest() && !isComplexRequest) {
            result.append("    public void parseHttpParameters(HttpHelper httpHelper){\n");
            for (WsField field : type.getFields()) {
                String fieldName = field.getName();
                String fieldJavaName = field.getJavaName();
                String fieldJavaTypeName = field.getJavaTypeName();
                if (field.isList()) {
                    if (field.isEnum()) {
                        result.append("        String[] tempArrayString = httpHelper.getRequestParameters(\"");
                        result.append(fieldName);
                        result.append("\");\n");
                        result.append("        ");
                        String fieldTypeName = field.getTypeName();
                        result.append(fieldTypeName);
                        result.append("[] tempArrayEnum = new ");
                        result.append(fieldTypeName);
                        result.append("[tempArrayString.length];\n");
                        result.append("        for (int i = 0; i < tempArrayString.length; i++) {\n");
                        result.append("            tempArrayEnum[i] = ");
                        result.append(fieldTypeName);
                        result.append(".getByCode(tempArrayString[i]);\n");
                        result.append("        }\n");
                        result.append("        ");
                        result.append(fieldJavaName);
                        result.append(" = Arrays.asList(tempArrayEnum);\n");
                    } else if ("String".equals(fieldJavaTypeName)) {
                        result.append("        ");
                        result.append(fieldJavaName);
                        result.append(" = Arrays.asList(httpHelper.getRequestParameters(\"");
                        result.append(fieldName);
                        result.append("\"));\n");
                    } else if ("Integer".equals(fieldJavaTypeName)) {
                        result.append("        ");
                        result.append(fieldJavaName);
                        result.append(" = Arrays.asList(httpHelper.getIntInstanceRequestParameters(\"");
                        result.append(fieldName);
                        result.append("\"));\n");
                    } else if (field.isDateTime()) {
                        result.append("        ");
                        result.append(fieldJavaName);
                        result.append(" = Arrays.asList(httpHelper.getXMLGregorianCalendarRequestParameters(\"");
                        result.append(fieldName);
                        result.append("\"));\n");
                    } else if (field.isDate()) {
                        result.append("        ");
                        result.append(fieldJavaName);
                        result.append(" = Arrays.asList(httpHelper.getDateRequestParameters(\"");
                        result.append(fieldName);
                        result.append("\", null));\n");
                    } else if ("Float".equals(fieldJavaTypeName)) {
                        result.append("        ");
                        result.append(fieldJavaName);
                        result.append(" = Arrays.asList(httpHelper.getFloatInstanceRequestParameters(\"");
                        result.append(fieldName);
                        result.append("\"));\n");
                    } else if ("Double".equals(fieldJavaTypeName)) {
                        result.append("        ");
                        result.append(fieldJavaName);
                        result.append(" = Arrays.asList(httpHelper.getDoubleInstanceRequestParameters(\"");
                        result.append(fieldName);
                        result.append("\"));\n");
                    } else if ("BigDecimal".equals(fieldJavaTypeName)) {
                        result.append("        ");
                        result.append(fieldJavaName);
                        result.append(" = Arrays.asList(httpHelper.getBigDecimalRequestParameters(\"");
                        result.append(fieldName);
                        result.append("\"));\n");
                    } else if ("Character".equals(fieldJavaTypeName)) {
                        result.append("        ");
                        result.append(fieldJavaName);
                        result.append(" = Arrays.asList(httpHelper.getCharacterRequestParameters(\"");
                        result.append(fieldName);
                        result.append("\"));\n");
                    } else if ("Boolean".equals(fieldJavaTypeName)) {
                        result.append("        ");
                        result.append(fieldJavaName);
                        result.append(" = Arrays.asList(httpHelper.getBooleanInstanceRequestParameters(\"");
                        result.append(fieldName);
                        result.append("\"));\n");
                    }
                } else if (field.isEnum()) {
                    result.append("        ");
                    result.append(fieldJavaName);
                    result.append(" = ");
                    result.append(fieldJavaTypeName);
                    result.append(".getByCode(httpHelper.getRequestParameter(\"");
                    result.append(fieldName);
                    result.append("\"));\n");
                } else if ("String".equals(fieldJavaTypeName)) {
                    result.append("        ");
                    result.append(fieldJavaName);
                    result.append(" = httpHelper.getRequestParameter(\"");
                    result.append(fieldName);
                    result.append("\");\n");
                } else if ("Integer".equals(fieldJavaTypeName)) {
                    result.append("        ");
                    result.append(fieldJavaName);
                    result.append(" = httpHelper.getIntInstanceRequestParameter(\"");
                    result.append(fieldName);
                    result.append("\");\n");
                } else if (field.isDateTime()) {
                    result.append("        ");
                    result.append(fieldJavaName);
                    result.append(" = httpHelper.getXMLGregorianCalendarRequestParameter(\"");
                    result.append(fieldName);
                    result.append("\");\n");
                } else if (field.isDate()) {
                    result.append("        ");
                    result.append(fieldJavaName);
                    result.append(" = httpHelper.getDateRequestParameter(\"");
                    result.append(fieldName);
                    result.append("\");\n");
                } else if ("Float".equals(fieldJavaTypeName)) {
                    result.append("        ");
                    result.append(fieldJavaName);
                    result.append(" = httpHelper.getFloatInstanceRequestParameter(\"");
                    result.append(fieldName);
                    result.append("\");\n");
                } else if ("Double".equals(fieldJavaTypeName)) {
                    result.append("        ");
                    result.append(fieldJavaName);
                    result.append(" = httpHelper.getDoubleInstanceRequestParameter(\"");
                    result.append(fieldName);
                    result.append("\");\n");
                } else if ("BigDecimal".equals(fieldJavaTypeName)) {
                    result.append("        ");
                    result.append(fieldJavaName);
                    result.append(" = httpHelper.getBigDecimalRequestParameter(\"");
                    result.append(fieldName);
                    result.append("\");\n");
                } else if ("Character".equals(fieldJavaTypeName)) {
                    result.append("        ");
                    result.append(fieldJavaName);
                    result.append(" = httpHelper.getCharacterRequestParameter(\"");
                    result.append(fieldName);
                    result.append("\");\n");
                } else if ("Boolean".equals(fieldJavaTypeName)) {
                    result.append("        ");
                    result.append(fieldJavaName);
                    result.append(" = httpHelper.getBooleanInstanceRequestParameter(\"");
                    result.append(fieldName);
                    result.append("\");\n");
                } else {
                    result.append("        //do nothing\n");
                }
            }
            result.append("    }\n");
        }
        result.append("}\n");
        //
        return result;
    }

    private static StringBuilder generateWsBeanEnum(String packageName, WsType type) {
        StringBuilder result = new StringBuilder();
        result.append("package ");
        result.append(packageName);
        result.append(";\n");
        result.append("\n");

        result.append("import javax.xml.bind.annotation.*;\n");
        result.append("import java.util.Map;\n");
        result.append("import java.util.HashMap;\n");
        result.append("\n");
        result.append("@XmlEnum\n");
        result.append("public enum ");
        String typeJavaTypeName = type.getJavaTypeName();
        result.append(typeJavaTypeName);
        result.append(" {\n");
        result.append("\n");
        int count = 1;
        for (WsEnumValue enumValue : type.getEnumValues()) {
            result.append("@XmlEnumValue(\"");
            result.append(enumValue.getValue());
            result.append("\")\n");
            if (count == type.getEnumValues().size()) {
                result.append("");
                result.append(enumValue.getJavaName());
                result.append("(\"");
                result.append(enumValue.getValue());
                result.append("\");\n");
            } else {
                result.append("");
                result.append(enumValue.getJavaName());
                result.append("(\"");
                result.append(enumValue.getValue());
                result.append("\"),\n");
            }
            count++;
        }
        result.append("\n");
        result.append("    private static Map <String, ");
        result.append(typeJavaTypeName);
        result.append("> map;\n");
        result.append("    private String code;\n");
        result.append("\n");
        result.append("    static {\n");
        result.append("        map = new HashMap <String, ");
        result.append(typeJavaTypeName);
        result.append(">();\n");
        result.append("        for (");
        result.append(typeJavaTypeName);
        result.append(" value : ");
        result.append(typeJavaTypeName);
        result.append(".values()) {\n");
        result.append("            map.put(value.code, value);\n");
        result.append("        }\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    ");
        result.append(typeJavaTypeName);
        result.append("(String code) {\n");
        result.append("        this.code = code;\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    public String getCode() {\n");
        result.append("        return code;\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    public static ");
        result.append(typeJavaTypeName);
        result.append(" getByCode(String code) {\n");
        result.append("        return map.get(code);\n");
        result.append("    }\n");
        result.append("\n");
        result.append("}\n");
        return result;
    }

    private static StringBuilder generateClientService(WsDefinition wsDefinition, String packageName, String serviceName) {
        StringBuilder result = new StringBuilder();
        result.append("package ");
        result.append(packageName);
        result.append(";\n");
        result.append("\n");
        result.append("import comtech.staxer.client.HttpWsClient;\n");
        result.append("import comtech.staxer.client.WsClientException;\n");
        result.append("import comtech.staxer.client.WsRequest;\n");
        result.append("import ");
        result.append(packageName);
        result.append(".bean.*;\n");
        result.append("\n");
        result.append("import javax.xml.stream.XMLStreamException;\n");
        result.append("\n");
        result.append("public class ");
        result.append(serviceName);
        result.append(" {\n");
        result.append("\n");
        result.append("    private HttpWsClient httpWsClient;\n");
        result.append("\n");
        result.append("    public HttpWsClient getHttpWsClient() {\n");
        result.append("        return httpWsClient;\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    public void setHttpWsClient(HttpWsClient httpWsClient) {\n");
        result.append("        this.httpWsClient = httpWsClient;\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    // service related methods\n");
        result.append("\n");
        for (WsMethod method : wsDefinition.getMethods()) {
            result.append("    public ");
            result.append(method.getOutType());
            result.append(" ");
            result.append(method.getName());
            result.append("(\n");
            result.append("            WsRequest wsRequest, ");
            result.append(method.getInType());
            result.append(" parameters\n");
            result.append("    ) throws WsClientException {\n");
            result.append("        return httpWsClient.processSoapQuery(\n");
            result.append("                wsRequest, parameters, " + method.getOutType() + ".class\n");
            result.append("        );\n");
            result.append("    }\n\n");
        }
        result.append("}\n");
        return result;
    }

    private static StringBuilder generateServiceService(
            String packageName, Map<String, String> typesElements, String beanPackageName, String serviceName, String namespace, List<WsMethod> methods
    ) {
        StringBuilder result = new StringBuilder();
        result.append("package ");
        result.append(packageName);
        result.append(";\n");
        result.append("\n");
        result.append("import comtech.staxer.server.ServerServiceWs;\n");
        result.append("import comtech.staxer.server.WsMessage;\n");
        result.append("import ");
        result.append(beanPackageName);
        result.append(".*;\n");
        result.append("\n");
        result.append("import comtech.util.xml.XmlName;\n");
        result.append("import java.util.HashMap;\n");
        result.append("import java.util.Map;\n");
        result.append("\n");
        result.append("public abstract class ");
        result.append(serviceName);
        result.append(" implements ServerServiceWs {\n");
        result.append("\n");
        result.append("    public static final String NAMESPACE = \"");
        result.append(namespace);
        result.append("\";\n");
        result.append("\n");
        result.append("    public static final Map<XmlName, Class> CLASSES;\n");
        result.append("    public static final Map<XmlName, String> METHOD_NAMES;\n");
        result.append("\n");
        result.append("    static {\n");
        result.append("        CLASSES = new HashMap<XmlName, Class>();\n");
        result.append("        METHOD_NAMES = new HashMap<XmlName, String>();\n");
        result.append("\n");
        result.append("        XmlName xmlName;\n");
        result.append("\n");
        for (WsMethod method : methods) {
            String inXmlElement = typesElements.get(method.getInType());
            result.append("        xmlName = new XmlName(NAMESPACE, \"");
            result.append(inXmlElement);
            result.append("\");\n");
            result.append("        CLASSES.put(xmlName, ");
            result.append(method.getInType());
            result.append(".class);\n");
            result.append("        METHOD_NAMES.put(xmlName, \"");
            result.append(method.getName());
            result.append("\");\n");
            result.append("\n");
        }
        result.append("    }\n");
        result.append("\n");
        result.append("    public Class getClass(XmlName xmlName) {\n");
        result.append("        return CLASSES.get(xmlName);\n");
        result.append("    }\n");
        result.append("\n");
        result.append("    public String getMethodName(XmlName xmlName) {\n");
        result.append("        return METHOD_NAMES.get(xmlName);\n");
        result.append("    }\n");
        result.append("\n");
        for (WsMethod method : methods) {
            result.append("    public abstract ");
            result.append(method.getOutType());
            result.append(" ");
            result.append(method.getName());
            result.append("(\n");
            result.append("            WsMessage<");
            result.append(method.getInType());
            result.append("> wsMessage\n");
            result.append("    );\n");
            result.append("\n");
        }
        result.append("}\n");
        result.append("\n");
        return result;
    }

    private static StringBuilder generatePackageInfo(String packageName, Map<String, String> prefixXmlnsMap) {
        StringBuilder result = new StringBuilder();

        result.append("@XmlSchema(\n");
        result.append("        namespace = \"" + prefixXmlnsMap.get(null) + "\"\n");
        if (prefixXmlnsMap.size() > 1) {
            result.append("        xmlns = {\n");
            for (Map.Entry<String, String> prefixXmlnsEntry : prefixXmlnsMap.entrySet()) {
                if (prefixXmlnsEntry.getKey() != null) {
                    result.append("                @XmlNs(prefix = \"" + prefixXmlnsEntry.getKey() + "\", namespaceURI = \"" + prefixXmlnsEntry.getValue() + "\")\n");
                }
            }
            result.append("        },\n");
        }
        result.append("        elementFormDefault = XmlNsForm.QUALIFIED,\n");
        result.append("        attributeFormDefault = XmlNsForm.UNQUALIFIED\n");
        result.append(") package " + packageName + ";\n\n");
        result.append("import javax.xml.bind.annotation.XmlNs;\n");
        result.append("import javax.xml.bind.annotation.XmlNsForm;\n");
        result.append("import javax.xml.bind.annotation.XmlSchema;\n");

        return result;
    }

    public static String getJavaTypeName(String typeName) {
        if (typeName != null) {
            if ("decimal".equals(typeName)) {
                return "BigDecimal";
            } else if ("unsignedShort".equals(typeName)) {
                return "Character";
            } else if ("int".equals(typeName)) {
                return "Integer";
            } else if ("integer".equals(typeName)) {
                return "Integer";
            } else if ("float".equals(typeName)) {
                return "Float";
            } else if ("double".equals(typeName)) {
                return "Double";
            } else if ("boolean".equals(typeName)) {
                return "Boolean";
            } else if ("base64Binary".equals(typeName)) {
                return "byte[]";
            } else if ("dateTime".equals(typeName)) {
                return "XMLGregorianCalendar";
            } else {
                return capitalize3(typeName);
            }
        } else {
            return null;
        }
    }

    public static List<WsType> createTypes(ComplexType complexType) {
        List<WsType> result = new ArrayList<WsType>();
        WsType type = new WsType();
        result.add(type);
        type.setName(complexType.getName());
        List<Attribute> attributes = null;
        Sequence sequence = null;
        if (complexType.getSequence() != null) {
            sequence = complexType.getSequence();
            attributes = complexType.getAttributes();
        } else if (complexType.getSimpleContent() != null) {
            ExtensionSimple extension = complexType.getSimpleContent().getExtension();
            WsField field = new WsField();
            field.setName("value");
            field.setTypeName(cleanPrefix(extension.getValueType()));
            field.setValue(true);
            type.getFields().add(field);
            attributes = extension.getAttributes();
        } else if (complexType.getComplexContent() != null) {
            ExtensionComplex extension = complexType.getComplexContent().getExtension();
            type.setSuperTypeName(capitalize3(cleanPrefix(extension.getBaseType())));
            sequence = extension.getSequence();
            attributes = extension.getAttributes();
        }
        if (attributes != null) {
            for (Attribute attribute : attributes) {
                WsField field = new WsField();
                field.setName(attribute.getName());
                field.setTypeName(cleanPrefix(attribute.getType()));
                type.getFields().add(field);
            }
        }
        if (sequence != null) {
            for (Element element : sequence.getElements()) {
                if (element.getComplexType() != null) {
                    List<WsType> innerTypes = createTypes(element.getComplexType());
                    WsType innerType = innerTypes.get(0);
                    innerType.setXmlElementName(element.getName());
                    if (innerType.getName() == null) {
                        innerType.setName(capitalize3(type.getName()) + capitalize3(element.getName()));
                    }
                    if (element.getType() == null) {
                        element.setType(innerType.getName());
                    }
                    result.addAll(innerTypes);
                }
                if (element.getSimpleType() != null) {
                    WsType enumType = createEnum(element.getSimpleType());
                    enumType.setXmlElementName(element.getName());
                    result.add(enumType);
                }
                WsField field = new WsField();
                field.setName(element.getName());
                field.setTypeName(cleanPrefix(element.getType()));
                field.setNillable(element.getNillable());
                field.setRequired(element.getMinOccurs() > 0);
                if (!"1".equals(element.getMaxOccurs())) {
                    field.setList(true);
                }
                field.setXmlElement(true);
                type.getFields().add(field);
            }
        }
        return result;
    }

    /**
     * Create types for complex wsdl
     */
    public static List<WsType> createTypes2(
            ComplexType complexType, Schema schema, Map<String, Map<String, WsType>> xmlnsTypesMap, String elementName
    ) {
        List<WsType> result = new ArrayList<WsType>();

        WsType type = new WsType();
        type.setName(complexType.getName() != null ? complexType.getName() : elementName);
        result.add(type);

        String prefix = getPrefix(complexType.getName() != null ? getPrefix(complexType.getName()) : null);
        type.setPrefix(prefix);
        type.setNsURI(schema.getPrefixXmlnsMap().get(prefix));

        List<Attribute> attributes = null;
        Sequence sequence = null;

        if (complexType.getSequence() != null) {
            sequence = complexType.getSequence();
            attributes = complexType.getAttributes();
        } else if (complexType.getSimpleContent() != null) {
            ExtensionSimple extension = complexType.getSimpleContent().getExtension();
            WsField field = new WsField();
            field.setName("value");
            field.setPrefix(getPrefix(extension.getValueType()));
            field.setNsURI(schema.getPrefixXmlnsMap().get(getPrefix(extension.getValueType())));
            field.setTypeName(cleanPrefix(extension.getValueType()));
            field.setValue(true);
            type.getFields().add(field);
            attributes = extension.getAttributes();
        } else if (complexType.getComplexContent() != null) {
            ExtensionComplex extension = complexType.getComplexContent().getExtension();
            type.setSuperTypeName(capitalize3(cleanPrefix(extension.getBaseType())));
            sequence = extension.getSequence();
            attributes = extension.getAttributes();
        }
        if (attributes != null) {
            for (Attribute attribute : attributes) {
                WsField field = new WsField();
                field.setName(attribute.getName());
                field.setPrefix(getPrefix(attribute.getType()));
                field.setNsURI(schema.getPrefixXmlnsMap().get(getPrefix(attribute.getType())));
                field.setTypeName(cleanPrefix(attribute.getType()));
                type.getFields().add(field);
            }
        }
        if (sequence != null) {
            for (Element element : sequence.getElements()) {
                if (element.getComplexType() != null) {
                    List<WsType> innerTypes = createTypes2(element.getComplexType(), schema, xmlnsTypesMap, element.getName());
                    WsType innerType = innerTypes.get(0);
                    innerType.setXmlElementName(
                            element.getName() != null ? element.getName() : cleanPrefix(element.getRef())
                    );
                    if (innerType.getName() == null) {
                        innerType.setName(
                                capitalize3(type.getName()) + capitalize3(
                                        element.getName() != null ? element.getName() : cleanPrefix(element.getRef())
                                )
                        );
                    }
                    if (element.getType() == null) {
                        element.setType(innerType.getName());
                    }
                    String innerTypePrefix = getPrefix(element.getType()) != null ? getPrefix(element.getType()) : getPrefix(element.getRef());
                    innerType.setPrefix(innerTypePrefix);
                    innerType.setNsURI(schema.getPrefixXmlnsMap().get(innerTypePrefix));
                    result.addAll(innerTypes);
                }
                if (element.getSimpleType() != null) {
                    if ((element.getSimpleType().getRestriction() != null && element.getSimpleType().getRestriction().getEnumerations().size() > 0
                         || element.getSimpleType().getUnion() != null)
                            ) {
                        WsType enumType = createEnum2(element.getSimpleType(), schema.getPrefixXmlnsMap(), xmlnsTypesMap);
                        enumType.setXmlElementName(element.getName() != null ? element.getName() : cleanPrefix(element.getRef()));
                        result.add(enumType);
                    } else if (element.getSimpleType().getRestriction() != null) {
                        WsType wsType = new WsType();
                        wsType.setPrefix(getPrefix(element.getSimpleType().getName()));
                        wsType.setNsURI(schema.getPrefixXmlnsMap().get(getPrefix(element.getSimpleType().getName())));
                        wsType.setName(cleanPrefix(element.getSimpleType().getName()));
                        wsType.setEnumType(false);
                        wsType.setJavaTypeName(cleanPrefix(element.getSimpleType().getRestriction().getBase()));
                        result.add(wsType);
                    }
                }
                WsField field = new WsField();
                field.setName(element.getName() != null ? element.getName() : cleanPrefix(element.getRef()));
                field.setPrefix(getPrefix(element.getType()));
                field.setNsURI(schema.getPrefixXmlnsMap().get(getPrefix(element.getType())));
                if (element.getSimpleType() != null && element.getSimpleType().getRestriction().getEnumerations().size() == 0) {
                    field.setTypeName(cleanPrefix(element.getSimpleType().getRestriction().getBase()));
                } else {
                    field.setTypeName(cleanPrefix(element.getType()));
                }
                field.setNillable(element.getNillable());
                field.setRequired(element.getMinOccurs() > 0);
                if (!"1".equals(element.getMaxOccurs())) {
                    field.setList(true);
                }
                field.setXmlElement(true);
                type.getFields().add(field);
            }
        }
        return result;
    }

    public static WsType createEnum(SimpleType simpleType) {
        if (simpleType.getRestriction() == null || simpleType.getRestriction().getEnumerations().size() == 0) {
            return null;
        }
        WsType type = new WsType();
        type.setName(simpleType.getName());
        type.setEnumType(true);
        List<WsEnumValue> enumValues = new ArrayList<WsEnumValue>();
        for (Enumeration enumeration : simpleType.getRestriction().getEnumerations()) {
            WsEnumValue enumValue = new WsEnumValue();
            String value = enumeration.getValue();
            enumValue.setValue(value);
            char first = value.charAt(0);
            if (first >= '0' && first <= '9') {
                enumValue.setJavaName(toEnumName("value_" + enumeration.getValue()));
            } else {
                enumValue.setJavaName(toEnumName(enumeration.getValue()));
            }
            enumValues.add(enumValue);
        }
        type.getEnumValues().addAll(enumValues);
        return type;
    }

    /**
     * Create enum for complex wsdl
     */
    public static WsType createEnum2(
            SimpleType simpleType, Map<String, String> prefixXmlnsMap, Map<String, Map<String, WsType>> xmlnsTypesMap
    ) {
        if (simpleType.getRestriction() != null && simpleType.getRestriction().getEnumerations().size() > 0) {
            WsType type = new WsType();
            type.setName(simpleType.getName());
            type.setEnumType(true);
            List<WsEnumValue> enumValues = new ArrayList<WsEnumValue>();
            for (Enumeration enumeration : simpleType.getRestriction().getEnumerations()) {
                WsEnumValue enumValue = new WsEnumValue();
                String value = enumeration.getValue();
                enumValue.setValue(value);
                char first = value.charAt(0);
                if (first >= '0' && first <= '9') {
                    enumValue.setJavaName(toEnumName("value_" + enumeration.getValue()));
                } else {
                    enumValue.setJavaName(toEnumName(enumeration.getValue()));
                }
                enumValues.add(enumValue);
            }
            type.getEnumValues().addAll(enumValues);
            return type;
        } else if (simpleType.getUnion() != null) {
            WsType type = new WsType();
            type.setName(simpleType.getName());
            type.setEnumType(true);
            List<WsEnumValue> enumValues = new ArrayList<WsEnumValue>();

            String[] memberTypes = simpleType.getUnion().getMemberTypes().split(" ");

            // TODO these enums may not exist yet, fix
            try {
                for (String anEnumName : memberTypes) {
                    Map<String, WsType> typesMap = xmlnsTypesMap.get(prefixXmlnsMap.get(getPrefix(anEnumName)));
                    enumValues.addAll(typesMap.get(capitalize(cleanPrefix(anEnumName))).getEnumValues());
                }
            } catch (Exception e) {
                // do nothing
            }

            type.getEnumValues().addAll(enumValues);
            return type;
        } else {
            return null;
        }
    }

    public static String cleanPrefix(String value) {
        if (value == null) {
            return null;
        }
        String[] values = value.split(":");
        if (values.length > 1) {
            return values[1];
        } else {
            return value;
        }
    }

    public static String getPrefix(String value) {
        if (value == null || value.split(":").length <= 1) {
            return null;
        } else {
            return value.split(":")[0];
        }
    }

    public static void serializeAsWsdl(
            Reader definitionReader, Writer out, String serviceUrl
    ) throws JAXBException, IOException, XMLStreamException {
        WsDefinition definition = loadDefinition(definitionReader);
        DocumentXmlStreamWriter document = new DocumentXmlStreamWriter(out);
        document.startDocument();
        document.startElement(WSDL_PREFIX, "definitions", WSDL_NAMESPACE);
        document.namespace(WSDL_PREFIX, WSDL_NAMESPACE);
        document.namespace(SOAP_PREFIX, SOAP_NAMESPACE);
        document.namespace(XSD_PREFIX, XSD_NAMESPACE);
        document.namespace("tns", definition.getTargetNamespace());
        document.attribute("name", definition.getName());
        document.attribute("targetNamespace", definition.getTargetNamespace());
        // types
        document.startElement(WSDL_PREFIX, "types", WSDL_NAMESPACE);
        document.startElement(XSD_PREFIX, "schema", XSD_NAMESPACE);
        document.attribute("targetNamespace", definition.getTargetNamespace());
        document.attribute("elementFormDefault", StringUtils.notEmptyElseNull(definition.getElementFormDefault()));
        document.attribute("attributeFormDefault", StringUtils.notEmptyElseNull(definition.getAttributeFormDefault()));

        Map<String, WsType> typesMap = new LinkedHashMap<String, WsType>();
        List<WsField> elementFields = new LinkedList<WsField>();
        List<WsField> attributeFields = new LinkedList<WsField>();
        WsField valueField = null;
        Set<String> writtenElements = new HashSet<String>();
        Set<String> writtenTypes = new HashSet<String>();
        for (WsType wsType : definition.getTypes()) {
            String wsTypeName = wsType.getName();
            if (writtenTypes.contains(wsTypeName)) {
                continue;
            }
            writtenTypes.add(wsTypeName);
            typesMap.put(wsType.getJavaTypeName(), wsType);
            if (wsType.isEnumType()) {
                document.startElement(XSD_PREFIX, "simpleType", XSD_NAMESPACE);
                document.attribute("name", wsTypeName);
                document.startElement(XSD_PREFIX, "restriction", XSD_NAMESPACE);
                document.attribute("base", XSD_PREFIX + ":string");
                for (WsEnumValue wsEnumValue : wsType.getEnumValues()) {
                    document.emptyElement(XSD_PREFIX, "enumeration", XSD_NAMESPACE);
                    document.attribute("value", wsEnumValue.getValue());
                }
                // restriction
                document.endElement();
                // simpleType
                document.endElement();
            } else {
                document.startElement(XSD_PREFIX, "complexType", XSD_NAMESPACE);
                document.attribute("name", wsTypeName);
                for (WsField wsField : wsType.getFields()) {
                    if (wsField.isValue()) {
                        valueField = wsField;
                    } else if (wsField.isXmlElement()) {
                        elementFields.add(wsField);
                    } else {
                        attributeFields.add(wsField);
                    }
                }
                if (elementFields.size() > 0) {
                    document.startElement(XSD_PREFIX, "sequence", XSD_NAMESPACE);
                    for (WsField wsField : elementFields) {
                        document.emptyElement(XSD_PREFIX, "element", XSD_NAMESPACE);
                        if (wsField.isRequired()) {
                            document.attribute("minOccurs", "1");
                        } else {
                            document.attribute("minOccurs", "0");
                        }
                        if (wsField.isList()) {
                            document.attribute("maxOccurs", "unbounded");
                        }
                        document.attribute("name", wsField.getName());
                        if (XSD_TYPES.contains(wsField.getTypeName())) {
                            document.attribute("type", XSD_PREFIX + ":" + wsField.getTypeName());
                        } else {
                            document.attribute("type", "tns:" + wsField.getTypeName());
                        }
                        if (wsField.isNillable()) {
                            document.attribute("nillable", "true");
                        }
                    }
                    // sequence
                    document.endElement();
                } else {
                    document.emptyElement(XSD_PREFIX, "sequence", XSD_NAMESPACE);
                }
                if (valueField != null && XSD_TYPES.contains(valueField.getTypeName())) {
                    document.startElement(XSD_PREFIX, "simpleContent", XSD_NAMESPACE);
                    document.startElement(XSD_PREFIX, "extension", XSD_NAMESPACE);
                    document.attribute("base", XSD_PREFIX + ":" + valueField.getTypeName());
                }
                if (attributeFields.size() > 0) {
                    for (WsField wsField : attributeFields) {
                        document.emptyElement(XSD_PREFIX, "attribute", XSD_NAMESPACE);
                        document.attribute("name", wsField.getName());
                        if (XSD_TYPES.contains(wsField.getTypeName())) {
                            document.attribute("type", XSD_PREFIX + ":" + wsField.getTypeName());
                        } else {
                            document.attribute("type", "tns:" + wsField.getTypeName());
                        }
                    }
                }
                if (valueField != null && XSD_TYPES.contains(valueField.getTypeName())) {
                    // extension
                    document.endElement();
                    // simpleContent
                    document.endElement();
                }
                // complexType
                document.endElement();

                elementFields.clear();
                attributeFields.clear();
                valueField = null;
            }
            String xmlElementName = wsType.getXmlElementName();
            if (!StringUtils.isEmpty(xmlElementName) && !writtenElements.contains(xmlElementName)) {
                writtenElements.add(xmlElementName);
                document.emptyElement(XSD_PREFIX, "element", XSD_NAMESPACE);
                document.attribute("name", xmlElementName);
                if (XSD_TYPES.contains(wsTypeName)) {
                    document.attribute("type", XSD_PREFIX + ":" + wsTypeName);
                } else {
                    document.attribute("type", "tns:" + wsTypeName);
                }
            }
        }

        // schema
        document.endElement();
        // types
        document.endElement();

        List<WsType[]> wsOperations = new LinkedList<WsType[]>();
/*
        for (Map.Entry<String, WsType> entry : typesMap.entrySet()) {
            if (entry.getKey().endsWith("Response")) {
                String methodName = StringUtils.substring(entry.getKey(), 0, -8);
                if (!StringUtils.isEmpty(methodName)) {
                    WsType inType = typesMap.get(methodName + "Request");
                    if (inType != null) {
                        wsOperations.add(new WsType[]{inType, entry.getValue()});
                    }
                }
            }
        }
*/

        Map<String, String> methodInNames = new HashMap<String, String>();
        for (WsMethod wsMethod : definition.getMethods()) {
            methodInNames.put(wsMethod.getInType(), wsMethod.getInName());
            WsType inWsType = typesMap.get(wsMethod.getInType());
            WsType outWsType = typesMap.get(wsMethod.getOutType());
            if (inWsType != null && outWsType != null) {
                wsOperations.add(new WsType[]{inWsType, outWsType});
            }
        }

        for (WsType[] wsOperation : wsOperations) {
            for (WsType wsType : wsOperation) {
                document.startElement(WSDL_PREFIX, "message", WSDL_NAMESPACE);
                String xmlElementName = wsType.getXmlElementName();
                document.attribute("name", xmlElementName);
                document.emptyElement(WSDL_PREFIX, "part", WSDL_NAMESPACE);
                document.attribute("element", "tns:" + xmlElementName);
                String inName = methodInNames.get(wsType.getJavaTypeName());
                if (StringUtils.isEmpty(inName)) {
                    inName = xmlElementName;
                }
                document.attribute("name", inName);
                // message
                document.endElement();
            }
        }

        document.startElement(WSDL_PREFIX, "portType", WSDL_NAMESPACE);
        document.attribute("name", definition.getName());
        for (WsType[] wsOperation : wsOperations) {
            WsType inType = wsOperation[0];
            WsType outType = wsOperation[1];
            document.startElement(WSDL_PREFIX, "operation", WSDL_NAMESPACE);
            document.attribute("name", inType.getXmlElementName());
            document.emptyElement(WSDL_PREFIX, "input", WSDL_NAMESPACE);
            document.attribute("message", "tns:" + inType.getXmlElementName());
            document.attribute("name", inType.getXmlElementName());
            document.emptyElement(WSDL_PREFIX, "output", WSDL_NAMESPACE);
            document.attribute("message", "tns:" + outType.getXmlElementName());
            document.attribute("name", outType.getXmlElementName());
            // message
            document.endElement();
        }
        // portType
        document.endElement();

        document.startElement(WSDL_PREFIX, "binding", WSDL_NAMESPACE);
        document.attribute("name", definition.getName());
        document.attribute("type", "tns:" + definition.getName());
        document.emptyElement(SOAP_PREFIX, "binding", SOAP_NAMESPACE);
        document.attribute("style", "document");
        document.attribute("transport", "http://schemas.xmlsoap.org/soap/http");
        for (WsType[] wsOperation : wsOperations) {
            WsType inType = wsOperation[0];
            WsType outType = wsOperation[1];
            document.startElement(WSDL_PREFIX, "operation", WSDL_NAMESPACE);
            document.attribute("name", inType.getXmlElementName());
            document.emptyElement(SOAP_PREFIX, "operation", SOAP_NAMESPACE);
            document.attribute("soapAction", inType.getXmlElementName());
            document.attribute("style", "document");
            document.startElement(WSDL_PREFIX, "input", WSDL_NAMESPACE);
            document.attribute("name", inType.getXmlElementName());
            document.emptyElement(SOAP_PREFIX, "body", SOAP_NAMESPACE);
            document.attribute("use", "literal");
            // input
            document.endElement();
            document.startElement(WSDL_PREFIX, "output", WSDL_NAMESPACE);
            document.attribute("name", outType.getXmlElementName());
            document.emptyElement(SOAP_PREFIX, "body", SOAP_NAMESPACE);
            document.attribute("use", "literal");
            // output
            document.endElement();
            // message
            document.endElement();
        }
        // binding
        document.endElement();

        document.startElement(WSDL_PREFIX, "service", WSDL_NAMESPACE);
        document.attribute("name", definition.getName());
        document.startElement(WSDL_PREFIX, "port", WSDL_NAMESPACE);
        document.attribute("binding", "tns:" + definition.getName());
        document.attribute("name", definition.getName());
        document.emptyElement(SOAP_PREFIX, "address", SOAP_NAMESPACE);
        document.attribute("location", serviceUrl);
        // port
        document.endElement();
        // service
        document.endElement();

        // definitions
        document.endElement();
        document.endDocument();
    }

    private static String getParentDirPathname(String pathname) {
        if (pathname != null) {
            String[] pathNames = pathname.split("/");
            return pathname.substring(0, pathname.length() - pathNames[pathNames.length - 1].length());
        } else {
            return null;
        }
    }

    /**
     * Get the absolute path to the file using a relative path relative to another file
     */
    private static String getFilePathname(String referenceFilePath, String relatedPath) {
        if (referenceFilePath != null && relatedPath != null) {

            String parentDirPathname = getParentDirPathname(referenceFilePath);

            if (!relatedPath.contains("..")) {
                return parentDirPathname + relatedPath;
            } else {
                int shift = 0;
                StringBuilder sb = new StringBuilder();
                String[] filePathNames = relatedPath.split("/");
                for (String filePathName : filePathNames) {
                    if (filePathName.contains("..")) {
                        shift++;
                    } else {
                        sb.append("/").append(filePathName);
                    }
                }
                StringBuilder result = new StringBuilder();
                String[] parentDirPathNames = parentDirPathname.split("/");
                for (int i = 1; i < parentDirPathNames.length - shift; i++) {
                    result.append("/").append(parentDirPathNames[i]);
                }
                return result.append(sb).toString();
            }
        } else {
            return null;
        }
    }

    /**
     * Deserialize schema and handle all namespace used in schema as Map<\String prefix, String namespace> field
     */
    private static Schema importSchema(String schemaLocation) throws JAXBException, FileNotFoundException, XMLStreamException {
        if (schemaLocation != null) {
            JAXBContext context = JAXBContext.newInstance(Schema.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            FileReader reader = new FileReader(schemaLocation);
            Schema result = (Schema) unmarshaller.unmarshal(reader);
            setSchemaNamespaces2(result, new FileReader(schemaLocation));
            return result;
        } else {
            return null;
        }
    }

}
