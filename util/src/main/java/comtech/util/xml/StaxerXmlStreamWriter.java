package comtech.util.xml;

import comtech.util.StringUtils;

import java.io.*;
import java.util.*;

import static comtech.util.xml.XmlConstants.XML_NAME_XSI_NIL;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-27 13:59 (Europe/Moscow)
 */
public class StaxerXmlStreamWriter {

    private static final String START_ELEMENT_END_NORMAL = ">";
    private static final String START_ELEMENT_END_EMPTY = "/>";

    private static final int STATE_DOCUMENT_START = 1;
    private static final int STATE_DOCUMENT_STARTED = 2;
    private static final int STATE_ELEMENT_START = 3;
    private static final int STATE_ELEMENT_STARTED = 4;
    private static final int STATE_TEXT_WRITED = 5;
    private static final int STATE_DOCUMENT_ENDED = 6;

    private Writer writer;
    private String charset;
    private int indentSize;

    private int level = 0;
    private int state = STATE_DOCUMENT_START;

    private Stack<XmlName> startedElements = new Stack<XmlName>();

    private Map<String, String> namespacesPrefixes =
            new HashMap<String, String>(XmlConstants.DEFAULT_NAMESPACES_PREFIXES);
    private Stack<String> namespacesNested = new Stack<String>();
    private String namespacesCurrent;

    private int namespacePrefixIdx = 1;

    private Set<String> namespacesToDeclare = new HashSet<String>();

    public StaxerXmlStreamWriter(
            OutputStream outputStream
    ) throws StaxerXmlStreamException {
        this(outputStream, "UTF-8", 0);
    }

    public StaxerXmlStreamWriter(
            OutputStream outputStream, String charset
    ) throws StaxerXmlStreamException {
        this(outputStream, charset, 0);
    }

