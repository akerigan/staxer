package comtech.util.xml;

import comtech.util.props.XmlNameMapProperties;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 07.04.2010
 * Time: 9:52:16
 */
public class StaxerXmlElement implements StaxerWriteXml, StaxerReadXml {

    private XmlName name;
    private String text;
    private XmlNameMapProperties attributes;
    private List<StaxerXmlElement> subElements;

    public StaxerXmlElement() {
    }

    public StaxerXmlElement(XmlName name) {
        this.name = name;
    }

    public StaxerXmlElement(String name) {
        this(new XmlName(name));
    }

    public StaxerXmlElement(XmlName name, Object text) {
        this.name = name;
        if (text != null) {
            this.text = text.toString();
        }
    }

    public StaxerXmlElement(String name, Object text) {
        this(new XmlName(name), text);
    }

    public XmlName getName() {
        return name;
    }

    public void setName(XmlName name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void appendText(String newText) {
        if (newText != null) {
            if (text == null) {
                text = newText;
            } else {
                text = text + " " + newText;
            }
        }
    }

    public XmlNameMapProperties getAttributes() {
        return attributes;
    }

    public void addAttribute(String name, String value) {
        addAttribute(new XmlName(name), value);
    }

    public void addAttribute(XmlName name, String value) {
        if (name != null) {
            if (attributes == null) {
                attributes = new XmlNameMapProperties();
            }
            if (value != null) {
                attributes.put(name, value);
            } else {
                attributes.put(name, "");
            }
        }
    }

    public List<StaxerXmlElement> getSubElements() {
        return subElements;
    }

    public void addSubElement(String name, String value) {
        addSubElement(new StaxerXmlElement(name, value));
    }

    public void addSubElement(StaxerXmlElement subElement) {
        if (subElement != null) {
            if (subElements == null) {
                subElements = new LinkedList<StaxerXmlElement>();
            }
            subElements.add(subElement);
        }
    }

    public void writeXmlAttributes(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        if (name != null) {
            xmlWriter.startElement(name);
        }
        if (attributes != null) {
            for (XmlName attributeName : attributes.getNames()) {
                xmlWriter.attribute(attributeName, attributes.get(attributeName));
            }
        }
    }

    public void writeXmlContent(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        if (subElements != null) {
            for (StaxerXmlElement subElement : subElements) {
                subElement.writeXmlContent(xmlWriter);
            }
        }
        if (text != null) {
            xmlWriter.text(text);
        }
        if (name != null) {
            xmlWriter.endElement();
        }
    }

    public void readXmlAttributes(XmlNameMapProperties attributes) throws StaxerXmlStreamException {
        this.attributes = attributes;
    }

    public void readXmlContent(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        name = xmlReader.getLastStartedElement();
        while (xmlReader.readNext() && !xmlReader.elementEnded(name)) {
            if (xmlReader.elementStarted()) {
                StaxerXmlElement subElement = XmlUtils.readXml(xmlReader, StaxerXmlElement.class, null);
                if (subElement != null) {
                    addSubElement(subElement);
                }
            } else {
                appendText(xmlReader.charactersRead());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        StaxerXmlElement that = (StaxerXmlElement) o;
        return this == o || !(o == null || getClass() != o.getClass())
                            && !(attributes != null ? !attributes.equals(that.attributes) : that.attributes != null)
                            && !(name != null ? !name.equals(that.name) : that.name != null)
                            && !(subElements != null ? !subElements.equals(that.subElements) : that.subElements != null)
                            && !(text != null ? !text.equals(that.text) : that.text != null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        result = 31 * result + (subElements != null ? subElements.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<StaxerXmlElement>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<text>");
        sb.append(text);
        sb.append("</text>\n");
        sb.append("<attributes>");
        sb.append(attributes);
        sb.append("</attributes>\n");
        if (subElements != null) {
            sb.append("<subElements>");
            for (Object obj : subElements) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</subElements>\n");
        } else {
            sb.append("<subElements/>\n");
        }
        sb.append("</StaxerXmlElement>\n");

        return sb.toString();
    }
}
