package comtech.staxer.soap;

import comtech.staxer.domain.wss.WssNonce;
import comtech.staxer.domain.wss.WssPassword;
import comtech.staxer.domain.wss.WssSecurity;
import comtech.staxer.domain.wss.WssUsernameToken;
import comtech.util.date.DateTimeUtils;
import comtech.util.xml.XmlConstants;
import comtech.util.xml.write.DocumentXmlStreamWriter;
import comtech.util.xml.write.DocumentXmlStreamWriter2;
import org.apache.commons.codec.binary.Base64;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import static comtech.util.xml.XmlConstants.*;


/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 15.02.2010
 * Time: 17:32:22
 */
public class WssSoapHeader implements SoapHeader {

    private static final String PASSWORD_DIGEST = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest";
    private static final String ENCODING_BASE64 = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary";

    private static final QName SECURITY = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");

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

    public void write(DocumentXmlStreamWriter document) throws XMLStreamException, NoSuchAlgorithmException {

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

        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        sha.update(nonceValue);
        sha.update(wssUsernameToken.getCreated().getBytes());
        sha.update(clientPassword.getBytes());
        wssPassword.setValue(Base64.encodeBase64String(sha.digest()).trim());

        document.startElement(NAMESPACE_PREFIX_SOAP, SOAP_HEADER, NAMESPACE_URI_SOAP_ENVELOPE);
        document.object(wssSecurity);
        document.endElement();
    }

    public void write(DocumentXmlStreamWriter2 document) throws NoSuchAlgorithmException, IOException {

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

        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        sha.update(nonceValue);
        sha.update(wssUsernameToken.getCreated().getBytes());
        sha.update(clientPassword.getBytes());
        wssPassword.setValue(Base64.encodeBase64String(sha.digest()).trim());

        wssSecurity.writeXml(document, XmlConstants.XML_NAME_SOAP_ENVELOPE_HEADER);
    }

}
