package comtech.util.xml;

import comtech.util.xml.read.DocumentXmlStreamReader2;

import javax.xml.stream.XMLStreamException;

/**
 * @author Vlad Vinichenko(akerigan@gmail.com)
 * @since 2011-10-14 22:11 (Europe/Moscow)
 */
public interface ReadXml {

    public void readXml(
            DocumentXmlStreamReader2 reader, XmlName elementName
    ) throws XMLStreamException;

}
