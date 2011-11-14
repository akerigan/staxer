package comtech.staxer.domain;

import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 12.11.2009
 * Time: 16:51:04
 */
@XmlRootElement(name = "Security",
                namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd")
@XmlAccessorType(XmlAccessType.FIELD)
public class WssSecurity implements StaxerReadXml, StaxerWriteXml {

    @XmlElement(name = "UsernameToken",
                namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd")
    private WssUsernameToken usernameToken;

    public WssUsernameToken getUsernameToken() {
        return usernameToken;
    }

    public void setUsernameToken(WssUsernameToken usernameToken) {
        this.usernameToken = usernameToken;
    }

    public void readXmlAttributes(XmlNameMapProperties attributes) throws StaxerXmlStreamException {
    }

    public void readXmlContent(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        XmlName rootElement = xmlReader.getLastStartedElement();
        while (xmlReader.readNext() && !xmlReader.elementEnded(rootElement)) {
            if (xmlReader.elementStarted(XmlConstants.XML_NAME_WSS_USERNAME_TOKEN)) {
                usernameToken = XmlUtils.readXml(xmlReader, WssUsernameToken.class, XmlConstants.XML_NAME_WSS_USERNAME_TOKEN);
            }
        }
    }

    public void writeXmlAttributes(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
    }

    public void writeXmlContent(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        XmlUtils.writeXmlElement(xmlWriter, usernameToken, XmlConstants.XML_NAME_WSS_USERNAME_TOKEN);
    }

}
