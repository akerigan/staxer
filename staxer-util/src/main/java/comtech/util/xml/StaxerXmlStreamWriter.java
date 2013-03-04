package comtech.util.xml;

import comtech.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    private int level = -1;
    private int state = STATE_DOCUMENT_START;

    private ArrayList<XmlElementContext> allContexts = new ArrayList<XmlElementContext>();

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

    public StaxerXmlStreamWriter(
            Writer writer
    ) {
        this(writer, "UTF-8", 0);
    }

    public StaxerXmlStreamWriter(
            Writer writer, String charset
    ) {
        this(writer, charset, 0);
    }

    public StaxerXmlStreamWriter(
            Writer writer, String charset, int indentSize
    ) {
        this.writer = writer;
        this.charset = charset;
        this.indentSize = indentSize;
    }

    public void startDocument() throws StaxerXmlStreamException {
        startDocument(null, null, true);
    }

    public void startDocument(boolean writeProlog) throws StaxerXmlStreamException {
        startDocument(null, null, writeProlog);
    }

    public void startDocument(
            String piName, String piData
    ) throws StaxerXmlStreamException {
        startDocument(piName, piData, true);
    }

    public void startDocument(
            String piName, String piData, boolean writeProlog
    ) throws StaxerXmlStreamException {
        try {
            if (writeProlog) {
                writer.write("<?xml version=\"1.0\" encoding=\"");
                writer.write(charset);
                writer.write("\"?>");
            }
            if (!StringUtils.isEmpty(piName)) {
                writer.write("<?");
                writer.write(piName);
                if (!StringUtils.isEmpty(piName)) {
                    writer.write(" ");
                    writer.write(piData);
                }
                writer.write("?>");
            }
            state = STATE_DOCUMENT_STARTED;
        } catch (IOException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public void declareNamespace(String namespaceURI) {
        declareNamespace(namespaceURI, null);
    }

    public void declareNamespace(String namespaceURI, String defaultPrefix) {
        if (!allContexts.isEmpty()) {
            XmlElementContext currentContext = allContexts.get(level);
            currentContext.prefix(namespaceURI, defaultPrefix);
        }
    }

    public void startElement(String elementName) throws StaxerXmlStreamException {
        startElement(new XmlName(elementName));
    }

    public void startElement(XmlName elementName) throws StaxerXmlStreamException {
        try {
            if (state != STATE_DOCUMENT_ENDED) {
                XmlElementContext previousContext = null;
                XmlElementContext currentContext;
                if (!allContexts.isEmpty()) {
                    previousContext = allContexts.get(level);
                    currentContext = previousContext.createChild(elementName);
                } else {
                    currentContext = new XmlElementContext(
                            elementName, 0, null
                    );
                }
                allContexts.add(currentContext);
                if (state == STATE_ELEMENT_START) {
                    writeNamespaces(previousContext);
                    writer.write(START_ELEMENT_END_NORMAL);
                    state = STATE_ELEMENT_STARTED;
                }
                writeIndents();
                writer.write('<');
                XmlName xmlName = currentContext.getXmlName();
                String prefix = xmlName.getPrefix();
                if (prefix != null) {
                    writer.write(prefix);
                    writer.write(':');
                }
                writer.write(xmlName.getLocalPart());
                state = STATE_ELEMENT_START;
                level += 1;
            }
        } catch (IOException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    private void writeNamespaces(XmlElementContext context) throws IOException {
        if (context != null) {
            ArrayList<String> namespacesToDeclare = context.getNamespacesToDeclare();
            if (namespacesToDeclare != null && !namespacesToDeclare.isEmpty()) {
                for (String namespaceToDeclare : namespacesToDeclare) {
                    String namespacePrefixToDeclare = context.prefix(namespaceToDeclare);
                    writer.write(" xmlns:");
                    writer.write(namespacePrefixToDeclare);
                    writer.write("=\"");
                    writer.write(namespaceToDeclare);
                    writer.write('"');
                }
            }
        }
    }

    private void writeIndents() throws StaxerXmlStreamException {
        try {
            if (indentSize > 0
                    && (state == STATE_ELEMENT_STARTED || state == STATE_DOCUMENT_STARTED)) {
                if (level > -1) {
                    writer.write(10);
                }
                for (int i = 0; i <= level; ++i) {
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
        endElement(false);
    }

    public void endElement(boolean c14n) throws StaxerXmlStreamException {
        try {
            if (!allContexts.isEmpty()) {
                XmlElementContext currentContext = allContexts.remove(level);
                level -= 1;
                if (state == STATE_ELEMENT_START && !c14n) {
                    writeNamespaces(currentContext);
                    writer.write(START_ELEMENT_END_EMPTY);
                } else {
                    if (state == STATE_ELEMENT_START && c14n) {
                        writeNamespaces(currentContext);
                        writer.write(">");
                    }
                    writeIndents();
                    writer.write("</");
                    XmlName xmlName = currentContext.getXmlName();
                    String prefix = xmlName.getPrefix();
                    if (prefix != null) {
                        writer.write(prefix);
                        writer.write(':');
                    }
                    writer.write(xmlName.getLocalPart());
                    writer.write('>');
                }
                if (level > -1) {
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
                String s = StringUtils.toString(value);
                if (s != null) {
                    if (state == STATE_ELEMENT_START) {
                        writeNamespaces(allContexts.get(level));
                        writer.write(START_ELEMENT_END_NORMAL);
                    }
                    appendQuoted(s, writer);
                    state = STATE_TEXT_WRITED;
                }
            }
        } catch (IOException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public void endDocument() throws StaxerXmlStreamException {
        while (!allContexts.isEmpty()) {
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
            String attributeName, Object value
    ) throws StaxerXmlStreamException {
        attribute(new XmlName(attributeName), value);
    }

    public void attribute(
            XmlName attributeName, Object value
    ) throws StaxerXmlStreamException {
        try {
            if (state == STATE_ELEMENT_START) {
                String s = StringUtils.notEmptyElseNull(StringUtils.toString(value));
                if (s != null) {
                    writer.write(' ');
                    XmlElementContext currentContext = allContexts.get(level);
                    String namespaceURI = attributeName.getNamespaceURI();
                    if (namespaceURI != null) {
                        writer.write(currentContext.prefix(namespaceURI));
                        writer.write(':');
                    }
                    writer.write(attributeName.getLocalPart());
                    writer.write("=\"");
                    appendQuoted(s, writer);
                    writer.write('"');
                }
            }
        } catch (IOException e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public void element(
            String elementName, Object value
    ) throws StaxerXmlStreamException {
        element(new XmlName(elementName), value, false);
    }

    public void element(
            XmlName elementName, Object value
    ) throws StaxerXmlStreamException {
        element(elementName, value, false);
    }

    public void element(
            XmlName elementName, Object value, boolean nillable
    ) throws StaxerXmlStreamException {
        if (value != null) {
            if (value instanceof StaxerWriteXml) {
                startElement(elementName);
                ((StaxerWriteXml) value).writeXmlAttributes(this);
                ((StaxerWriteXml) value).writeXmlContent(this);
                endElement();
            } else {
                String s = StringUtils.notEmptyElseNull(StringUtils.toString(value));
                if (s != null) {
                    startElement(elementName);
                    text(s);
                    endElement();
                }
            }
        } else if (nillable) {
            startElement(elementName);
            attribute(XML_NAME_XSI_NIL, "true");
            endElement();
        }

    }

    public void elementAndAttribute(
            String elementName, Object elementValue,
            String attributeName, Object attributeValue
    ) throws StaxerXmlStreamException {
        elementAndAttribute(
                new XmlName(elementName), elementValue,
                new XmlName(attributeName), attributeValue
        );
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

    private void appendQuoted(String s, Appendable a) throws IOException {
        char[] chars = s.toCharArray();
        for (char ch : chars) {
            if (ch <= 0x1f) {
                continue;
            }
            switch (ch) {
                case '<':
                    a.append("&lt;");
                    break;
                case '>':
                    a.append("&gt;");
                    break;
                case '&':
                    a.append("&amp;");
                    break;
                case '"':
                    a.append("&quot;");
                    break;
                default:
                    a.append(ch);
            }
        }
    }


    static class XmlElementContext {
        private XmlName xmlName;
        private Map<String, String> prefixes;
        private int currentPrefixIdx;
        private ArrayList<String> namespacesToDeclare;

        XmlElementContext(
                XmlName xmlName, int currentPrefixIdx,
                Map<String, String> declaredPrefixes
        ) {
            this.xmlName = xmlName;
            this.currentPrefixIdx = currentPrefixIdx;
            if (declaredPrefixes != null) {
                prefixes = new HashMap<String, String>();
                prefixes.putAll(declaredPrefixes);
            }
            String namespaceURI = xmlName.getNamespaceURI();
            if (namespaceURI != null) {
                xmlName.setPrefix(prefix(namespaceURI, xmlName.getPrefix()));
            }
        }

        public XmlName getXmlName() {
            return xmlName;
        }

        public String prefix(String namespaceURI) {
            return prefix(namespaceURI, null);
        }

        public String prefix(String namespaceURI, String defaultPrefix) {
            if (namespaceURI != null) {
                String result = null;
                if (prefixes != null) {
                    result = prefixes.get(namespaceURI);
                } else {
                    prefixes = new HashMap<String, String>();
                }
                if (result == null) {
                    if (defaultPrefix != null) {
                        result = defaultPrefix;
                    } else {
                        result = XmlConstants.DEFAULT_NAMESPACES_PREFIXES.get(namespaceURI);
                        if (result == null) {
                            currentPrefixIdx += 1;
                            result = "ns" + currentPrefixIdx;
                        }
                    }
                    if (namespacesToDeclare == null) {
                        namespacesToDeclare = new ArrayList<String>();
                    }
                    namespacesToDeclare.add(namespaceURI);
                    prefixes.put(namespaceURI, result);
                }
                return result;
            } else {
                return null;
            }
        }

        public ArrayList<String> getNamespacesToDeclare() {
            return namespacesToDeclare;
        }

        public XmlElementContext createChild(XmlName childElementName) {
            return new XmlElementContext(
                    childElementName, currentPrefixIdx, prefixes
            );
        }

    }

}

