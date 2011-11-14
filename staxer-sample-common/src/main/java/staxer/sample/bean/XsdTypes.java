package staxer.sample.bean;

import comtech.util.DateTimeUtils;
import comtech.util.NumberUtils;
import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.*;
import org.apache.commons.codec.binary.Base64;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
public class XsdTypes implements StaxerReadXml, StaxerWriteXml {

    public static final XmlName XML_NAME_ELEM_STRING = new XmlName("http://staxer.sample/", "elemString");
    public static final XmlName XML_NAME_ELEM_INTEGER = new XmlName("http://staxer.sample/", "elemInteger");
    public static final XmlName XML_NAME_ELEM_CHARACTER = new XmlName("http://staxer.sample/", "elemCharacter");
    public static final XmlName XML_NAME_ELEM_FLOAT = new XmlName("http://staxer.sample/", "elemFloat");
    public static final XmlName XML_NAME_ELEM_DOUBLE = new XmlName("http://staxer.sample/", "elemDouble");
    public static final XmlName XML_NAME_ELEM_DECIMAL = new XmlName("http://staxer.sample/", "elemDecimal");
    public static final XmlName XML_NAME_ELEM_DATE_TIME = new XmlName("http://staxer.sample/", "elemDateTime");
    public static final XmlName XML_NAME_ELEM_BOOLEAN = new XmlName("http://staxer.sample/", "elemBoolean");
    public static final XmlName XML_NAME_ELEM_BASE64_BINARY = new XmlName("http://staxer.sample/", "elemBase64Binary");
    public static final XmlName XML_NAME_LST_STRING = new XmlName("http://staxer.sample/", "lstString");
    public static final XmlName XML_NAME_LST_INTEGER = new XmlName("http://staxer.sample/", "lstInteger");
    public static final XmlName XML_NAME_LST_CHARACTER = new XmlName("http://staxer.sample/", "lstCharacter");
    public static final XmlName XML_NAME_LST_FLOAT = new XmlName("http://staxer.sample/", "lstFloat");
    public static final XmlName XML_NAME_LST_DOUBLE = new XmlName("http://staxer.sample/", "lstDouble");
    public static final XmlName XML_NAME_LST_DECIMAL = new XmlName("http://staxer.sample/", "lstDecimal");
    public static final XmlName XML_NAME_LST_DATE_TIME = new XmlName("http://staxer.sample/", "lstDateTime");
    public static final XmlName XML_NAME_LST_BOOLEAN = new XmlName("http://staxer.sample/", "lstBoolean");
    public static final XmlName XML_NAME_LST_BASE64_BINARY = new XmlName("http://staxer.sample/", "lstBase64Binary");
    public static final XmlName XML_NAME_ATT_STRING = new XmlName("http://staxer.sample/", "attString");
    public static final XmlName XML_NAME_ATT_INTEGER = new XmlName("http://staxer.sample/", "attInteger");
    public static final XmlName XML_NAME_ATT_CHARACTER = new XmlName("http://staxer.sample/", "attCharacter");
    public static final XmlName XML_NAME_ATT_FLOAT = new XmlName("http://staxer.sample/", "attFloat");
    public static final XmlName XML_NAME_ATT_DOUBLE = new XmlName("http://staxer.sample/", "attDouble");
    public static final XmlName XML_NAME_ATT_DECIMAL = new XmlName("http://staxer.sample/", "attDecimal");
    public static final XmlName XML_NAME_ATT_DATE_TIME = new XmlName("http://staxer.sample/", "attDateTime");
    public static final XmlName XML_NAME_ATT_BOOLEAN = new XmlName("http://staxer.sample/", "attBoolean");
    public static final XmlName XML_NAME_ATT_BASE64_BINARY = new XmlName("http://staxer.sample/", "attBase64Binary");

    @XmlElement(name = "elemString", namespace = "http://staxer.sample/")
    private String elemString;

    @XmlElement(name = "elemInteger", namespace = "http://staxer.sample/")
    private Integer elemInteger;

    @XmlElement(name = "elemCharacter", namespace = "http://staxer.sample/")
    private Integer elemCharacter;

