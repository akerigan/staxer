package comtech.util.xml.write;

import comtech.util.StringUtils;
import comtech.util.xml.XmlConstants;
import comtech.util.xml.XmlName;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 04.09.2009
 * Time: 13:07:24
 */
public class DocumentXmlStreamWriter {

    private static final XMLOutputFactory factory = XMLOutputFactory.newInstance();
    private XMLStreamWriter writer;

    private Map<String, String> namespacesPrefixes =
            new HashMap<String, String>(XmlConstants.DEFAULT_NAMESPACES_PREFIXES);

    private int namespacePrefixIdx = 1;

    public DocumentXmlStreamWriter(OutputStream stream) throws XMLStreamException {
        this(stream, false);
    }

    public DocumentXmlStreamWriter(OutputStream stream, boolean pretty) throws XMLStreamException {
        this.writer = factory.createXMLStreamWriter(stream, "utf-8");
        if (pretty) {
            this.writer = new IndentingXMLStreamWriter(this.writer);
        }
    }

    public DocumentXmlStreamWriter(Writer writer) throws XMLStreamException {
        this(writer, false);
    }

    public DocumentXmlStreamWriter(Writer writer, boolean pretty) throws XMLStreamException {
        this.writer = factory.createXMLStreamWriter(writer);
        if (pretty) {
            this.writer = new IndentingXMLStreamWriter(this.writer);
        }
    }

    private String getPrefix(String namespaceUri) {
        if (namespaceUri != null) {
            String result = namespacesPrefixes.get(namespaceUri);
            if (result == null) {
                result = "ns" + namespacePrefixIdx;
                namespacePrefixIdx += 1;
                namespacesPrefixes.put(namespaceUri, result);
            }
            return result;
        } else {
            return null;
        }
    }

    public void startDocument() throws XMLStreamException {
        writer.writeStartDocument();
    }

    public void startDocument(String encoding, String version) throws XMLStreamException {
        writer.writeStartDocument(encoding, version);
    }

    public void endDocument() throws XMLStreamException {
        writer.writeEndDocument();
        writer.flush();
        writer.close();
    }

    public void startElement(XmlName elementName) throws XMLStreamException {
        String namespaceURI = elementName.getNamespaceURI();
        if (!StringUtils.isEmpty(namespaceURI)) {
            writer.writeStartElement(getPrefix(namespaceURI), elementName.getLocalPart(), namespaceURI);
        } else {
            writer.writeStartElement(elementName.getLocalPart());
        }
    }

    public void startElement(String localName) throws XMLStreamException {
        writer.writeStartElement(localName);
    }

