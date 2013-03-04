package org.staxer.util.xml;

import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.soap.SoapFault;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.staxer.util.xml.XmlConstants.XML_NAME_SOAP_ENVELOPE_FAULT;
import static org.staxer.util.xml.XmlConstants.XML_NAME_XSI_NIL;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 14.11.2008
 * Time: 8:18:55
 */
public class XmlUtils {

    private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    public static boolean elementExist(
            InputStream inputStream, XmlName elementName
    ) throws StaxerXmlStreamException {
        StaxerXmlStreamReader reader = new StaxerXmlStreamReader(inputStream);
        return reader.readStartElement(elementName);
    }

    public static synchronized Document createDocument(InputStream inputStream)
            throws ParserConfigurationException, SAXException, IOException {
        return documentBuilderFactory.newDocumentBuilder().parse(new InputSource(inputStream));
    }

    public static synchronized Document createDocument(Reader reader)
            throws ParserConfigurationException, SAXException, IOException {
        return documentBuilderFactory.newDocumentBuilder().parse(new InputSource(reader));
    }

    public static String fetchSubElementValue(Element e, String subElementName) {
        Element element = fetchSubElement(e, subElementName);
        if (element != null) {
            return getTextContent(element);
        }
        return null;
    }

    public static String fetchSubElementValue(Element parent, String subElementName, String lang) {
        Collection<Element> elements = fetchSubElements(parent, subElementName);

        lang = lang.toUpperCase();

        for (Element e : elements) {
            String lng = e.getAttribute("xml:lang");
            if (lng != null && lang.equals(lng.toUpperCase())) {
                return getTextContent(e);
            }
        }

        return fetchSubElementValue(parent, subElementName);
    }

