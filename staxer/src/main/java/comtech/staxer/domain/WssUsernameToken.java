package comtech.staxer.domain;

import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 12.11.2009
 * Time: 16:51:27
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WssUsernameToken implements ReadXml, WriteXml {

    public static final XmlName XML_NAME_USERNAME = new XmlName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Username");
    public static final XmlName XML_NAME_PASSWORD = new XmlName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Password");
    public static final XmlName XML_NAME_NONCE = new XmlName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Nonce");
    public static final XmlName XML_NAME_CREATED = new XmlName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Created");

    @XmlElement(name = "Username",
            namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd")
    private String userName;

    @XmlElement(name = "Password",
            namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd")
    private WssPassword password;

    @XmlElement(name = "Nonce",
            namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd")
    private WssNonce nonce;

    @XmlElement(name = "Created",
            namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd")
    private String created;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public WssPassword getPassword() {
        return password;
    }

    public void setPassword(WssPassword password) {
        this.password = password;
    }

    public WssNonce getNonce() {
        return nonce;
    }

    public void setNonce(WssNonce nonce) {
        this.nonce = nonce;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void readXml(XmlStreamReader reader, XmlName elementName) throws XMLStreamException {
        while (reader.readNext()) {
            if (reader.elementEnded(elementName)) {
                break;
            } else if (reader.elementStarted(XML_NAME_USERNAME)) {
                userName = reader.readCharacters(XML_NAME_USERNAME);
            } else if (reader.elementStarted(XML_NAME_PASSWORD)) {
                password = new WssPassword();
                password.readXml(reader, XML_NAME_PASSWORD);
            } else if (reader.elementStarted(XML_NAME_NONCE)) {
                nonce = new WssNonce();
                nonce.readXml(reader, XML_NAME_NONCE);
            } else if (reader.elementStarted(XML_NAME_CREATED)) {
                created = reader.readCharacters(XML_NAME_CREATED);
            }
        }
    }

    public void writeXml(XmlStreamWriter writer, XmlName elementName) throws IOException {
        writer.startElement(elementName);
        if (userName != null) {
            writer.element(XML_NAME_USERNAME, userName);
        }
        if (password != null) {
            password.writeXml(writer, XML_NAME_PASSWORD);
        }
        if (nonce != null) {
            nonce.writeXml(writer, XML_NAME_NONCE);
        }
        if (created != null) {
            writer.element(XML_NAME_CREATED, created);
        }
        writer.endElement();
    }
}
