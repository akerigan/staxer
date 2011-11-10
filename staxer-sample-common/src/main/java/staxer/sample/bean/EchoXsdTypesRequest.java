package staxer.sample.bean;

import comtech.util.props.StringMapProperties;
import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "echoXsdTypes", namespace = "http://staxer.sample/")
@XmlAccessorType(XmlAccessType.FIELD)
public class EchoXsdTypesRequest extends XsdTypes implements StaxerXmlReader, StaxerXmlWriter {

    public void readXmlAttributes(
            StringMapProperties attributes
    ) throws StaxerXmlStreamException {
        super.readXmlAttributes(attributes);
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        super.readXmlContent(xmlReader);
    }

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        super.writeXmlAttributes(xmlWriter);
    }

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        super.writeXmlAttributes(xmlWriter);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<EchoXsdTypesRequest>\n");
        toString(sb);
        sb.append("</EchoXsdTypesRequest>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        super.toString(sb);
    }

}
