package comtech.staxer.domain;

import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 12.11.2009
 * Time: 16:51:27
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WssUsernameToken implements StaxerReadXml, StaxerWriteXml {

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

    public void readXmlAttributes(XmlNameMapProperties attributes) throws StaxerXmlStreamException {
    }

    public void readXmlContent(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        XmlName rootElementName = xmlReader.getLastStartedElement();
        while (xmlReader.readNext() && !xmlReader.elementEnded(rootElementName)) {
            if (xmlReader.elementStarted(XML_NAME_USERNAME)) {
                userName = xmlReader.readCharacters(XML_NAME_USERNAME);
            } else if (xmlReader.elementStarted(XML_NAME_PASSWORD)) {
                password = XmlUtils.readXml(xmlReader, WssPassword.class, XML_NAME_PASSWORD);
            } else if (xmlReader.elementStarted(XML_NAME_NONCE)) {
                nonce = XmlUtils.readXml(xmlReader, WssNonce.class, XML_NAME_NONCE);
            } else if (xmlReader.elementStarted(XML_NAME_CREATED)) {
                created = xmlReader.readCharacters(XML_NAME_CREATED);
            }
        }
    }

    public void writeXmlAttributes(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
    }

    public void writeXmlContent(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        xmlWriter.element(XML_NAME_USERNAME, userName);
        XmlUtils.writeXmlElement(xmlWriter, password, XML_NAME_PASSWORD);
        XmlUtils.writeXmlElement(xmlWriter, nonce, XML_NAME_NONCE);
        xmlWriter.element(XML_NAME_CREATED, created);
    }
}
