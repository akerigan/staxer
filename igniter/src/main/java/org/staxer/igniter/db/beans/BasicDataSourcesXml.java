package org.staxer.igniter.db.beans;

import org.staxer.util.props.XmlNameMapProperties;
import org.staxer.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;

@XmlAccessorType(XmlAccessType.FIELD)
public class BasicDataSourcesXml implements StaxerReadXml, StaxerWriteXml {

    public static final XmlName XML_NAME_DATASOURCE = new XmlName("datasource");

    @XmlElement(name = "datasource")
    private ArrayList<BasicDataSourceXml> datasource = new ArrayList<BasicDataSourceXml>();

    public ArrayList<BasicDataSourceXml> getDatasource() {
        return datasource;
    }

    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
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
        if (xmlReader.elementStarted(XML_NAME_DATASOURCE)) {
            BasicDataSourceXml datasourceItem = XmlUtils.readXml(xmlReader, BasicDataSourceXml.class, XML_NAME_DATASOURCE, false);
            if (datasourceItem != null) {
                datasource.add(datasourceItem);
            }
            return true;
        }
        return false;
    }

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
    }

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        if (!datasource.isEmpty()) {
            for (BasicDataSourceXml datasourceItem : datasource) {
                XmlUtils.writeXmlElement(xmlWriter, XML_NAME_DATASOURCE, datasourceItem, false);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<BasicDataSourcesXml>\n");
        toString(sb);
        sb.append("</BasicDataSourcesXml>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        if (datasource != null) {
            sb.append("<datasource>");
            for (Object obj : datasource) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</datasource>\n");
        } else {
            sb.append("<datasource/>\n");
        }
    }

}
