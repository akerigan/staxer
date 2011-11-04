package comtech.staxer.soap;

import comtech.util.xml.read.DocumentXmlStreamReader;
import comtech.util.xml.write.DocumentXmlStreamWriter;

import javax.xml.stream.XMLStreamException;

/**
 * @author Anton Proshin (proshin.anton@gmail.com)
 * @since 2011-09-14 10:24 (Moscow/Europe)
 */
public interface StaxerXmlHandler {

    void callWriteXml(
            DocumentXmlStreamWriter dxsr, StaxerXmlHandler contentObj,
            String nsPrefix, String nsUri
    ) throws XMLStreamException;

    void callWriteXmlAsSoapBody(
            StaxerXmlHandler contentObj, DocumentXmlStreamWriter dxsr
    ) throws XMLStreamException;

    void callWriteXmlAsSoapBody(
            StaxerXmlHandler contentObj, DocumentXmlStreamWriter dxsr,
            String nsPrefix, String rootName, String nsUri
    ) throws XMLStreamException;

    public String callWriteXmlAsRoot(
            StaxerXmlHandler contentObj
    ) throws XMLStreamException;

    public String callWriteXmlAsRoot(
            StaxerXmlHandler contentObj, String nsPrefix,
            String rootName, String nsUri
    ) throws XMLStreamException;

    public StaxerXmlHandler callReadXml(
            DocumentXmlStreamReader dxsr
    ) throws XMLStreamException;

    public StaxerXmlHandler callReadXmlAsRoot(
            String responseXml
    ) throws XMLStreamException;

    public StaxerXmlHandler callReadXmlAsRoot(
            String responseXml, String rootName, String namespaceUri
    ) throws XMLStreamException;

}
