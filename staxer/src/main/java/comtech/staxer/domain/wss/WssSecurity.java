package comtech.staxer.domain.wss;

import comtech.util.xml.ReadXml;
import comtech.util.xml.WriteXml;
import comtech.util.xml.XmlName;
import comtech.util.xml.read.DocumentXmlStreamReader2;
import comtech.util.xml.write.DocumentXmlStreamWriter2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 12.11.2009
 * Time: 16:51:04
 */
@XmlRootElement(name = "Security",
                namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd")
@XmlAccessorType(XmlAccessType.FIELD)
public class WssSecurity implements ReadXml, WriteXml {

    public static final XmlName XML_NAME_USERNAME_TOKEN = new XmlName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "UsernameToken");

    @XmlElement(name = "UsernameToken",
                namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd")
    private WssUsernameToken usernameToken;

    public void readXml(DocumentXmlStreamReader2 reader, XmlName elementName) throws XMLStreamException {
        while (reader.readNext()) {
            if (reader.elementEnded(elementName)) {
                break;
            } else if (reader.elementStarted(XML_NAME_USERNAME_TOKEN)) {
                usernameToken = new WssUsernameToken();
                usernameToken.readXml(reader, XML_NAME_USERNAME_TOKEN);
            }
        }
    }

    public void writeXml(DocumentXmlStreamWriter2 writer, XmlName elementName) throws IOException {
        writer.startElement(elementName);
        if (usernameToken != null) {
            usernameToken.writeXml(writer, XML_NAME_USERNAME_TOKEN);
        }
        writer.endElement();
    }

    public WssUsernameToken getUsernameToken() {
        return usernameToken;
    }

    public void setUsernameToken(WssUsernameToken usernameToken) {
        this.usernameToken = usernameToken;
    }
}
