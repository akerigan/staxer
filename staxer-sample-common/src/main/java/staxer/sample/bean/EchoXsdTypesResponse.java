package staxer.sample.bean;

import comtech.util.DateTimeUtils;
import comtech.util.NumberUtils;
import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.StaxerXmlStreamException;
import comtech.util.xml.StaxerXmlStreamReader;
import comtech.util.xml.StaxerXmlStreamWriter;
import comtech.util.xml.XmlName;
import org.apache.commons.codec.binary.Base64;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Date;

@XmlRootElement(name = "echoXsdTypesResponse", namespace = "http://staxer.sample/")
@XmlAccessorType(XmlAccessType.FIELD)
public class EchoXsdTypesResponse extends XsdTypes {

    public static final XmlName XML_NAME_NILL_ELEM_STRING = new XmlName("http://staxer.sample/", "nillElemString");
    public static final XmlName XML_NAME_NILL_ELEM_INTEGER = new XmlName("http://staxer.sample/", "nillElemInteger");
    public static final XmlName XML_NAME_NILL_ELEM_CHARACTER = new XmlName("http://staxer.sample/", "nillElemCharacter");
    public static final XmlName XML_NAME_NILL_ELEM_FLOAT = new XmlName("http://staxer.sample/", "nillElemFloat");
    public static final XmlName XML_NAME_NILL_ELEM_DOUBLE = new XmlName("http://staxer.sample/", "nillElemDouble");
    public static final XmlName XML_NAME_NILL_ELEM_DECIMAL = new XmlName("http://staxer.sample/", "nillElemDecimal");
    public static final XmlName XML_NAME_NILL_ELEM_DATE_TIME = new XmlName("http://staxer.sample/", "nillElemDateTime");
    public static final XmlName XML_NAME_NILL_ELEM_BOOLEAN = new XmlName("http://staxer.sample/", "nillElemBoolean");
    public static final XmlName XML_NAME_NILL_ELEM_BASE64_BINARY = new XmlName("http://staxer.sample/", "nillElemBase64Binary");

    @XmlElement(name = "nillElemString", namespace = "http://staxer.sample/", nillable = true)
    private String nillElemString;

    @XmlElement(name = "nillElemInteger", namespace = "http://staxer.sample/", nillable = true)
    private Integer nillElemInteger;

    @XmlElement(name = "nillElemCharacter", namespace = "http://staxer.sample/", nillable = true)
    private Integer nillElemCharacter;

    @XmlElement(name = "nillElemFloat", namespace = "http://staxer.sample/", nillable = true)
    private Float nillElemFloat;

    @XmlElement(name = "nillElemDouble", namespace = "http://staxer.sample/", nillable = true)
    private Double nillElemDouble;

    @XmlElement(name = "nillElemDecimal", namespace = "http://staxer.sample/", nillable = true)
    private BigDecimal nillElemDecimal;

    @XmlElement(name = "nillElemDateTime", namespace = "http://staxer.sample/", nillable = true)
    @XmlSchemaType(name = "dateTime")
    private Date nillElemDateTime;

    @XmlElement(name = "nillElemBoolean", namespace = "http://staxer.sample/", nillable = true)
    private Boolean nillElemBoolean;

    @XmlElement(name = "nillElemBase64Binary", namespace = "http://staxer.sample/", nillable = true)
    private byte[] nillElemBase64Binary;

    public String getNillElemString() {
        return nillElemString;
    }

    public void setNillElemString(String nillElemString) {
        this.nillElemString = nillElemString;
    }

    public Integer getNillElemInteger() {
        return nillElemInteger;
    }

    public void setNillElemInteger(Integer nillElemInteger) {
        this.nillElemInteger = nillElemInteger;
    }

    public Integer getNillElemCharacter() {
        return nillElemCharacter;
    }

    public void setNillElemCharacter(Integer nillElemCharacter) {
        this.nillElemCharacter = nillElemCharacter;
    }

    public Float getNillElemFloat() {
        return nillElemFloat;
    }

    public void setNillElemFloat(Float nillElemFloat) {
        this.nillElemFloat = nillElemFloat;
    }

    public Double getNillElemDouble() {
        return nillElemDouble;
    }

    public void setNillElemDouble(Double nillElemDouble) {
        this.nillElemDouble = nillElemDouble;
    }

    public BigDecimal getNillElemDecimal() {
        return nillElemDecimal;
    }

    public void setNillElemDecimal(BigDecimal nillElemDecimal) {
        this.nillElemDecimal = nillElemDecimal;
    }

    public Date getNillElemDateTime() {
        return nillElemDateTime;
    }

    public void setNillElemDateTime(Date nillElemDateTime) {
        this.nillElemDateTime = nillElemDateTime;
    }

    public Boolean getNillElemBoolean() {
        return nillElemBoolean;
    }

    public void setNillElemBoolean(Boolean nillElemBoolean) {
        this.nillElemBoolean = nillElemBoolean;
    }

