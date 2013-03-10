package org.staxer.igniter.log.beans;

import org.staxer.util.http.helper.HttpRequestHelper;
import org.staxer.util.http.helper.ReadHttpParameters;
import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.StaxerXmlStreamException;
import org.staxer.util.xml.StaxerXmlStreamReader;
import org.staxer.util.xml.StaxerXmlStreamWriter;
import org.staxer.util.xml.XmlName;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConsoleAppenderXml extends AppenderXml implements ReadHttpParameters {

    public static final XmlName XML_NAME_TARGET = new XmlName("target");

    @XmlAttribute(name = "target")
    private String target;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void readHttpParameters(
            HttpRequestHelper httpRequestHelper
    ) {
        super.readHttpParameters(httpRequestHelper);
        target = httpRequestHelper.getRequestParameter("target");
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
