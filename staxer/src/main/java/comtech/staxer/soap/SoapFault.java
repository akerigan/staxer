package comtech.staxer.soap;

import comtech.util.xml.ReadXml;
import comtech.util.xml.XmlName;
import comtech.util.xml.read.DocumentXmlStreamReader2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLStreamException;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 15.09.2009
 * Time: 15:11:15
 */
@XmlRootElement(name = "Fault", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
@XmlAccessorType(XmlAccessType.FIELD)
public class SoapFault implements ReadXml {

    public static final XmlName QNAME_FAULTCODE = new XmlName("faultcode");
    public static final XmlName QNAME_FAULTSTRING = new XmlName("faultstring");
    public static final XmlName QNAME_FAULTACTOR = new XmlName("faultactor");
    public static final XmlName QNAME_DETAIL = new XmlName("detail");

    @XmlElement(name = "faultcode")
    private String code;

    @XmlElement(name = "faultstring")
    private String string;

    @XmlElement(name = "faultactor")
    private String actor;

    @XmlElement(name = "detail")
    private SoapFaultDetail detail;

    public SoapFault() {
    }

    public SoapFault(String code, String string) {
        this.code = code;
        this.string = string;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public SoapFaultDetail getDetail() {
        return detail;
    }

    public void setDetail(SoapFaultDetail detail) {
        this.detail = detail;
    }

    public void readXml(DocumentXmlStreamReader2 reader, XmlName elementName) throws XMLStreamException {
        while (reader.readNext()) {
            if (reader.elementEnded(elementName)) {
                break;
            } else if (reader.elementStarted(QNAME_FAULTCODE)) {
                code = reader.readCharacters(QNAME_FAULTCODE);
            } else if (reader.elementStarted(QNAME_FAULTSTRING)) {
                string = reader.readCharacters(QNAME_FAULTSTRING);
            } else if (reader.elementStarted(QNAME_FAULTACTOR)) {
                actor = reader.readCharacters(QNAME_FAULTACTOR);
            } else if (reader.elementStarted(QNAME_DETAIL)) {
                detail = new SoapFaultDetail();
                detail.readXml(reader, QNAME_DETAIL);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<SoapFault>\n");
        sb.append("<code>");
        sb.append(code);
        sb.append("</code>\n");
        sb.append("<string>");
        sb.append(string);
        sb.append("</string>\n");
        sb.append("<actor>");
        sb.append(actor);
        sb.append("</actor>\n");
        sb.append("<detail>");
        sb.append(detail);
        sb.append("</detail>\n");
        sb.append("</SoapFault>\n");

        return sb.toString();
    }
}
