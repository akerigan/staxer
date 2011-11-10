package comtech.staxer.domain;

import comtech.util.DateTimeUtils;
import comtech.util.xml.*;
import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;


/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 15.02.2010
 * Time: 17:32:22
 */
public class WssSoapHeader implements StaxerXmlWriter {

    public static final XmlName XML_NAME_SECURITY = new XmlName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");

    private static final String PASSWORD_DIGEST = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest";
    private static final String ENCODING_BASE64 = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary";

    private static Random random = new Random(System.currentTimeMillis());

    private String clientName;
    private String clientPassword;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPassword() {
        return clientPassword;
    }

    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    public void writeXmlAttributes(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
    }

    public void writeXmlContent(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        WssSecurity wssSecurity = new WssSecurity();

        WssUsernameToken wssUsernameToken = new WssUsernameToken();
        wssSecurity.setUsernameToken(wssUsernameToken);

        wssUsernameToken.setUserName(clientName);

        wssUsernameToken.setCreated(DateTimeUtils.formatDateTime2(new Date()));

        WssNonce wssNonce = new WssNonce();
        wssUsernameToken.setNonce(wssNonce);

        wssNonce.setEncodingType(ENCODING_BASE64);

        byte[] nonceValue = new byte[8];
        random.nextBytes(nonceValue);
        wssNonce.setValue(Base64.encodeBase64String(nonceValue));


        WssPassword wssPassword = new WssPassword();
        wssUsernameToken.setPassword(wssPassword);

        wssPassword.setType(PASSWORD_DIGEST);

        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new StaxerXmlStreamException(e);
        }
        sha.update(nonceValue);
        sha.update(wssUsernameToken.getCreated().getBytes());
        sha.update(clientPassword.getBytes());
        wssPassword.setValue(Base64.encodeBase64String(sha.digest()).trim());

        xmlWriter.startElement(XmlConstants.XML_NAME_SOAP_ENVELOPE_HEADER);
        XmlUtils.writeXmlElement(xmlWriter, wssSecurity, XmlConstants.XML_NAME_WSS_SECURITY);
        xmlWriter.endElement();
    }

}
