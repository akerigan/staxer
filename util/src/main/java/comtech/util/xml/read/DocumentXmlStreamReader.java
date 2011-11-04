package comtech.util.xml.read;

import comtech.util.xml.XmlName;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 04.09.2009
 * Time: 13:10:01
 */
public class DocumentXmlStreamReader {

    private static final XMLInputFactory factory = XMLInputFactory.newInstance();
    private XMLStreamReader reader;
    private Collection<QName> qNamesStack = new Stack<QName>();

    public DocumentXmlStreamReader(InputStream stream) throws XMLStreamException {
        this.reader = factory.createXMLStreamReader(stream, "utf-8");
    }

    public DocumentXmlStreamReader(Reader reader) throws XMLStreamException {
        this.reader = factory.createXMLStreamReader(reader);
    }

    public DocumentXmlStreamReader(XMLStreamReader xmlStreamReader) {
        this.reader = xmlStreamReader;
    }

    public StartElement readStartElement() throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                StartElement startElement = new StartElement();
                fillStartElement(startElement);
                return startElement;
            }
        }
        return null;
    }

    public StartElement getStartElement(String name, String nmspUri) throws XMLStreamException {
        if (name != null && nmspUri != null) {

            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT &&
                    name.equals(reader.getLocalName()) &&
                    nmspUri.equals(reader.getNamespaceURI())) {

                    StartElement result = new StartElement();
                    fillStartElement(result);
                    return result;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    public Map<String, String> getNamespacesMap(String name, String nmspUri) throws XMLStreamException {
        if (name != null && nmspUri != null) {
            Map<String, String> result = new LinkedHashMap<String, String>();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT &&
                    name.equals(reader.getLocalName()) &&
                    nmspUri.equals(reader.getNamespaceURI())) {
                    int nsCount = reader.getNamespaceCount();
                    for (int i = 0; i < nsCount; i++) {
                        result.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
                    }
                    return result;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    public boolean hasNext() throws XMLStreamException {
        return reader.hasNext();
    }

    public int next() throws XMLStreamException {
        return reader.next();
    }

    public String getText() {
        return reader.getText();
    }

    public QName getName() {
        return reader.getName();
    }

    public XmlName getXmlName() {
        return new XmlName(reader.getNamespaceURI(), reader.getLocalName());
    }

    public StartElement readStartElement(QName elementName) throws XMLStreamException {
        if (reader.getEventType() == XMLStreamConstants.START_ELEMENT && elementName.equals(reader.getName())) {
            return createStartElement();
        } else {
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    QName qName = reader.getName();
                    if (elementName.equals(qName)) {
                        return createStartElement();
                    }
                }
            }
            return null;
        }
    }

    private StartElement createStartElement() {
        StartElement startElement = new StartElement();
        fillStartElement(startElement);
        return startElement;
    }

    private void fillStartElement(StartElement startElement) {
        startElement.setName(reader.getName());

        Map<String, String> attributes = startElement.getAttributes();
        int count = reader.getAttributeCount();
        for (int i = 0; i < count; i++) {
            QName qName = reader.getAttributeName(i);
            attributes.put(qName.getLocalPart(), reader.getAttributeValue(i));
        }
    }

    public void fillElementAttributes(StartElement startElement) {
        Map<String, String> attributes = startElement.getAttributes();
        int count = reader.getAttributeCount();
        for (int i = 0; i < count; i++) {
            QName qName = reader.getAttributeName(i);
            attributes.put(qName.getLocalPart(), reader.getAttributeValue(i));
        }
    }

    public String readEndElement(boolean collectText) throws XMLStreamException {
        if (collectText) {
            StringBuilder builder = new StringBuilder();
            int level = 0;
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    ++level;
                } else if (event == XMLStreamConstants.CHARACTERS) {
                    if (builder.length() != 0) {
                        builder.append(" ");
                    }
                    builder.append(reader.getText().trim());
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    if (level != 0) {
                        --level;
                    } else {
                        break;
                    }
                }
            }
            return builder.toString();
        } else {
            int level = 0;
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    ++level;
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    if (level != 0) {
                        --level;
                    } else {
                        break;
                    }
                }
            }
            return null;
        }
    }

    public Element readElement() throws XMLStreamException {
        Element element = new Element();
        List<String> values = new ArrayList<String>();
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                fillStartElement(element);
            } else if (event == XMLStreamConstants.CHARACTERS) {
                values.add(reader.getText());
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                StringBuilder builder = new StringBuilder();
                for (String value : values) {
                    if (builder.length() != 0) {
                        builder.append(" ");
                    }
                    builder.append(value);
                }
                if (builder.length() != 0) {
                    element.setText(builder.toString());
                }
                return element;
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T readObject(Class<T> objectClass, boolean checkStartElement) throws JAXBException, XMLStreamException {
        if (checkStartElement) {
            int event = reader.next();
            if (event != XMLStreamConstants.START_ELEMENT) {
                throw new XMLStreamException("Cant deserializeBody object: current state is not START_ELEMENT");
            }
        }
        JAXBContext context = JAXBContext.newInstance(objectClass);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (T) unmarshaller.unmarshal(reader);
    }

    public void close() throws XMLStreamException {
        reader.close();
    }

    public Map<String, String> getAttributes() {
        int attrubuteCount = reader.getAttributeCount();
        if (attrubuteCount != 0) {
            Map<String, String> attributes = new LinkedHashMap<String, String>();
            for (int i = 0; i < attrubuteCount; i++) {
                attributes.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            }
            return attributes;
        }
        return null;
    }

    public Map<String, String> getNamespacesMap() {
        Map<String, String> result = null;
        for (int i = 0, len = reader.getNamespaceCount(); i < len; ++i) {
            if (result == null) {
                result = new HashMap<String, String>();
            }
            result.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
        }
        return result;
    }

    public void updateNamespacesMap(Map<String, String> namespacesMap) {
        if (namespacesMap != null) {
            for (int i = 0, len = reader.getNamespaceCount(); i < len; ++i) {
                namespacesMap.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
            }
        }
    }

    public boolean findStartElement(QName elementName) throws XMLStreamException {
        if (reader.getEventType() == XMLStreamConstants.START_ELEMENT && elementName.equals(reader.getName())) {
            return true;
        } else {
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    QName qName = reader.getName();
                    if (elementName.equals(qName)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }


}

