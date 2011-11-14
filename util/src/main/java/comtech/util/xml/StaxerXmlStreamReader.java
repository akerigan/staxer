package comtech.util.xml;

import comtech.util.StringUtils;
import comtech.util.props.XmlNameMapProperties;

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
public class StaxerXmlStreamReader {

    private static final XMLInputFactory factory = XMLInputFactory.newInstance();
    private XMLStreamReader reader;
    private Stack<XmlName> startedElements = new Stack<XmlName>();
    private XmlName lastStartedElement;
    private XmlName endedElement;
    private int event;

    public StaxerXmlStreamReader(InputStream stream) throws StaxerXmlStreamException {
        try {
            this.reader = factory.createXMLStreamReader(stream, "utf-8");
        } catch (Exception e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public StaxerXmlStreamReader(Reader reader) throws StaxerXmlStreamException {
        try {
            this.reader = factory.createXMLStreamReader(reader);
        } catch (Exception e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public StaxerXmlStreamReader(XMLStreamReader xmlStreamReader) {
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

    public XmlName readStartElement() throws StaxerXmlStreamException {
        try {
            while (reader.hasNext()) {
                updateState();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    return lastStartedElement;
                }
            }
            return null;
        } catch (Exception e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    private void updateState() throws StaxerXmlStreamException {
        try {
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
        } catch (XMLStreamException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public boolean readStartElement(XmlName elementName) throws StaxerXmlStreamException {
        XmlName qName;
        do {
            qName = readStartElement();
        } while (qName != null && !qName.equals(elementName));
        return qName != null;
    }

    public XmlNameMapProperties getAttributes() {
        int attributeCount = reader.getAttributeCount();
        XmlNameMapProperties result = new XmlNameMapProperties();
        if (attributeCount != 0) {
            for (int i = 0; i < attributeCount; i++) {
                result.put(new XmlName(reader.getAttributeName(i)), reader.getAttributeValue(i));
            }
        }
        return result;
    }

    public XmlName readEndElement() throws StaxerXmlStreamException {
        try {
            while (reader.hasNext()) {
                updateState();
                if (event == XMLStreamConstants.END_ELEMENT) {
                    return endedElement;
                }
            }
            return null;
        } catch (XMLStreamException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public boolean readEndElement(XmlName elementName) throws StaxerXmlStreamException {
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

    public boolean readNext() throws StaxerXmlStreamException {
        try {
            if (reader.hasNext()) {
                updateState();
                return true;
            } else {
                return false;
            }
        } catch (XMLStreamException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public String readCharacters() throws StaxerXmlStreamException {
        return readCharacters(lastStartedElement);
    }

    public String readCharacters(XmlName endElement) throws StaxerXmlStreamException {
        StringBuilder result = new StringBuilder();
        while (readNext() && !elementEnded(endElement)) {
            if (event == XMLStreamConstants.CHARACTERS) {
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

    public boolean isCurrentElement(XmlName elementName) {
        return lastStartedElement != null && lastStartedElement.equals(elementName);
    }

}

