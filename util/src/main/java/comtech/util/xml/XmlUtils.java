package comtech.util.xml;

import comtech.util.props.StringMapProperties;
import comtech.util.xml.soap.SoapFault;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import static comtech.util.xml.XmlConstants.XML_NAME_SOAP_ENVELOPE_FAULT;
import static comtech.util.xml.XmlConstants.XML_NAME_XSI_NIL;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 14.11.2008
 * Time: 8:18:55
 */
public class XmlUtils {

    public static <T extends StaxerXmlReader> T readXml(
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

    public static <T extends StaxerXmlReader> T readXml(
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

    public static <T extends StaxerXmlReader> T readXml(
            StaxerXmlStreamReader xmlReader, Class<T> cls, XmlName elementName
    ) throws StaxerXmlStreamException {
        return readXml(xmlReader, cls, elementName, false);
    }

    public static <T extends StaxerXmlReader> T readXml(
            StaxerXmlStreamReader xmlReader, Class<T> cls,
            XmlName elementName, boolean nillable
    ) throws StaxerXmlStreamException {
        try {
            if (!xmlReader.elementStarted(elementName)) {
                xmlReader.readStartElement(elementName);
            }
            if (xmlReader.elementStarted(elementName)) {
                StringMapProperties attributes = xmlReader.getAttributes();
                if (nillable && attributes.getBoolean(XML_NAME_XSI_NIL.toString())) {
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
            StaxerXmlWriter staxerXmlWriter, XmlName rootElementName
    ) throws StaxerXmlStreamException {
        try {
            if (staxerXmlWriter != null && rootElementName != null) {
                StaxerXmlStreamWriter xmlWriter = new StaxerXmlStreamWriter(
                        outputStream, charset, indentSize
                );
                xmlWriter.startDocument();
                xmlWriter.startElement(rootElementName);
                staxerXmlWriter.writeXmlAttributes(xmlWriter);
                staxerXmlWriter.writeXmlContent(xmlWriter);
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
            StaxerXmlWriter staxerXmlWriter, XmlName payloadElementName
    ) throws StaxerXmlStreamException {
        try {
            StaxerXmlStreamWriter xmlWriter = new StaxerXmlStreamWriter(
                    outputStream, charset, indentSize
            );
            xmlWriter.startDocument();
            xmlWriter.startElement(XmlConstants.XML_NAME_SOAP_ENVELOPE);
            xmlWriter.startElement(XmlConstants.XML_NAME_SOAP_ENVELOPE_BODY);
            if (staxerXmlWriter != null) {
                if (staxerXmlWriter instanceof SoapFault) {
                    payloadElementName = XML_NAME_SOAP_ENVELOPE_FAULT;
                }
                if (payloadElementName != null) {
                    xmlWriter.startElement(payloadElementName);
                    staxerXmlWriter.writeXmlAttributes(xmlWriter);
                    staxerXmlWriter.writeXmlAttributes(xmlWriter);
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
            StaxerXmlStreamWriter xmlWriter, StaxerXmlWriter staxerXmlWriter, XmlName elementName
    ) throws StaxerXmlStreamException {
        writeXmlElement(xmlWriter, staxerXmlWriter, elementName, false);
    }

    public static void writeXmlElement(
            StaxerXmlStreamWriter xmlWriter, StaxerXmlWriter staxerXmlWriter,
            XmlName elementName, boolean nillable
    ) throws StaxerXmlStreamException {
        try {
            if (staxerXmlWriter != null) {
                xmlWriter.startElement(elementName);
                staxerXmlWriter.writeXmlAttributes(xmlWriter);
                staxerXmlWriter.writeXmlContent(xmlWriter);
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