    @XmlElement(name = "elemFloat", namespace = "http://staxer.sample/")
    private Float elemFloat;

    @XmlElement(name = "elemDouble", namespace = "http://staxer.sample/")
    private Double elemDouble;

    @XmlElement(name = "elemDecimal", namespace = "http://staxer.sample/")
    private BigDecimal elemDecimal;

    @XmlElement(name = "elemDateTime", namespace = "http://staxer.sample/")
    @XmlSchemaType(name = "dateTime")
    private Date elemDateTime;

    @XmlElement(name = "elemBoolean", namespace = "http://staxer.sample/")
    private Boolean elemBoolean;

    @XmlElement(name = "elemBase64Binary", namespace = "http://staxer.sample/")
    private byte[] elemBase64Binary;

    @XmlElement(name = "lstString", namespace = "http://staxer.sample/")
    private ArrayList<String> lstString = new ArrayList<String>();

    @XmlElement(name = "lstInteger", namespace = "http://staxer.sample/")
    private ArrayList<Integer> lstInteger = new ArrayList<Integer>();

    @XmlElement(name = "lstCharacter", namespace = "http://staxer.sample/")
    private ArrayList<Integer> lstCharacter = new ArrayList<Integer>();

    @XmlElement(name = "lstFloat", namespace = "http://staxer.sample/")
    private ArrayList<Float> lstFloat = new ArrayList<Float>();

    @XmlElement(name = "lstDouble", namespace = "http://staxer.sample/")
    private ArrayList<Double> lstDouble = new ArrayList<Double>();

    @XmlElement(name = "lstDecimal", namespace = "http://staxer.sample/")
    private ArrayList<BigDecimal> lstDecimal = new ArrayList<BigDecimal>();

    @XmlElement(name = "lstDateTime", namespace = "http://staxer.sample/")
    @XmlSchemaType(name = "dateTime")
    private ArrayList<Date> lstDateTime = new ArrayList<Date>();

    @XmlElement(name = "lstBoolean", namespace = "http://staxer.sample/")
    private ArrayList<Boolean> lstBoolean = new ArrayList<Boolean>();

    @XmlElement(name = "lstBase64Binary", namespace = "http://staxer.sample/")
    private ArrayList<byte[]> lstBase64Binary = new ArrayList<byte[]>();

    @XmlAttribute(name = "attString", namespace = "http://staxer.sample/")
    private String attString;

    @XmlAttribute(name = "attInteger", namespace = "http://staxer.sample/")
    private Integer attInteger;

    @XmlAttribute(name = "attCharacter", namespace = "http://staxer.sample/")
    private Integer attCharacter;

    @XmlAttribute(name = "attFloat", namespace = "http://staxer.sample/")
    private Float attFloat;

    @XmlAttribute(name = "attDouble", namespace = "http://staxer.sample/")
    private Double attDouble;

    @XmlAttribute(name = "attDecimal", namespace = "http://staxer.sample/")
    private BigDecimal attDecimal;

    @XmlAttribute(name = "attDateTime", namespace = "http://staxer.sample/")
    @XmlSchemaType(name = "dateTime")
    private Date attDateTime;

    @XmlAttribute(name = "attBoolean", namespace = "http://staxer.sample/")
    private Boolean attBoolean;

    @XmlAttribute(name = "attBase64Binary", namespace = "http://staxer.sample/")
    private byte[] attBase64Binary;

    public String getElemString() {
        return elemString;
    }

    public void setElemString(String elemString) {
        this.elemString = elemString;
    }

    public Integer getElemInteger() {
        return elemInteger;
    }

    public void setElemInteger(Integer elemInteger) {
        this.elemInteger = elemInteger;
    }

    public Integer getElemCharacter() {
        return elemCharacter;
    }

    public void setElemCharacter(Integer elemCharacter) {
        this.elemCharacter = elemCharacter;
    }

    public Float getElemFloat() {
        return elemFloat;
    }

    public void setElemFloat(Float elemFloat) {
        this.elemFloat = elemFloat;
    }

    public Double getElemDouble() {
        return elemDouble;
    }

