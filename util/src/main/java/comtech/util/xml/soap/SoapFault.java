package comtech.util.xml.soap;

import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 15.09.2009
 * Time: 15:11:15
 */
@XmlRootElement(name = "Fault", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
@XmlAccessorType(XmlAccessType.FIELD)
public class SoapFault implements StaxerReadXml, StaxerWriteXml {

    public static final XmlName XML_NAME_FAULTCODE = new XmlName("faultcode");
    public static final XmlName XML_NAME_FAULTSTRING = new XmlName("faultstring");
    public static final XmlName XML_NAME_FAULTACTOR = new XmlName("faultactor");
    public static final XmlName XML_NAME_DETAIL = new XmlName("detail");

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

    public void readXmlAttributes(XmlNameMapProperties attributes) throws StaxerXmlStreamException {
    }

    public void readXmlContent(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        XmlName rootElementName = xmlReader.getLastStartedElement();
        while (xmlReader.readNext() && !xmlReader.elementEnded(rootElementName)) {
            if (xmlReader.elementStarted(XML_NAME_FAULTCODE)) {
                code = xmlReader.readCharacters(XML_NAME_FAULTCODE);
            } else if (xmlReader.elementStarted(XML_NAME_FAULTSTRING)) {
                string = xmlReader.readCharacters(XML_NAME_FAULTSTRING);
            } else if (xmlReader.elementStarted(XML_NAME_FAULTACTOR)) {
                actor = xmlReader.readCharacters(XML_NAME_FAULTACTOR);
            } else if (xmlReader.elementStarted(XML_NAME_DETAIL)) {
                detail = XmlUtils.readXml(xmlReader, SoapFaultDetail.class, XML_NAME_DETAIL);
            }
        }
    }

    public void writeXmlAttributes(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
    }

    public void writeXmlContent(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        xmlWriter.element(XML_NAME_FAULTCODE, code);
        xmlWriter.element(XML_NAME_FAULTSTRING, string);
        xmlWriter.element(XML_NAME_FAULTACTOR, actor);
        XmlUtils.writeXmlElement(xmlWriter, detail, XML_NAME_DETAIL);
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
