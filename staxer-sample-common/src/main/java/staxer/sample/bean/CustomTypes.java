package staxer.sample.bean;

import comtech.util.props.StringMapProperties;
import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;

@XmlAccessorType(XmlAccessType.FIELD)
public class CustomTypes implements StaxerXmlReader, StaxerXmlWriter {

    public static final XmlName XML_NAME_ELEM_ENUM = new XmlName("http://staxer.sample/", "elemEnum");
    public static final XmlName XML_NAME_LST_ENUM = new XmlName("http://staxer.sample/", "lstEnum");
    public static final XmlName XML_NAME_ELEM_VALUE = new XmlName("http://staxer.sample/", "elemValue");
    public static final XmlName XML_NAME_LST_VALUE = new XmlName("http://staxer.sample/", "lstValue");
    public static final XmlName XML_NAME_ELEM_OVAL = new XmlName("http://staxer.sample/", "elemOval");
    public static final XmlName XML_NAME_LST_OVAL = new XmlName("http://staxer.sample/", "lstOval");
    public static final XmlName XML_NAME_ATT_ENUM = new XmlName("http://staxer.sample/", "attEnum");

    @XmlElement(name = "elemEnum", namespace = "http://staxer.sample/")
    private EnumType elemEnum;

    @XmlElement(name = "lstEnum", namespace = "http://staxer.sample/")
    private ArrayList<EnumType> lstEnum = new ArrayList<EnumType>();

    @XmlElement(name = "elemValue", namespace = "http://staxer.sample/")
    private ValueType elemValue;

    @XmlElement(name = "lstValue", namespace = "http://staxer.sample/")
    private ArrayList<ValueType> lstValue = new ArrayList<ValueType>();

    @XmlElement(name = "elemOval", namespace = "http://staxer.sample/")
    private Oval elemOval;

    @XmlElement(name = "lstOval", namespace = "http://staxer.sample/")
    private ArrayList<Oval> lstOval = new ArrayList<Oval>();

    @XmlAttribute(name = "attEnum", namespace = "http://staxer.sample/")
    private EnumType attEnum;

    public EnumType getElemEnum() {
        return elemEnum;
    }

    public void setElemEnum(EnumType elemEnum) {
        this.elemEnum = elemEnum;
    }

    public ArrayList<EnumType> getLstEnum() {
        return lstEnum;
    }

    public ValueType getElemValue() {
        return elemValue;
    }

    public void setElemValue(ValueType elemValue) {
        this.elemValue = elemValue;
    }

    public ArrayList<ValueType> getLstValue() {
        return lstValue;
    }

    public Oval getElemOval() {
        return elemOval;
    }

    public void setElemOval(Oval elemOval) {
        this.elemOval = elemOval;
    }

    public ArrayList<Oval> getLstOval() {
        return lstOval;
    }

    public EnumType getAttEnum() {
        return attEnum;
    }

    public void setAttEnum(EnumType attEnum) {
        this.attEnum = attEnum;
    }

    public void readXmlAttributes(
            StringMapProperties attributes
    ) throws StaxerXmlStreamException {
        attEnum = EnumType.getByCode(attributes.get(XML_NAME_ATT_ENUM.toString()));
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        XmlName rootElementName = xmlReader.getLastStartedElement();
        while (xmlReader.readNext()) {
            if (xmlReader.elementEnded(rootElementName)) {
                break;
            } else if (xmlReader.elementStarted(XML_NAME_ELEM_ENUM)) {
                elemEnum = EnumType.getByCode(xmlReader.readCharacters());
            } else if (xmlReader.elementStarted(XML_NAME_LST_ENUM)) {
                EnumType lstEnumItem = EnumType.getByCode(xmlReader.readCharacters());
                if (lstEnumItem != null) {
                    lstEnum.add(lstEnumItem);
                }
            } else if (xmlReader.elementStarted(XML_NAME_ELEM_VALUE)) {
                elemValue = XmlUtils.readXml(xmlReader, ValueType.class, XML_NAME_ELEM_VALUE, false);
            } else if (xmlReader.elementStarted(XML_NAME_LST_VALUE)) {
                ValueType lstValueItem = XmlUtils.readXml(xmlReader, ValueType.class, XML_NAME_LST_VALUE, false);
                if (lstValueItem != null) {
                    lstValue.add(lstValueItem);
                }
            } else if (xmlReader.elementStarted(XML_NAME_ELEM_OVAL)) {
                elemOval = XmlUtils.readXml(xmlReader, Oval.class, XML_NAME_ELEM_OVAL, false);
            } else if (xmlReader.elementStarted(XML_NAME_LST_OVAL)) {
                Oval lstOvalItem = XmlUtils.readXml(xmlReader, Oval.class, XML_NAME_LST_OVAL, false);
                if (lstOvalItem != null) {
                    lstOval.add(lstOvalItem);
                }
            }
        }
    }

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        if (attEnum != null) {
            xmlWriter.attribute(XML_NAME_ATT_ENUM, attEnum.getCode());
        }
    }

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        String elemEnumCode = null;
        if (elemEnum != null) {
            elemEnumCode = elemEnum.getCode();
        }
        xmlWriter.element(XML_NAME_ELEM_ENUM, elemEnumCode, false);
        if (!lstEnum.isEmpty()) {
            for (EnumType lstEnumItem : lstEnum) {
                String lstEnumItemCode = null;
                if (lstEnumItem != null) {
                    lstEnumItemCode = lstEnumItem.getCode();
                }
                xmlWriter.element(XML_NAME_LST_ENUM, lstEnumItemCode, false);
            }
        }
        XmlUtils.writeXmlElement(xmlWriter, XML_NAME_ELEM_VALUE, elemValue, false);
        if (!lstValue.isEmpty()) {
            for (ValueType lstValueItem : lstValue) {
                XmlUtils.writeXmlElement(xmlWriter, XML_NAME_LST_VALUE, lstValueItem, false);
            }
        }
        XmlUtils.writeXmlElement(xmlWriter, XML_NAME_ELEM_OVAL, elemOval, false);
        if (!lstOval.isEmpty()) {
            for (Oval lstOvalItem : lstOval) {
                XmlUtils.writeXmlElement(xmlWriter, XML_NAME_LST_OVAL, lstOvalItem, false);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<CustomTypes>\n");
        toString(sb);
        sb.append("</CustomTypes>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        sb.append("<elemEnum>");
        sb.append(elemEnum);
        sb.append("</elemEnum>\n");
        if (lstEnum != null) {
            sb.append("<lstEnum>");
            for (Object obj : lstEnum) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</lstEnum>\n");
        } else {
            sb.append("<lstEnum/>\n");
        }
        sb.append("<elemValue>");
        sb.append(elemValue);
        sb.append("</elemValue>\n");
        if (lstValue != null) {
            sb.append("<lstValue>");
            for (Object obj : lstValue) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</lstValue>\n");
        } else {
            sb.append("<lstValue/>\n");
        }
        sb.append("<elemOval>");
        sb.append(elemOval);
        sb.append("</elemOval>\n");
        if (lstOval != null) {
            sb.append("<lstOval>");
            for (Object obj : lstOval) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</lstOval>\n");
        } else {
            sb.append("<lstOval/>\n");
        }
        sb.append("<attEnum>");
        sb.append(attEnum);
        sb.append("</attEnum>\n");
    }

}