    public void startElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        writer.writeStartElement(prefix, localName, namespaceURI);
    }

    public void namespace(String prefix, String namespaceURI) throws XMLStreamException {
        writer.setPrefix(prefix, namespaceURI);
        writer.writeNamespace(prefix, namespaceURI);
    }

    public void endElement() throws XMLStreamException {
        writer.writeEndElement();
    }

    public void attribute(String name, Object value) throws XMLStreamException {
        attribute(null, name, value);
    }

    public void attribute(XmlName attributeName, Object value) throws XMLStreamException {
        attribute(attributeName.getNamespaceURI(), attributeName.getLocalPart(), value);
    }

    public void attribute(String namespaceUri, String name, Object value) throws XMLStreamException {
        value = StringUtils.notEmptyElseNull(StringUtils.toString(value));
        if (value != null) {
            String sValue = value.toString();
            if (!StringUtils.isEmpty(sValue)) {
                if (!StringUtils.isEmpty(namespaceUri)) {
                    writer.writeAttribute(getPrefix(namespaceUri), namespaceUri, name, sValue);
                } else {
                    writer.writeAttribute(name, sValue);
                }
            }
        }
    }

    public void element(String localName, Object value) throws XMLStreamException {
        element(null, localName, value);
    }

    public void element(XmlName elementName, Object value) throws XMLStreamException {
        element(elementName.getNamespaceURI(), elementName.getLocalPart(), value);
    }

    public void element(String namespaceUri, String localName, Object value) throws XMLStreamException {
        value = StringUtils.notEmptyElseNull(StringUtils.toString(value));
        if (value != null) {
            String sValue = value.toString();
            if (!StringUtils.isEmpty(sValue)) {
                if (!StringUtils.isEmpty(namespaceUri)) {
                    writer.writeStartElement(getPrefix(namespaceUri), localName, namespaceUri);
                } else {
                    writer.writeStartElement(localName);
                }
                writer.writeCharacters(sValue);
                writer.writeEndElement();
            }
        }
    }

    public void element(String prefix, String localName, String namespaceURI, Object value) throws XMLStreamException {
        value = StringUtils.notEmptyElseNull(StringUtils.toString(value));
        if (value != null) {
            String sValue = value.toString();
            if (!StringUtils.isEmpty(sValue)) {
                writer.writeStartElement(prefix, localName, namespaceURI);
                writer.writeCharacters(sValue);
                writer.writeEndElement();
            }
        }
    }

    public void elementAndAttribute(
            String elementName, Object elementValue,
            String attributeName, Object attributeValue
    ) throws XMLStreamException {
        elementValue = StringUtils.notEmptyElseNull(StringUtils.toString(elementValue));
        if (elementValue != null) {
            writer.writeStartElement(elementName);
        } else {
            writer.writeEmptyElement(elementName);
        }
        if (attributeValue != null) {
            String sValue = attributeValue.toString();
            if (!StringUtils.isEmpty(sValue)) {
                writer.writeAttribute(attributeName, sValue);
            }
        }
        if (elementValue != null) {
            String sValue = elementValue.toString();
            if (!StringUtils.isEmpty(sValue)) {
                writer.writeCharacters(sValue);
                writer.writeEndElement();
            }
        }
    }

    public void xsiNillElement(String elementName) throws XMLStreamException {
        writer.writeEmptyElement(elementName);
        writer.writeAttribute(XmlConstants.NAMESPACE_PREFIX_XSI, XmlConstants.NAMESPACE_URI_XSI, "nil", "true");
    }

    public void emptyElement(String name) throws XMLStreamException {
        writer.writeEmptyElement(name);
    }

    public void emptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        writer.writeEmptyElement(prefix, localName, namespaceURI);
    }

    public void element(
            String name, Map<String, String> attributes, Object value
    ) throws XMLStreamException {
        value = StringUtils.notEmptyElseNull(StringUtils.toString(value));
        if (value != null) {
            writer.writeStartElement(name);
        } else {
            writer.writeEmptyElement(name);
        }
        if (attributes != null) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                writer.writeAttribute(entry.getKey(), entry.getValue());
            }
        }
        if (value != null) {
            String sValue = value.toString();
            if (!StringUtils.isEmpty(sValue)) {
                writer.writeCharacters(sValue);
                writer.writeEndElement();
            }
        }
    }

    public void element(String name, Map<String, String> attributes) throws XMLStreamException {
        element(name, attributes, null);
    }

    public void text(Object value) throws XMLStreamException {
        value = StringUtils.notEmptyElseNull(StringUtils.toString(value));
        if (value != null) {
            String sValue = value.toString();
            if (!StringUtils.isEmpty(sValue)) {
                writer.writeCharacters(sValue);
            }
        }
    }

    public void object(Object object) throws XMLStreamException {
        object(object, false, null);
    }

    public void object(Object object, Class[] classes) throws XMLStreamException {
        object(object, false, classes);
    }

    public void object(Object object, boolean skipRootElement, Class[] classes) throws XMLStreamException {
        if (object != null) {
            ExistingDocumentXmlStreamWriter wrappedWriter = new ExistingDocumentXmlStreamWriter(writer, skipRootElement);
            try {
                JAXBContext jaxbContext;
                if (classes != null) {
                    Class[] allClasses = new Class[classes.length + 1];
                    allClasses[0] = object.getClass();
                    System.arraycopy(classes, 0, allClasses, 1, classes.length);
                    jaxbContext = JAXBContext.newInstance(allClasses);
                } else {
                    jaxbContext = JAXBContext.newInstance(object.getClass());
                }
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.marshal(object, wrappedWriter);
            } catch (JAXBException e) {
                throw new XMLStreamException(e);
            }
        }
    }

    public void processingInstruction(String target) throws XMLStreamException {
        writer.writeProcessingInstruction(target);
    }
}
