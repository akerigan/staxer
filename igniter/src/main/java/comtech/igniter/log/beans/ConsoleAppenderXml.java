package comtech.igniter.log.beans;

import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.StaxerXmlStreamException;
import comtech.util.xml.StaxerXmlStreamReader;
import comtech.util.xml.StaxerXmlStreamWriter;
import comtech.util.xml.XmlName;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConsoleAppenderXml extends AppenderXml {

    public static final XmlName XML_NAME_TARGET = new XmlName("target");

    @XmlAttribute(name = "target")
    private String target;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
        super.readXmlAttributes(attributes);
        target = attributes.get(XML_NAME_TARGET);
    }

    @Override
    public boolean readXmlContentElement(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        return super.readXmlContentElement(xmlReader);
    }

    @Override
    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        super.writeXmlAttributes(xmlWriter);
        xmlWriter.attribute(XML_NAME_TARGET, target);
    }

    @Override
    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        super.writeXmlContent(xmlWriter);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<ConsoleAppenderXml>\n");
        toString(sb);
        sb.append("</ConsoleAppenderXml>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("<target>");
        sb.append(target);
        sb.append("</target>\n");
    }

}