    public static Element fetchSubElement(Element e, String subElementName) {
        if (e == null || subElementName == null) {
            return null;
        }

        subElementName = subElementName.toUpperCase();

        NodeList list = e.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i) instanceof Element) {
                Element element = (Element) list.item(i);
                if (element.getTagName().toUpperCase().equals(subElementName)) {
                    return element;
                }
            }
        }

        return null;
    }

    public static Collection<Element> fetchSubElements(Element e, String subElementName) {
        if (e == null || subElementName == null) {
            return null;
        }

        subElementName = subElementName.toUpperCase();

        Collection<Element> elements = new ArrayList<Element>();

        NodeList list = e.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i) instanceof Element) {
                Element element = (Element) list.item(i);
                if (element.getTagName().toUpperCase().equals(subElementName)) {
                    elements.add(element);
                }
            }
        }

        return elements;
    }

    public static Element getElementByPath(Element element, String path) {
        Element result = element;

        for (String token : path.split("\\/")) {
            result = fetchSubElement(result, token);
            if (result == null) {
                return null;
            }
        }

        return result;
    }

    public static String getTextContent(Node e) {
        if (e == null || e.getNodeType() != Node.ELEMENT_NODE) {
            return null;
        }

        NodeList nodes = e.getChildNodes();
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = e.getFirstChild();

            if (node != null && node.getNodeType() == Node.TEXT_NODE) {
                String s = node.getNodeValue();
                if (s != null) {
                    text.append(s);
                }
            }
        }

        if (text.length() > 0) {
            return text.toString();
        } else {
            return null;
        }
    }

    public static String getSubElementTextContent(Element e, String subElementName) {
        Element subElement = fetchSubElement(e, subElementName);
        if (subElement != null) {
            return getTextContent(subElement);
        } else {
            return null;
        }
    }

    public static String readAttributeValue(Element e, String attributeName) {
        if (!e.hasAttribute(attributeName)) {
            throw new IllegalStateException("Attribute '" + attributeName + "' is absent");
        }

        return e.getAttribute(attributeName);
    }

    public static String readAttributeValue(Element e, String attributeName, String defaultValue) {
        if (e.hasAttribute(attributeName)) {
            return e.getAttribute(attributeName);
        } else {
            return defaultValue;
        }
    }

    public static Date readDate(
            Element e, String attributeName, DateFormat format
    ) throws ParseException {
        String dateStr = readAttributeValue(e, attributeName);
        return format.parse(dateStr);
    }

    public static Date readDateIfExists(Element e, String attributeName, DateFormat format) throws ParseException {
        String dateStr = readAttributeValue(e, attributeName, null);

        if (dateStr == null) {
            return null;
        }

        return format.parse(dateStr);
    }

    public static <T> T deserializeJaxb(Class<T> cls, Reader reader) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(cls);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (T) unmarshaller.unmarshal(reader);
    }

    public static <T> T deserializeJaxb(Class<T> cls, Node node) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(cls);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (T) unmarshaller.unmarshal(node);
    }

    public static <T extends StaxerReadXml> T readXml(
            InputStream inputStream, String charset, Class<T> cls, XmlName elementName
    ) throws StaxerXmlStreamException {
        try {
            return readXml(new InputStreamReader(inputStream, charset), cls, elementName);
        } catch (StaxerXmlStreamException e) {
            throw e;
        } catch (Exception e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public static <T extends StaxerReadXml> T readXml(
            Reader reader, Class<T> cls, XmlName elementName
    ) throws StaxerXmlStreamException {
        try {
            return readXml(new StaxerXmlStreamReader(reader), cls, elementName);
        } catch (StaxerXmlStreamException e) {
            throw e;
        } catch (Exception e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public static <T extends StaxerReadXml> T readXml(
            StaxerXmlStreamReader xmlReader, Class<T> cls, XmlName elementName
    ) throws StaxerXmlStreamException {
        return readXml(xmlReader, cls, elementName, false);
    }

    public static <T extends StaxerReadXml> T readXml(
            StaxerXmlStreamReader xmlReader, Class<T> cls,
            XmlName elementName, boolean nillable
    ) throws StaxerXmlStreamException {
        try {
            if (elementName != null && !xmlReader.elementStarted(elementName)) {
                xmlReader.readStartElement(elementName);
            }
            if (elementName == null || xmlReader.elementStarted(elementName)) {
                if (!xmlReader.elementStarted()) {
                    xmlReader.readStartElement();
                }
                XmlNameMapProperties attributes = xmlReader.getAttributes();
                if (nillable && attributes.getBoolean(XML_NAME_XSI_NIL)) {
                    return null;
                }
                T t = cls.newInstance();
                t.readXmlAttributes(attributes);
                t.readXmlContent(xmlReader);
                return t;
            } else {
                return null;
            }
        } catch (StaxerXmlStreamException e) {
            throw e;
        } catch (Exception e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public static void writeXml(
            OutputStream outputStream, String charset, int indentSize,
            StaxerWriteXml staxerWriteXml, XmlName rootElementName
    ) throws StaxerXmlStreamException {
        try {
            if (staxerWriteXml != null && rootElementName != null) {
                StaxerXmlStreamWriter xmlWriter = new StaxerXmlStreamWriter(
                        outputStream, charset, indentSize
                );
                xmlWriter.startDocument();
                xmlWriter.startElement(rootElementName);
                staxerWriteXml.writeXmlAttributes(xmlWriter);
                staxerWriteXml.writeXmlContent(xmlWriter);
                xmlWriter.endElement();
                xmlWriter.endDocument();
            }
        } catch (StaxerXmlStreamException e) {
            throw e;
        } catch (Exception e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public static void writeSoapEnvelopedElement(
            OutputStream outputStream, String charset, int indentSize,
            StaxerWriteXml staxerWriteXml, XmlName payloadElementName
    ) throws StaxerXmlStreamException {
        writeSoapEnvelopedElement(
                outputStream, charset, indentSize,
                staxerWriteXml, payloadElementName, null
        );
    }

    public static void writeSoapEnvelopedElement(
            OutputStream outputStream, String charset, int indentSize,
            StaxerWriteXml staxerWriteXml, XmlName payloadElementName,
            List<? extends StaxerWriteXml> headerWriters
    ) throws StaxerXmlStreamException {
        try {
            StaxerXmlStreamWriter xmlWriter = new StaxerXmlStreamWriter(
                    outputStream, charset, indentSize
            );
            xmlWriter.startDocument();
            xmlWriter.startElement(XmlConstants.XML_NAME_SOAP_ENVELOPE);
            if (headerWriters != null && !headerWriters.isEmpty()) {
                xmlWriter.startElement(XmlConstants.XML_NAME_SOAP_ENVELOPE_HEADER);
                for (StaxerWriteXml headerWriter : headerWriters) {
                    headerWriter.writeXmlAttributes(xmlWriter);
                    headerWriter.writeXmlContent(xmlWriter);
                }
                xmlWriter.endElement();
            }
            xmlWriter.startElement(XmlConstants.XML_NAME_SOAP_ENVELOPE_BODY);
            if (staxerWriteXml != null) {
                if (staxerWriteXml instanceof SoapFault) {
                    payloadElementName = XML_NAME_SOAP_ENVELOPE_FAULT;
                }
                if (payloadElementName != null) {
                    xmlWriter.startElement(payloadElementName);
                    staxerWriteXml.writeXmlAttributes(xmlWriter);
                    staxerWriteXml.writeXmlContent(xmlWriter);
                    xmlWriter.endElement();
                }
            }

            xmlWriter.endElement();
            xmlWriter.endElement();
            xmlWriter.endDocument();
        } catch (StaxerXmlStreamException e) {
            throw e;
        } catch (Exception e) {
            throw new StaxerXmlStreamException(e);
        }
    }

    public static void writeXmlElement(
            StaxerXmlStreamWriter xmlWriter, StaxerWriteXml staxerWriteXml, XmlName elementName
    ) throws StaxerXmlStreamException {
        writeXmlElement(xmlWriter, elementName, staxerWriteXml, false);
    }

    public static void writeXmlElement(
            StaxerXmlStreamWriter xmlWriter, XmlName elementName,
            StaxerWriteXml staxerWriteXml, boolean nillable
    ) throws StaxerXmlStreamException {
        try {
            if (staxerWriteXml != null) {
                xmlWriter.startElement(elementName);
                staxerWriteXml.writeXmlAttributes(xmlWriter);
                staxerWriteXml.writeXmlContent(xmlWriter);
                xmlWriter.endElement();
            } else if (nillable) {
                xmlWriter.startElement(elementName);
                xmlWriter.attribute(XML_NAME_XSI_NIL, "true");
                xmlWriter.endElement();
            }
        } catch (StaxerXmlStreamException e) {
            throw e;
        } catch (Exception e) {
            throw new StaxerXmlStreamException(e);
        }
    }

}


