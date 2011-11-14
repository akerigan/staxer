package staxer.sample.bean;

import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "echoCustomTypes", namespace = "http://staxer.sample/")
@XmlAccessorType(XmlAccessType.FIELD)
public class EchoCustomTypesRequest extends CustomTypes {

    public static final XmlName XML_NAME_NILL_ELEM_ENUM = new XmlName("http://staxer.sample/", "nillElemEnum");
    public static final XmlName XML_NAME_NILL_ELEM_VALUE = new XmlName("http://staxer.sample/", "nillElemValue");
    public static final XmlName XML_NAME_NILL_ELEM_OVAL = new XmlName("http://staxer.sample/", "nillElemOval");

    @XmlElement(name = "nillElemEnum", namespace = "http://staxer.sample/", nillable = true)
    private EnumType nillElemEnum;

    @XmlElement(name = "nillElemValue", namespace = "http://staxer.sample/", nillable = true)
    private ValueType nillElemValue;

    @XmlElement(name = "nillElemOval", namespace = "http://staxer.sample/", nillable = true)
    private Oval nillElemOval;

    public EnumType getNillElemEnum() {
        return nillElemEnum;
    }

    public void setNillElemEnum(EnumType nillElemEnum) {
        this.nillElemEnum = nillElemEnum;
    }

    public ValueType getNillElemValue() {
        return nillElemValue;
    }

    public void setNillElemValue(ValueType nillElemValue) {
        this.nillElemValue = nillElemValue;
    }

    public Oval getNillElemOval() {
        return nillElemOval;
    }

    public void setNillElemOval(Oval nillElemOval) {
        this.nillElemOval = nillElemOval;
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
            if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_ENUM)) {
                nillElemEnum = EnumType.getByCode(xmlReader.readCharacters());
            } else if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_VALUE)) {
                nillElemValue = XmlUtils.readXml(xmlReader, ValueType.class, XML_NAME_NILL_ELEM_VALUE, true);
            } else if (xmlReader.elementStarted(XML_NAME_NILL_ELEM_OVAL)) {
                nillElemOval = XmlUtils.readXml(xmlReader, Oval.class, XML_NAME_NILL_ELEM_OVAL, true);
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
        String nillElemEnumCode = null;
        if (nillElemEnum != null) {
            nillElemEnumCode = nillElemEnum.getCode();
        }
        xmlWriter.element(XML_NAME_NILL_ELEM_ENUM, nillElemEnumCode, true);
        XmlUtils.writeXmlElement(xmlWriter, XML_NAME_NILL_ELEM_VALUE, nillElemValue, true);
        XmlUtils.writeXmlElement(xmlWriter, XML_NAME_NILL_ELEM_OVAL, nillElemOval, true);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<EchoCustomTypesRequest>\n");
        toString(sb);
        sb.append("</EchoCustomTypesRequest>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("<nillElemEnum>");
        sb.append(nillElemEnum);
        sb.append("</nillElemEnum>\n");
        sb.append("<nillElemValue>");
        sb.append(nillElemValue);
        sb.append("</nillElemValue>\n");
        sb.append("<nillElemOval>");
        sb.append(nillElemOval);
        sb.append("</nillElemOval>\n");
    }

}
