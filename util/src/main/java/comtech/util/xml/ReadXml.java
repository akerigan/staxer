package comtech.util.xml;

import javax.xml.stream.XMLStreamException;

/**
 * @author Vlad Vinichenko(akerigan@gmail.com)
 * @since 2011-10-14 22:11 (Europe/Moscow)
 */
public interface ReadXml {

    public void readXml(
            XmlStreamReader reader, XmlName elementName
    ) throws XMLStreamException;

}
