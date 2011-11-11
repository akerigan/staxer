package comtech.util.xml.soap;

import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-05-04 17:05 (Europe/Moscow)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SoapFaultDetail implements StaxerReadXml, StaxerWriteXml {

    public static final XmlName XML_NAME_ENTRY = new XmlName("entry");

    @XmlElement(name = "entry")
    private List<SoapFaultDetailEntry> entries = new LinkedList<SoapFaultDetailEntry>();

    public List getEntries() {
        return entries;
    }

    public void readXmlAttributes(XmlNameMapProperties attributes) throws StaxerXmlStreamException {
    }

    public void readXmlContent(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        XmlName rootElement = xmlReader.getLastStartedElement();
        while (xmlReader.readNext()) {
            if (xmlReader.elementEnded(rootElement)) {
                break;
            } else if (xmlReader.elementStarted(XML_NAME_ENTRY)) {
                SoapFaultDetailEntry entryLocal = XmlUtils.readXml(xmlReader, SoapFaultDetailEntry.class, XML_NAME_ENTRY);
                if (entryLocal != null) {
                    entries.add(entryLocal);
                }
            }
        }
    }

    public void writeXmlAttributes(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
    }

    public void writeXmlContent(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        for (SoapFaultDetailEntry entry : entries) {
            XmlUtils.writeXmlElement(xmlWriter, entry, XML_NAME_ENTRY);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<SoapFaultDetail>\n");
        if (entries != null) {
            sb.append("<entries>");
            for (Object obj : entries) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</entries>\n");
        } else {
            sb.append("<entries/>\n");
        }
        sb.append("</SoapFaultDetail>\n");

        return sb.toString();
    }
}
