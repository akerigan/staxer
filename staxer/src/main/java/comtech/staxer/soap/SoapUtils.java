package comtech.staxer.soap;

import comtech.staxer.StaxerException;
import comtech.util.xml.read.DocumentXmlStreamReader;
import comtech.util.xml.read.StartElement;
import comtech.util.xml.write.DocumentXmlStreamWriter;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;

import static comtech.util.xml.XmlConstants.*;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 21.10.2009
 * Time: 13:00:41
 */
public class SoapUtils {

    public static void serialize(
            Object payload, OutputStream outputStream
    ) throws XMLStreamException {
        serialize(payload, outputStream, null);
    }

    public static void serialize(
            Object payload, OutputStream outputStream, Class[] classes
    ) throws XMLStreamException {
        DocumentXmlStreamWriter document1 = new DocumentXmlStreamWriter(outputStream);
        document1.startDocument("utf-8", "1.0");
        document1.startElement(NAMESPACE_PREFIX_SOAP, SOAP_ENVELOPE, NAMESPACE_URI_SOAP_ENVELOPE);
        document1.namespace(NAMESPACE_PREFIX_SOAP, NAMESPACE_URI_SOAP_ENVELOPE);
        document1.namespace(NAMESPACE_PREFIX_XSI, NAMESPACE_URI_XSI);
        document1.startElement(NAMESPACE_PREFIX_SOAP, SOAP_BODY, NAMESPACE_URI_SOAP_ENVELOPE);

/*
        if (payload instanceof StaxerXmlHandler) {
            ((StaxerXmlHandler) payload).callWriteXmlAsSoapBody((StaxerXmlHandler) payload, document1);
        } else {
*/
        document1.object(payload, classes);
/*
        }
*/
        document1.endElement();
        document1.endElement();
        document1.endDocument();
    }

    public static <T> T deserializeBody(DocumentXmlStreamReader xmlDocument, Class<T> responseClass)
            throws XMLStreamException, JAXBException, StaxerException {
        StartElement startElement = xmlDocument.readStartElement(QNAME_SOAP_ENVELOP_BODY);
        if (startElement == null || !QNAME_SOAP_ENVELOP_BODY.equals(startElement.getName())) {
            throw new StaxerException("Cant locate " + XML_NAME_SOAP_ENVELOPE_BODY + " element");
        }
        startElement = xmlDocument.readStartElement();
        if (startElement == null) {
            throw new StaxerException("Cant locate payload element");
        } else if (QNAME_SOAP_ENVELOP_FAULT.equals(startElement.getName())) {
            throw new StaxerException(xmlDocument.readObject(SoapFault.class, false));
        }
        return xmlDocument.readObject(responseClass, false);
    }
}