    public byte[] getNillElemBase64Binary() {
        return nillElemBase64Binary;
    }

    public void setNillElemBase64Binary(byte[] nillElemBase64Binary) {
        this.nillElemBase64Binary = nillElemBase64Binary;
    }

    @Override
    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
        super.readXmlAttributes(attributes);
    }

    @Override
    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        super.readXmlContent(xmlReader);
        XmlName rootElementName = xmlReader.getLastStartedElement();
        while (xmlReader.readNext() && !xmlReader.elementEnded(rootElementName)) {
            if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_STRING)) {
                nillElemString = xmlReader.readCharacters(XML_NAME_NILL_ELEM_STRING);
            } else if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_INTEGER)) {
                nillElemInteger = NumberUtils.parseInteger(xmlReader.readCharacters(XML_NAME_NILL_ELEM_INTEGER));
            } else if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_CHARACTER)) {
                nillElemCharacter = NumberUtils.parseInteger(xmlReader.readCharacters(XML_NAME_NILL_ELEM_CHARACTER));
            } else if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_FLOAT)) {
                nillElemFloat = NumberUtils.parseFloat(xmlReader.readCharacters(XML_NAME_NILL_ELEM_FLOAT));
            } else if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_DOUBLE)) {
                nillElemDouble = NumberUtils.parseDouble(xmlReader.readCharacters(XML_NAME_NILL_ELEM_DOUBLE));
            } else if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_DECIMAL)) {
                nillElemDecimal = NumberUtils.parseBigDecimal(xmlReader.readCharacters(XML_NAME_NILL_ELEM_DECIMAL));
            } else if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_DATE_TIME)) {
                nillElemDateTime = DateTimeUtils.parseXmlDate(xmlReader.readCharacters(XML_NAME_NILL_ELEM_DATE_TIME));
            } else if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_BOOLEAN)) {
                nillElemBoolean = Boolean.parseBoolean(xmlReader.readCharacters(XML_NAME_NILL_ELEM_BOOLEAN));
            } else if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_BASE64_BINARY)) {
                nillElemBase64Binary = Base64.decodeBase64(xmlReader.readCharacters(XML_NAME_NILL_ELEM_BASE64_BINARY));
            }
        }
    }

    @Override
    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        xmlWriter.declareNamespace("http://www.w3.org/2001/XMLSchema-instance");
        super.writeXmlAttributes(xmlWriter);
    }

    @Override
    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        super.writeXmlContent(xmlWriter);
        xmlWriter.element(XML_NAME_NILL_ELEM_STRING, nillElemString, true);
        xmlWriter.element(XML_NAME_NILL_ELEM_INTEGER, nillElemInteger, true);
        xmlWriter.element(XML_NAME_NILL_ELEM_CHARACTER, nillElemCharacter, true);
        xmlWriter.element(XML_NAME_NILL_ELEM_FLOAT, nillElemFloat, true);
        xmlWriter.element(XML_NAME_NILL_ELEM_DOUBLE, nillElemDouble, true);
        xmlWriter.element(XML_NAME_NILL_ELEM_DECIMAL, nillElemDecimal, true);
        xmlWriter.element(XML_NAME_NILL_ELEM_DATE_TIME, DateTimeUtils.formatXmlDate(nillElemDateTime), true);
        xmlWriter.element(XML_NAME_NILL_ELEM_BOOLEAN, nillElemBoolean, true);
        xmlWriter.element(XML_NAME_NILL_ELEM_BASE64_BINARY, Base64.encodeBase64String(nillElemBase64Binary), true);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<EchoXsdTypesResponse>\n");
        toString(sb);
        sb.append("</EchoXsdTypesResponse>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("<nillElemString>");
        sb.append(nillElemString);
        sb.append("</nillElemString>\n");
        sb.append("<nillElemInteger>");
        sb.append(nillElemInteger);
        sb.append("</nillElemInteger>\n");
        sb.append("<nillElemCharacter>");
        sb.append(nillElemCharacter);
        sb.append("</nillElemCharacter>\n");
        sb.append("<nillElemFloat>");
        sb.append(nillElemFloat);
        sb.append("</nillElemFloat>\n");
        sb.append("<nillElemDouble>");
        sb.append(nillElemDouble);
        sb.append("</nillElemDouble>\n");
        sb.append("<nillElemDecimal>");
        sb.append(nillElemDecimal);
        sb.append("</nillElemDecimal>\n");
        sb.append("<nillElemDateTime>");
        sb.append(nillElemDateTime);
        sb.append("</nillElemDateTime>\n");
        sb.append("<nillElemBoolean>");
        sb.append(nillElemBoolean);
        sb.append("</nillElemBoolean>\n");
        sb.append("<nillElemBase64Binary>");
        sb.append(nillElemBase64Binary);
        sb.append("</nillElemBase64Binary>\n");
    }

}
