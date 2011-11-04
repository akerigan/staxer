package comtech.util.xml;

import javax.xml.stream.XMLStreamException;
import java.io.*;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 14.11.2008
 * Time: 8:18:55
 */
public class XmlUtils {

    public static <T extends ReadXml> T readXml(
            InputStream inputStream, String charset, Class<T> cls, XmlName elementName
    ) throws UnsupportedEncodingException, XMLStreamException, InstantiationException, IllegalAccessException {
        return readXml(new InputStreamReader(inputStream, charset), cls, elementName);
    }

    public static <T extends ReadXml> T readXml(
            Reader reader, Class<T> cls, XmlName elementName
    ) throws XMLStreamException, IllegalAccessException, InstantiationException {
        return readXml(new XmlStreamReader(reader), cls, elementName);
    }

    public static <T extends ReadXml> T readXml(
            XmlStreamReader reader, Class<T> cls, XmlName elementName
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

    public static void writeXml(
            OutputStream outputStream, String charset, int indentSize,
            WriteXml writeXml, XmlName rootElementName
    ) throws Exception {
        if (writeXml != null && rootElementName != null) {
            XmlStreamWriter writer = new XmlStreamWriter(
                    outputStream, charset, indentSize
            );
            writer.startDocument();
            writeXml.writeXml(writer, rootElementName);
            writer.endDocument();
        }
    }

    public static void serializeSoapEnvelop(
            OutputStream outputStream, String charset, int indentSize,
            WriteXml writeXml, XmlName rootElementName
    ) throws Exception {
        XmlStreamWriter writer = new XmlStreamWriter(
                outputStream, charset, indentSize
        );
        writer.startDocument();
        writer.startElement(XmlConstants.XML_NAME_SOAP_ENVELOPE);
        writer.startElement(XmlConstants.XML_NAME_SOAP_ENVELOPE_BODY);
        if (writeXml != null && rootElementName != null) {
            writeXml.writeXml(writer, rootElementName);
        }

        writer.endElement();
        writer.endElement();
        writer.endDocument();
    }

}
