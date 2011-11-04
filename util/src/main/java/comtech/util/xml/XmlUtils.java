package comtech.util.xml;

import comtech.util.xml.read.DocumentXmlStreamReader;
import comtech.util.xml.read.DocumentXmlStreamReader2;
import comtech.util.xml.read.StartElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.*;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 14.11.2008
 * Time: 8:18:55
 */
public class XmlUtils {

    private static TransformerFactory transformerFactory = null;
    private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private static XPath xpath = XPathFactory.newInstance().newXPath();

    private XmlUtils() {
    }

    public static String serialize(Object o) throws JAXBException {
        StringWriter stringWriter = new StringWriter();
        serialize(o, stringWriter, false);
        return stringWriter.toString();
    }

    public static void serialize(Object o, Writer writer) throws JAXBException {
        serialize(o, writer, false);
    }

    public static void serialize(
            Object o, OutputStream outputStream
    ) throws JAXBException, UnsupportedEncodingException {
        serialize(o, new OutputStreamWriter(outputStream, "UTF-8"), false);
    }

    public static void serialize(Object o, Writer writer, boolean format) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(o.getClass());
        Marshaller marshaller = jaxbContext.createMarshaller();
        if (format) {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        }
        marshaller.marshal(o, writer);
    }

    public static <T> T deserialize(Class<T> cls, Reader reader) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(cls);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (T) unmarshaller.unmarshal(reader);
    }

    public static <T> T deserialize(Class<T> cls, Node node) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(cls);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (T) unmarshaller.unmarshal(node);
    }

    public static boolean elementExist(InputStream inputStream, QName elementName) throws XMLStreamException {
        DocumentXmlStreamReader reader = new DocumentXmlStreamReader(inputStream);
        StartElement startElement = reader.readStartElement(elementName);
        return startElement != null;
    }

    private static Transformer getTransformer(boolean doIndents) throws TransformerException {
        if (transformerFactory == null) {
            try {
                transformerFactory = TransformerFactory.newInstance();
            } catch (TransformerFactoryConfigurationError e) {
                throw new TransformerException(e);
            }
        }

        Transformer transformer = transformerFactory.newTransformer();
        if (doIndents) {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        }
        return transformer;
    }

    public static synchronized Document createDocument(InputStream inputStream)
            throws ParserConfigurationException, SAXException, IOException {
        return documentBuilderFactory.newDocumentBuilder().parse(new InputSource(inputStream));
    }

    public static synchronized Document createDocument(Reader reader)
            throws ParserConfigurationException, SAXException, IOException {
        return documentBuilderFactory.newDocumentBuilder().parse(new InputSource(reader));
    }

    public static Node find(Node startAt, String xpathstring) throws XPathExpressionException {
        return (Node) xpath.evaluate(xpathstring, startAt, XPathConstants.NODE);
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

    /**
     * Ищет подэлемент указанного элемента по пути
     *
     * @param element элемент
     * @param path    путь
     * @return найденый элемент
     */
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

    /**
     * Возвращает значение атрибута attributeName элемента e
     *
     * @param e             элемент
     * @param attributeName имя атрибута
     * @return String значение атрибута
     */
    public static String readAttributeValue(Element e, String attributeName) {
        if (!e.hasAttribute(attributeName)) {
            throw new IllegalStateException("Attribute '" + attributeName + "' is absent");
        }

        return e.getAttribute(attributeName);
    }

    /**
     * Возвращает значение атрибута attributeName элемента e или defaultValue, если атрибут отсутствует
     *
     * @param e             элемент
     * @param attributeName имя атрибута
     * @param defaultValue  значение по умолчанию
     * @return String значение атрибута
     */
    public static String readAttributeValue(Element e, String attributeName, String defaultValue) {
        if (e.hasAttribute(attributeName)) {
            return e.getAttribute(attributeName);
        } else {
            return defaultValue;
        }
    }

    /**
     * Считывает значение атрибута как дату.
     *
     * @param e             Элемент xml-документа
     * @param attributeName Имя атрибута
     * @param format        Формат даты
     * @return Date Считаная дата
     * @throws java.text.ParseException если не удаётся считать атрибут как дату или атрибут отсутствует
     */
    public static Date readDate(
            Element e, String attributeName, DateFormat format
    ) throws ParseException {
        String dateStr = readAttributeValue(e, attributeName);
        return format.parse(dateStr);
    }

    /**
     * Считывает значение атрибута как дату.
     *
     * @param e             Элемент xml-документа
     * @param attributeName имя атрибута
     * @param format        Формат даты
     * @return Date считаная дата
     * @throws ParseException если не удаётся считать атрибут как дату
     */
    public static Date readDateIfExists(Element e, String attributeName, DateFormat format) throws ParseException {
        String dateStr = readAttributeValue(e, attributeName, null);

        if (dateStr == null) {
            return null;
        }

        return format.parse(dateStr);
    }

    public static <T extends ReadXml> T readXml(
            InputStream inputStream, String charset, Class<T> cls, XmlName elementName
    ) throws UnsupportedEncodingException, XMLStreamException, InstantiationException, IllegalAccessException {
        return readXml(new InputStreamReader(inputStream, charset), cls, elementName);
    }

    public static <T extends ReadXml> T readXml(
            Reader reader, Class<T> cls, XmlName elementName
    ) throws XMLStreamException, IllegalAccessException, InstantiationException {
        return readXml(new DocumentXmlStreamReader2(reader), cls, elementName);
    }

    public static <T extends ReadXml> T readXml(
            DocumentXmlStreamReader2 reader, Class<T> cls, XmlName elementName
    ) throws XMLStreamException, IllegalAccessException, InstantiationException {
        if (!reader.elementStarted(elementName)) {
            reader.readStartElement(elementName);
        }
        if (reader.elementStarted(elementName)) {
            T t = cls.newInstance();
            t.readXml(reader, elementName);
            return t;
        } else {
            return null;
        }
    }

}
