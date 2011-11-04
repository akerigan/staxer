package comtech.staxer.soap;

import comtech.util.xml.write.DocumentXmlStreamWriter;
import comtech.util.xml.write.DocumentXmlStreamWriter2;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 15.02.2010
 * Time: 17:32:38
 */
public interface SoapHeader {

    public void write(DocumentXmlStreamWriter document) throws XMLStreamException, NoSuchAlgorithmException;

    public void write(DocumentXmlStreamWriter2 document) throws NoSuchAlgorithmException, IOException;

}
