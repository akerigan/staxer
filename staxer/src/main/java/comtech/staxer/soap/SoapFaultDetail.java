package comtech.staxer.soap;

import comtech.util.xml.ReadXml;
import comtech.util.xml.XmlName;
import comtech.util.xml.read.DocumentXmlStreamReader2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.stream.XMLStreamException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-05-04 17:05 (Europe/Moscow)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SoapFaultDetail implements ReadXml {

    public static final XmlName QNAME_ENTRY = new XmlName("entry");

    @XmlElement(name = "entry")
    private List<SoapFaultDetailEntry> entries = new LinkedList<SoapFaultDetailEntry>();

    public List getEntries() {
        return entries;
    }

    public void readXml(DocumentXmlStreamReader2 reader, XmlName elementName) throws XMLStreamException {
        while (reader.readNext()) {
            if (reader.elementEnded(elementName)) {
                break;
            } else if (reader.elementStarted(QNAME_ENTRY)) {
                SoapFaultDetailEntry entryLocal = new SoapFaultDetailEntry();
                entryLocal.readXml(reader, QNAME_ENTRY);
                entries.add(entryLocal);
            }
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
