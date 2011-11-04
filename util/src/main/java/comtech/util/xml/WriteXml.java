package comtech.util.xml;

import comtech.util.xml.write.DocumentXmlStreamWriter2;

import java.io.IOException;

/**
 * @author Vlad Vinichenko(akerigan@gmail.com)
 * @since 2011-10-14 22:13 (Europe/Moscow)
 */
public interface WriteXml {

    public void writeXml(
            DocumentXmlStreamWriter2 writer, XmlName elementName
    ) throws IOException;

}
