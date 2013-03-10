package org.staxer.util.xml.soap;

import org.staxer.util.json.*;
import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.*;

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
public class SoapFault implements StaxerReadXml, StaxerWriteXml, StaxerReadJson, StaxerWriteJson {

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

    public void readJson(StaxerJsonStreamReader jsonReader) throws StaxerJsonStreamException {
        if (jsonReader.readObjectStart()) {
            int rootLevel = jsonReader.getLevel() + 1;
            while (jsonReader.readNext() && !jsonReader.objectEnded(rootLevel)) {
                String fieldName = jsonReader.readFieldName(rootLevel);
                if (XML_NAME_FAULTCODE.getLocalPart().equals(fieldName)) {
                    if (jsonReader.readFieldValue(rootLevel)) {
                        code = jsonReader.getStringValue();
                    }
                } else if (XML_NAME_FAULTSTRING.getLocalPart().equals(fieldName)) {
                    if (jsonReader.readFieldValue(rootLevel)) {
                        string = jsonReader.getStringValue();
                    }
                } else if (XML_NAME_FAULTACTOR.getLocalPart().equals(fieldName)) {
                    if (jsonReader.readFieldValue(rootLevel)) {
                        actor = jsonReader.getStringValue();
                    }
                } else if (XML_NAME_DETAIL.getLocalPart().equals(fieldName)) {
                    if (jsonReader.readObjectFieldValue(rootLevel)) {
                        detail = JsonUtils.readJson(jsonReader, SoapFaultDetail.class);
                    }
                }
            }
        }
    }

    public void writeJson(StaxerJsonStreamWriter jsonWriter) throws StaxerJsonStreamException {
        jsonWriter.startObject();
        jsonWriter.objectEntry(XML_NAME_FAULTCODE.getLocalPart(), code);
        jsonWriter.objectEntry(XML_NAME_DETAIL.getLocalPart(), detail);
        jsonWriter.objectEntry(XML_NAME_FAULTSTRING.getLocalPart(), string);
        jsonWriter.objectEntry(XML_NAME_FAULTACTOR.getLocalPart(), actor);
        jsonWriter.endObject();
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
