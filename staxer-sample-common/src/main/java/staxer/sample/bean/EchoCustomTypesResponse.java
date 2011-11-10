package staxer.sample.bean;

import comtech.util.props.StringMapProperties;
import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "echoCustomTypesResponse", namespace = "http://staxer.sample/")
@XmlAccessorType(XmlAccessType.FIELD)
public class EchoCustomTypesResponse implements StaxerXmlReader, StaxerXmlWriter {

    public void readXmlAttributes(
            StringMapProperties attributes
    ) throws StaxerXmlStreamException {
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
    }

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
    }

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<EchoCustomTypesResponse>\n");
        toString(sb);
        sb.append("</EchoCustomTypesResponse>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
    }

}