    public StaxerXmlStreamWriter(
            OutputStream outputStream, String charset, int indentSize
    ) throws StaxerXmlStreamException {
        try {
            this.writer = new OutputStreamWriter(outputStream, charset);
            this.charset = charset;
            this.indentSize = indentSize;
        } catch (UnsupportedEncodingException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    private String getPrefix(String namespaceURI) {
        if (namespaceURI != null) {
            String result = namespacesPrefixes.get(namespaceURI);
            if (result == null) {
                result = "ns" + namespacePrefixIdx;
                namespacePrefixIdx += 1;
                namespacesPrefixes.put(namespaceURI, result);
                if (StringUtils.indexOf(namespacesCurrent, result) == -1) {
                    namespacesToDeclare.add(namespaceURI);
                }
            }
            return result;
        } else {
            return null;
        }
    }

    public void startDocument() throws StaxerXmlStreamException {
        try {
            writer.write("<?xml version=\"1.0\" encoding=\"");
            writer.write(charset);
            writer.write("\"?>");
            state = STATE_DOCUMENT_STARTED;
        } catch (IOException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public void declareNamespace(String namespaceURI) {
        String prefix = namespacesPrefixes.get(namespaceURI);
        if (StringUtils.indexOf(namespacesCurrent, prefix) == -1) {
            namespacesToDeclare.add(namespaceURI);
        }
    }

    public void startElement(XmlName elementName) throws StaxerXmlStreamException {
        try {
            if (state != STATE_DOCUMENT_ENDED) {
                String namespacePrefix = null;
                String namespaceURI = elementName.getNamespaceURI();
                if (namespaceURI != null) {
                    namespacePrefix = getPrefix(namespaceURI);
                    elementName.setPrefix(namespacePrefix);
                }
                startedElements.add(elementName);
                if (level > 0) {
                    namespacesNested.add(namespacesCurrent);
                }
                if (state == STATE_ELEMENT_START) {
                    writer.write(START_ELEMENT_END_NORMAL);
                    state = STATE_ELEMENT_STARTED;
                }
                writeIndents();
                writer.write('<');
                if (namespacePrefix != null) {
                    writer.write(namespacePrefix);
                    writer.write(':');
                }
                writer.write(elementName.getLocalPart());
                if (!namespacesToDeclare.isEmpty()) {
                    for (String namespaceToDeclare : namespacesToDeclare) {
                        namespacePrefix = getPrefix(namespaceToDeclare);
                        writer.write(" xmlns:");
                        writer.write(namespacePrefix);
                        writer.write("=\"");
                        writer.write(namespaceToDeclare);
                        writer.write('"');
                        namespacesCurrent = StringUtils.join("-", namespacesCurrent, namespaceToDeclare);
                    }
                    namespacesToDeclare.clear();
                }
                state = STATE_ELEMENT_START;
                level += 1;
            }
        } catch (IOException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    private void writeIndents() throws StaxerXmlStreamException {
        try {
            if (indentSize > 0
                && (state == STATE_ELEMENT_STARTED || state == STATE_DOCUMENT_STARTED)) {
                writer.write("\n");
                for (int i = 0; i < level; ++i) {
                    for (int j = 0; j < indentSize; ++j) {
                        writer.write(' ');
                    }
                }
            }
        } catch (IOException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public void endElement() throws StaxerXmlStreamException {
        try {
            if (!startedElements.isEmpty()) {
                XmlName elementName = startedElements.pop();
                level -= 1;
                if (state == STATE_ELEMENT_START) {
                    writer.write(START_ELEMENT_END_EMPTY);
                } else {
                    writeIndents();
                    writer.write("</");
                    String prefix = elementName.getPrefix();
                    if (prefix != null) {
                        writer.write(prefix);
                        writer.write(':');
                    }
                    writer.write(elementName.getLocalPart());
                    writer.write('>');
                }
                if (level > 0) {
                    namespacesCurrent = namespacesNested.pop();
                    state = STATE_ELEMENT_STARTED;
                } else {
                    state = STATE_DOCUMENT_ENDED;
                }
            }
        } catch (IOException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public void text(Object value) throws StaxerXmlStreamException {
        try {
            if (state == STATE_ELEMENT_START
                || state == STATE_ELEMENT_STARTED
                || state == STATE_TEXT_WRITED) {
                if (state == STATE_ELEMENT_START) {
                    writer.write(START_ELEMENT_END_NORMAL);
                }
                String s = StringUtils.toString(value);
                if (s != null) {
                    writer.write(s);
                    state = STATE_TEXT_WRITED;
                }
            }
        } catch (IOException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public void endDocument() throws StaxerXmlStreamException {
        while (!startedElements.isEmpty()) {
            endElement();
        }
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public void attribute(
            XmlName attributeName, Object value
    ) throws StaxerXmlStreamException {
        try {
            if (state == STATE_ELEMENT_START) {
                String s = StringUtils.notEmptyElseNull(StringUtils.toString(value));
                if (s != null) {
                    String namespacePrefix = null;
                    String namespaceURI = attributeName.getNamespaceURI();
                    if (namespaceURI != null) {
                        namespacePrefix = getPrefix(namespaceURI);
                    }
                    writer.write(' ');
                    if (namespacePrefix != null) {
                        writer.write(namespacePrefix);
                        writer.write(':');
                    }
                    writer.write(attributeName.getLocalPart());
                    writer.write("=\"");
                    writer.write(s);
                    writer.write('"');
                }
                if (!namespacesToDeclare.isEmpty()) {
                    for (String namespaceToDeclare : namespacesToDeclare) {
                        String namespacePrefix = getPrefix(namespaceToDeclare);
                        writer.write(" xmlns:");
                        writer.write(namespacePrefix);
                        writer.write("=\"");
                        writer.write(namespaceToDeclare);
                        writer.write('"');
                        namespacesCurrent = StringUtils.join("-", namespacesCurrent, namespaceToDeclare);
                    }
                    namespacesToDeclare.clear();
                }
            }
        } catch (IOException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public void element(
            XmlName elementName, Object value
    ) throws StaxerXmlStreamException {
        element(elementName, value, false);
    }

    public void element(
            XmlName elementName, Object value, boolean nillable
    ) throws StaxerXmlStreamException {
        String s = StringUtils.notEmptyElseNull(StringUtils.toString(value));
        if (s != null) {
            startElement(elementName);
            text(s);
            endElement();
        } else if (nillable) {
            startElement(elementName);
            attribute(XML_NAME_XSI_NIL, "true");
            endElement();
        }
    }

    public void elementAndAttribute(
            XmlName elementName, Object elementValue,
            XmlName attributeName, Object attributeValue
    ) throws StaxerXmlStreamException {
        String s = StringUtils.notEmptyElseNull(StringUtils.toString(elementValue));
        startElement(elementName);
        attribute(attributeName, attributeValue);
        if (s != null) {
            text(s);
        }
        endElement();
    }

    public void emptyElement(XmlName elementName) throws StaxerXmlStreamException {
        startElement(elementName);
        endElement();
    }

}