    public void setElemDouble(Double elemDouble) {
        this.elemDouble = elemDouble;
    }

    public BigDecimal getElemDecimal() {
        return elemDecimal;
    }

    public void setElemDecimal(BigDecimal elemDecimal) {
        this.elemDecimal = elemDecimal;
    }

    public Date getElemDateTime() {
        return elemDateTime;
    }

    public void setElemDateTime(Date elemDateTime) {
        this.elemDateTime = elemDateTime;
    }

    public Boolean getElemBoolean() {
        return elemBoolean;
    }

    public void setElemBoolean(Boolean elemBoolean) {
        this.elemBoolean = elemBoolean;
    }

    public byte[] getElemBase64Binary() {
        return elemBase64Binary;
    }

    public void setElemBase64Binary(byte[] elemBase64Binary) {
        this.elemBase64Binary = elemBase64Binary;
    }

    public ArrayList<String> getLstString() {
        return lstString;
    }

    public ArrayList<Integer> getLstInteger() {
        return lstInteger;
    }

    public ArrayList<Integer> getLstCharacter() {
        return lstCharacter;
    }

    public ArrayList<Float> getLstFloat() {
        return lstFloat;
    }

    public ArrayList<Double> getLstDouble() {
        return lstDouble;
    }

    public ArrayList<BigDecimal> getLstDecimal() {
        return lstDecimal;
    }

    public ArrayList<Date> getLstDateTime() {
        return lstDateTime;
    }

    public ArrayList<Boolean> getLstBoolean() {
        return lstBoolean;
    }

    public ArrayList<byte[]> getLstBase64Binary() {
        return lstBase64Binary;
    }

    public String getAttString() {
        return attString;
    }

    public void setAttString(String attString) {
        this.attString = attString;
    }

    public Integer getAttInteger() {
        return attInteger;
    }

    public void setAttInteger(Integer attInteger) {
        this.attInteger = attInteger;
    }

    public Integer getAttCharacter() {
        return attCharacter;
    }

    public void setAttCharacter(Integer attCharacter) {
        this.attCharacter = attCharacter;
    }

    public Float getAttFloat() {
        return attFloat;
    }

    public void setAttFloat(Float attFloat) {
        this.attFloat = attFloat;
    }

    public Double getAttDouble() {
        return attDouble;
    }

    public void setAttDouble(Double attDouble) {
        this.attDouble = attDouble;
    }

    public BigDecimal getAttDecimal() {
        return attDecimal;
    }

    public void setAttDecimal(BigDecimal attDecimal) {
        this.attDecimal = attDecimal;
    }

    public Date getAttDateTime() {
        return attDateTime;
    }

    public void setAttDateTime(Date attDateTime) {
        this.attDateTime = attDateTime;
    }

    public Boolean getAttBoolean() {
        return attBoolean;
    }

    public void setAttBoolean(Boolean attBoolean) {
        this.attBoolean = attBoolean;
    }

    public byte[] getAttBase64Binary() {
        return attBase64Binary;
    }

