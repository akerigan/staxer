package org.staxer.sample.bean;

import org.staxer.util.date.DateTimeUtils;
import org.staxer.util.NumberUtils;
import org.staxer.util.StringUtils;
import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.StaxerXmlStreamException;
import org.staxer.util.xml.StaxerXmlStreamReader;
import org.staxer.util.xml.StaxerXmlStreamWriter;
import org.staxer.util.xml.XmlName;
import org.apache.commons.codec.binary.Base64;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Date;

@XmlRootElement(name = "echoXsdTypesResponse", namespace = "http://sample.staxer.org/")
@XmlAccessorType(XmlAccessType.FIELD)
public class EchoXsdTypesResponse extends XsdTypes {

    public static final XmlName XML_NAME_NILL_ELEM_STRING = new XmlName("http://sample.staxer.org/", "nillElemString");
    public static final XmlName XML_NAME_NILL_ELEM_INTEGER = new XmlName("http://sample.staxer.org/", "nillElemInteger");
    public static final XmlName XML_NAME_NILL_ELEM_CHARACTER = new XmlName("http://sample.staxer.org/", "nillElemCharacter");
    public static final XmlName XML_NAME_NILL_ELEM_FLOAT = new XmlName("http://sample.staxer.org/", "nillElemFloat");
    public static final XmlName XML_NAME_NILL_ELEM_DOUBLE = new XmlName("http://sample.staxer.org/", "nillElemDouble");
    public static final XmlName XML_NAME_NILL_ELEM_DECIMAL = new XmlName("http://sample.staxer.org/", "nillElemDecimal");
    public static final XmlName XML_NAME_NILL_ELEM_DATE_TIME = new XmlName("http://sample.staxer.org/", "nillElemDateTime");
    public static final XmlName XML_NAME_NILL_ELEM_BOOLEAN = new XmlName("http://sample.staxer.org/", "nillElemBoolean");
    public static final XmlName XML_NAME_NILL_ELEM_BASE64_BINARY = new XmlName("http://sample.staxer.org/", "nillElemBase64Binary");

    @XmlElement(name = "nillElemString", namespace = "http://sample.staxer.org/", nillable = true)
    private String nillElemString;

    @XmlElement(name = "nillElemInteger", namespace = "http://sample.staxer.org/", nillable = true)
    private Integer nillElemInteger;

    @XmlElement(name = "nillElemCharacter", namespace = "http://sample.staxer.org/", nillable = true)
    private Integer nillElemCharacter;

    @XmlElement(name = "nillElemFloat", namespace = "http://sample.staxer.org/", nillable = true)
    private Float nillElemFloat;

    @XmlElement(name = "nillElemDouble", namespace = "http://sample.staxer.org/", nillable = true)
    private Double nillElemDouble;

    @XmlElement(name = "nillElemDecimal", namespace = "http://sample.staxer.org/", nillable = true)
    private BigDecimal nillElemDecimal;

    @XmlElement(name = "nillElemDateTime", namespace = "http://sample.staxer.org/", nillable = true)
    @XmlSchemaType(name = "dateTime")
    private Date nillElemDateTime;

    @XmlElement(name = "nillElemBoolean", namespace = "http://sample.staxer.org/", nillable = true)
    private Boolean nillElemBoolean;

    @XmlElement(name = "nillElemBase64Binary", namespace = "http://sample.staxer.org/", nillable = true)
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
    public boolean readXmlContentElement(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_STRING)) {
            nillElemString = xmlReader.readCharacters(XML_NAME_NILL_ELEM_STRING);
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_INTEGER)) {
            nillElemInteger = NumberUtils.parseInteger(xmlReader.readCharacters(XML_NAME_NILL_ELEM_INTEGER));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_CHARACTER)) {
            nillElemCharacter = NumberUtils.parseInteger(xmlReader.readCharacters(XML_NAME_NILL_ELEM_CHARACTER));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_FLOAT)) {
            nillElemFloat = NumberUtils.parseFloat(xmlReader.readCharacters(XML_NAME_NILL_ELEM_FLOAT));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_DOUBLE)) {
            nillElemDouble = NumberUtils.parseDouble(xmlReader.readCharacters(XML_NAME_NILL_ELEM_DOUBLE));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_DECIMAL)) {
            nillElemDecimal = NumberUtils.parseBigDecimal(xmlReader.readCharacters(XML_NAME_NILL_ELEM_DECIMAL));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_DATE_TIME)) {
            nillElemDateTime = DateTimeUtils.parseXmlDate(xmlReader.readCharacters(XML_NAME_NILL_ELEM_DATE_TIME));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_BOOLEAN)) {
            nillElemBoolean = StringUtils.parseBooleanInstance(xmlReader.readCharacters(XML_NAME_NILL_ELEM_BOOLEAN));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_BASE64_BINARY)) {
            nillElemBase64Binary = Base64.decodeBase64(xmlReader.readCharacters(XML_NAME_NILL_ELEM_BASE64_BINARY));
            return true;
        }
        return super.readXmlContentElement(xmlReader);
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
