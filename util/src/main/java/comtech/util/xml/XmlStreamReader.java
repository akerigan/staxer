package comtech.util.xml;

import comtech.util.StringUtils;
import comtech.util.props.StringMapProperties;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 04.09.2009
 * Time: 13:10:01
 */
public class XmlStreamReader {

    private static final XMLInputFactory factory = XMLInputFactory.newInstance();
    private XMLStreamReader reader;
    private Stack<XmlName> startedElements = new Stack<XmlName>();
    private XmlName lastStartedElement;
    private XmlName endedElement;
    private int event;

    public XmlStreamReader(InputStream stream) throws XMLStreamException {
        this.reader = factory.createXMLStreamReader(stream, "utf-8");
    }

    public XmlStreamReader(Reader reader) throws XMLStreamException {
        this.reader = factory.createXMLStreamReader(reader);
    }

    public XmlStreamReader(XMLStreamReader xmlStreamReader) {
        this.reader = xmlStreamReader;
    }

    public XMLStreamReader getReader() {
        return reader;
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

    public XmlName readStartElement() throws XMLStreamException {
        while (reader.hasNext()) {
            updateState();
            if (event == XMLStreamConstants.START_ELEMENT) {
                return lastStartedElement;
            }
        }
        return null;
    }

    private void updateState() throws XMLStreamException {
        event = reader.next();
        if (event == XMLStreamConstants.START_ELEMENT) {
            if (lastStartedElement != null) {
                startedElements.push(lastStartedElement);
            }
            lastStartedElement = new XmlName(reader.getNamespaceURI(), reader.getLocalName());
        } else if (event == XMLStreamConstants.END_ELEMENT) {
            endedElement = lastStartedElement;
            if (!startedElements.isEmpty()) {
                lastStartedElement = startedElements.pop();
            } else {
                lastStartedElement = null;
            }
        }
    }

    public boolean readStartElement(XmlName elementName) throws XMLStreamException {
        XmlName qName;
        do {
            qName = readStartElement();
        } while (qName != null && !qName.equals(elementName));
        return qName != null;
    }

    public StringMapProperties getAttributes() {
        int attributeCount = reader.getAttributeCount();
        StringMapProperties result = new StringMapProperties();
        if (attributeCount != 0) {
            for (int i = 0; i < attributeCount; i++) {
                result.put(reader.getAttributeName(i).toString(), reader.getAttributeValue(i));
            }
        }
        return result;
    }

    public XmlName readEndElement() throws XMLStreamException {
        while (reader.hasNext()) {
            updateState();
            if (event == XMLStreamConstants.END_ELEMENT) {
                return endedElement;
            }
        }
        return null;
    }

    public boolean readEndElement(XmlName elementName) throws XMLStreamException {
        XmlName qName;
        do {
            qName = readEndElement();
        } while (qName != null && !qName.equals(elementName));
        return qName != null;
    }

    public XmlName getLastStartedElement() {
        return lastStartedElement;
    }

    public XmlName getEndedElement() {
        return endedElement;
    }

    public boolean elementStarted() {
        return event == XMLStreamConstants.START_ELEMENT;
    }

    public boolean elementStarted(XmlName name) {
        return event == XMLStreamConstants.START_ELEMENT
                && name != null && name.equals(lastStartedElement);
    }

    public boolean elementEnded() {
        return event == XMLStreamConstants.END_ELEMENT;
    }

    public boolean elementEnded(XmlName name) {
        return event == XMLStreamConstants.END_ELEMENT &&
                name != null && name.equals(endedElement);
    }

    public boolean readNext() throws XMLStreamException {
        if (reader.hasNext()) {
            updateState();
            return true;
        } else {
            return false;
        }
    }

    public String readCharacters(XmlName endElement) throws XMLStreamException {
        StringBuilder result = new StringBuilder();
        while (readNext()) {
            if (elementEnded(endElement)) {
                break;
            } else if (event == XMLStreamConstants.CHARACTERS) {
                if (result.length() != 0) {
                    result.append(" ");
                }
                result.append(StringUtils.notEmptyTrimmedElseDefault(reader.getText(), ""));
            }
        }
        if (result.length() != 0) {
            return result.toString();
        } else {
            return null;
        }
    }
}