    public void setAttBase64Binary(byte[] attBase64Binary) {
        this.attBase64Binary = attBase64Binary;
    }

    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
        attString = attributes.get(XML_NAME_ATT_STRING);
        attInteger = NumberUtils.parseInteger(attributes.get(XML_NAME_ATT_INTEGER));
        attCharacter = NumberUtils.parseInteger(attributes.get(XML_NAME_ATT_CHARACTER));
        attFloat = NumberUtils.parseFloat(attributes.get(XML_NAME_ATT_FLOAT));
        attDouble = NumberUtils.parseDouble(attributes.get(XML_NAME_ATT_DOUBLE));
        attDecimal = NumberUtils.parseBigDecimal(attributes.get(XML_NAME_ATT_DECIMAL));
        attDateTime = DateTimeUtils.parseXmlDate(attributes.get(XML_NAME_ATT_DATE_TIME));
        attBoolean = Boolean.parseBoolean(attributes.get(XML_NAME_ATT_BOOLEAN));
        attBase64Binary = Base64.decodeBase64(attributes.get(XML_NAME_ATT_BASE64_BINARY));
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        XmlName rootElementName = xmlReader.getLastStartedElement();
        while (xmlReader.readNext() && !xmlReader.elementEnded(rootElementName)) {
            readXmlContentElement(xmlReader);
        }
    }

    public boolean readXmlContentElement(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        if (xmlReader.elementStarted(XML_NAME_ELEM_STRING)) {
            elemString = xmlReader.readCharacters(XML_NAME_ELEM_STRING);
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_ELEM_INTEGER)) {
            elemInteger = NumberUtils.parseInteger(xmlReader.readCharacters(XML_NAME_ELEM_INTEGER));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_ELEM_CHARACTER)) {
            elemCharacter = NumberUtils.parseInteger(xmlReader.readCharacters(XML_NAME_ELEM_CHARACTER));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_ELEM_FLOAT)) {
            elemFloat = NumberUtils.parseFloat(xmlReader.readCharacters(XML_NAME_ELEM_FLOAT));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_ELEM_DOUBLE)) {
            elemDouble = NumberUtils.parseDouble(xmlReader.readCharacters(XML_NAME_ELEM_DOUBLE));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_ELEM_DECIMAL)) {
            elemDecimal = NumberUtils.parseBigDecimal(xmlReader.readCharacters(XML_NAME_ELEM_DECIMAL));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_ELEM_DATE_TIME)) {
            elemDateTime = DateTimeUtils.parseXmlDate(xmlReader.readCharacters(XML_NAME_ELEM_DATE_TIME));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_ELEM_BOOLEAN)) {
            elemBoolean = Boolean.parseBoolean(xmlReader.readCharacters(XML_NAME_ELEM_BOOLEAN));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_ELEM_BASE64_BINARY)) {
            elemBase64Binary = Base64.decodeBase64(xmlReader.readCharacters(XML_NAME_ELEM_BASE64_BINARY));
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_LST_STRING)) {
            String lstStringItem = xmlReader.readCharacters(XML_NAME_LST_STRING);
            if (lstStringItem != null) {
                lstString.add(lstStringItem);
            }
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_LST_INTEGER)) {
            Integer lstIntegerItem = NumberUtils.parseInteger(xmlReader.readCharacters(XML_NAME_LST_INTEGER));
            if (lstIntegerItem != null) {
                lstInteger.add(lstIntegerItem);
            }
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_LST_CHARACTER)) {
            Integer lstCharacterItem = NumberUtils.parseInteger(xmlReader.readCharacters(XML_NAME_LST_CHARACTER));
            if (lstCharacterItem != null) {
                lstCharacter.add(lstCharacterItem);
            }
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_LST_FLOAT)) {
            Float lstFloatItem = NumberUtils.parseFloat(xmlReader.readCharacters(XML_NAME_LST_FLOAT));
            if (lstFloatItem != null) {
                lstFloat.add(lstFloatItem);
            }
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_LST_DOUBLE)) {
            Double lstDoubleItem = NumberUtils.parseDouble(xmlReader.readCharacters(XML_NAME_LST_DOUBLE));
            if (lstDoubleItem != null) {
                lstDouble.add(lstDoubleItem);
            }
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_LST_DECIMAL)) {
            BigDecimal lstDecimalItem = NumberUtils.parseBigDecimal(xmlReader.readCharacters(XML_NAME_LST_DECIMAL));
            if (lstDecimalItem != null) {
                lstDecimal.add(lstDecimalItem);
            }
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_LST_DATE_TIME)) {
            Date lstDateTimeItem = DateTimeUtils.parseXmlDate(xmlReader.readCharacters(XML_NAME_LST_DATE_TIME));
            if (lstDateTimeItem != null) {
                lstDateTime.add(lstDateTimeItem);
            }
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_LST_BOOLEAN)) {
            Boolean lstBooleanItem = Boolean.parseBoolean(xmlReader.readCharacters(XML_NAME_LST_BOOLEAN));
            if (lstBooleanItem != null) {
                lstBoolean.add(lstBooleanItem);
            }
            return true;
        }
        if (xmlReader.elementStarted(XML_NAME_LST_BASE64_BINARY)) {
            byte[] lstBase64BinaryItem = Base64.decodeBase64(xmlReader.readCharacters(XML_NAME_LST_BASE64_BINARY));
            if (lstBase64BinaryItem != null) {
                lstBase64Binary.add(lstBase64BinaryItem);
            }
            return true;
        }
        return false;
    }

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        xmlWriter.attribute(XML_NAME_ATT_STRING, attString);
        xmlWriter.attribute(XML_NAME_ATT_INTEGER, attInteger);
        xmlWriter.attribute(XML_NAME_ATT_CHARACTER, attCharacter);
        xmlWriter.attribute(XML_NAME_ATT_FLOAT, attFloat);
        xmlWriter.attribute(XML_NAME_ATT_DOUBLE, attDouble);
        xmlWriter.attribute(XML_NAME_ATT_DECIMAL, attDecimal);
        xmlWriter.attribute(XML_NAME_ATT_DATE_TIME, DateTimeUtils.formatXmlDate(attDateTime));
        xmlWriter.attribute(XML_NAME_ATT_BOOLEAN, attBoolean);
        xmlWriter.attribute(XML_NAME_ATT_BASE64_BINARY, Base64.encodeBase64String(attBase64Binary));
    }

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        xmlWriter.element(XML_NAME_ELEM_STRING, elemString, false);
        xmlWriter.element(XML_NAME_ELEM_INTEGER, elemInteger, false);
        xmlWriter.element(XML_NAME_ELEM_CHARACTER, elemCharacter, false);
        xmlWriter.element(XML_NAME_ELEM_FLOAT, elemFloat, false);
        xmlWriter.element(XML_NAME_ELEM_DOUBLE, elemDouble, false);
        xmlWriter.element(XML_NAME_ELEM_DECIMAL, elemDecimal, false);
        xmlWriter.element(XML_NAME_ELEM_DATE_TIME, DateTimeUtils.formatXmlDate(elemDateTime), false);
        xmlWriter.element(XML_NAME_ELEM_BOOLEAN, elemBoolean, false);
        xmlWriter.element(XML_NAME_ELEM_BASE64_BINARY, Base64.encodeBase64String(elemBase64Binary), false);
        if (!lstString.isEmpty()) {
            for (String lstStringItem : lstString) {
                xmlWriter.element(XML_NAME_LST_STRING, lstStringItem, false);
            }
        }
        if (!lstInteger.isEmpty()) {
            for (Integer lstIntegerItem : lstInteger) {
                xmlWriter.element(XML_NAME_LST_INTEGER, lstIntegerItem, false);
            }
        }
        if (!lstCharacter.isEmpty()) {
            for (Integer lstCharacterItem : lstCharacter) {
                xmlWriter.element(XML_NAME_LST_CHARACTER, lstCharacterItem, false);
            }
        }
        if (!lstFloat.isEmpty()) {
            for (Float lstFloatItem : lstFloat) {
                xmlWriter.element(XML_NAME_LST_FLOAT, lstFloatItem, false);
            }
        }
        if (!lstDouble.isEmpty()) {
            for (Double lstDoubleItem : lstDouble) {
                xmlWriter.element(XML_NAME_LST_DOUBLE, lstDoubleItem, false);
            }
        }
        if (!lstDecimal.isEmpty()) {
            for (BigDecimal lstDecimalItem : lstDecimal) {
                xmlWriter.element(XML_NAME_LST_DECIMAL, lstDecimalItem, false);
            }
        }
        if (!lstDateTime.isEmpty()) {
            for (Date lstDateTimeItem : lstDateTime) {
                xmlWriter.element(XML_NAME_LST_DATE_TIME, DateTimeUtils.formatXmlDate(lstDateTimeItem), false);
            }
        }
        if (!lstBoolean.isEmpty()) {
            for (Boolean lstBooleanItem : lstBoolean) {
                xmlWriter.element(XML_NAME_LST_BOOLEAN, lstBooleanItem, false);
            }
        }
        if (!lstBase64Binary.isEmpty()) {
            for (byte[] lstBase64BinaryItem : lstBase64Binary) {
                xmlWriter.element(XML_NAME_LST_BASE64_BINARY, Base64.encodeBase64String(lstBase64BinaryItem), false);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<XsdTypes>\n");
        toString(sb);
        sb.append("</XsdTypes>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        sb.append("<elemString>");
        sb.append(elemString);
        sb.append("</elemString>\n");
        sb.append("<elemInteger>");
        sb.append(elemInteger);
        sb.append("</elemInteger>\n");
        sb.append("<elemCharacter>");
        sb.append(elemCharacter);
        sb.append("</elemCharacter>\n");
        sb.append("<elemFloat>");
        sb.append(elemFloat);
        sb.append("</elemFloat>\n");
        sb.append("<elemDouble>");
        sb.append(elemDouble);
        sb.append("</elemDouble>\n");
        sb.append("<elemDecimal>");
        sb.append(elemDecimal);
        sb.append("</elemDecimal>\n");
        sb.append("<elemDateTime>");
        sb.append(elemDateTime);
        sb.append("</elemDateTime>\n");
        sb.append("<elemBoolean>");
        sb.append(elemBoolean);
        sb.append("</elemBoolean>\n");
        sb.append("<elemBase64Binary>");
        sb.append(elemBase64Binary);
        sb.append("</elemBase64Binary>\n");
        if (lstString != null) {
            sb.append("<lstString>");
            for (Object obj : lstString) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</lstString>\n");
        } else {
            sb.append("<lstString/>\n");
        }
        if (lstInteger != null) {
            sb.append("<lstInteger>");
            for (Object obj : lstInteger) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</lstInteger>\n");
        } else {
            sb.append("<lstInteger/>\n");
        }
        if (lstCharacter != null) {
            sb.append("<lstCharacter>");
            for (Object obj : lstCharacter) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</lstCharacter>\n");
        } else {
            sb.append("<lstCharacter/>\n");
        }
        if (lstFloat != null) {
            sb.append("<lstFloat>");
            for (Object obj : lstFloat) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</lstFloat>\n");
        } else {
            sb.append("<lstFloat/>\n");
        }
        if (lstDouble != null) {
            sb.append("<lstDouble>");
            for (Object obj : lstDouble) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</lstDouble>\n");
        } else {
            sb.append("<lstDouble/>\n");
        }
        if (lstDecimal != null) {
            sb.append("<lstDecimal>");
            for (Object obj : lstDecimal) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</lstDecimal>\n");
        } else {
            sb.append("<lstDecimal/>\n");
        }
        if (lstDateTime != null) {
            sb.append("<lstDateTime>");
            for (Object obj : lstDateTime) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</lstDateTime>\n");
        } else {
            sb.append("<lstDateTime/>\n");
        }
        if (lstBoolean != null) {
            sb.append("<lstBoolean>");
            for (Object obj : lstBoolean) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</lstBoolean>\n");
        } else {
            sb.append("<lstBoolean/>\n");
        }
        if (lstBase64Binary != null) {
            sb.append("<lstBase64Binary>");
            for (Object obj : lstBase64Binary) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</lstBase64Binary>\n");
        } else {
            sb.append("<lstBase64Binary/>\n");
        }
        sb.append("<attString>");
        sb.append(attString);
        sb.append("</attString>\n");
        sb.append("<attInteger>");
        sb.append(attInteger);
        sb.append("</attInteger>\n");
        sb.append("<attCharacter>");
        sb.append(attCharacter);
        sb.append("</attCharacter>\n");
        sb.append("<attFloat>");
        sb.append(attFloat);
        sb.append("</attFloat>\n");
        sb.append("<attDouble>");
        sb.append(attDouble);
        sb.append("</attDouble>\n");
        sb.append("<attDecimal>");
        sb.append(attDecimal);
        sb.append("</attDecimal>\n");
        sb.append("<attDateTime>");
        sb.append(attDateTime);
        sb.append("</attDateTime>\n");
        sb.append("<attBoolean>");
        sb.append(attBoolean);
        sb.append("</attBoolean>\n");
        sb.append("<attBase64Binary>");
        sb.append(attBase64Binary);
        sb.append("</attBase64Binary>\n");
    }

}
