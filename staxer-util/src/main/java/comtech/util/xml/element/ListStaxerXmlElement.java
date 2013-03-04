package comtech.util.xml.element;

import comtech.util.StringUtils;
import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 07.04.2010
 * Time: 9:52:16
 */
public class ListStaxerXmlElement implements StaxerXmlElement {

    private XmlName name;
    private String text;
    private XmlNameMapProperties attributes;
    private List<StaxerXmlElement> subElements;

    public ListStaxerXmlElement() {
    }

    public ListStaxerXmlElement(XmlName name) {
        this.name = name;
    }

    public ListStaxerXmlElement(String name) {
        this(new XmlName(name));
    }

    public ListStaxerXmlElement(XmlName name, Object text) {
        this.name = name;
        if (text != null) {
            this.text = text.toString();
        }
    }

    public ListStaxerXmlElement(String name, Object text) {
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
        newText = StringUtils.notEmptyTrimmedElseNull(newText);
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

    public StaxerXmlElement getSubElement(XmlName xmlName, int elementIndex) {
        int index = 0;
        for (StaxerXmlElement subElement : subElements) {
            if (subElement.getName().equals(xmlName)) {
                if (index == elementIndex) {
                    return subElement;
                } else {
                    index += 1;
                }
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<StaxerXmlElement> getSubElements() {
        if (subElements == null) {
            subElements = new ArrayList<StaxerXmlElement>();
        }
        return subElements;
    }

    public void addSubElement(String name, String value) {
        addSubElement(new ListStaxerXmlElement(name, value));
    }

    public void addSubElement(StaxerXmlElement subElement) {
        if (subElement != null) {
            if (subElements == null) {
                subElements = new LinkedList<StaxerXmlElement>();
            }
            subElements.add(subElement);
        }
    }

    public void addSubElement(int elementIndex, StaxerXmlElement subElement) {
        addSubElement(subElement);
    }

    public void writeXmlAttributes(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        if (attributes != null) {
            for (XmlName attributeName : attributes.getNames()) {
                xmlWriter.attribute(attributeName, attributes.get(attributeName));
            }
        }
    }

    public void writeXmlContent(StaxerXmlStreamWriter xmlWriter) throws StaxerXmlStreamException {
        if (subElements != null) {
            for (StaxerXmlElement subElement : subElements) {
                XmlUtils.writeXmlElement(xmlWriter, subElement, subElement.getName());
            }
        }
        if (text != null) {
            xmlWriter.text(text);
        }
    }

    public void readXmlAttributes(XmlNameMapProperties attributes) throws StaxerXmlStreamException {
        this.attributes = attributes;
    }

    public void readXmlContent(StaxerXmlStreamReader xmlReader) throws StaxerXmlStreamException {
        name = xmlReader.getLastStartedElement();
        while (xmlReader.readNext() && !xmlReader.elementEnded(name)) {
            if (xmlReader.elementStarted()) {
                ListStaxerXmlElement subElement = XmlUtils.readXml(xmlReader, ListStaxerXmlElement.class, null);
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
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof StaxerXmlElement)) {
            return false;
        }
        StaxerXmlElement that = (StaxerXmlElement) o;
        XmlNameMapProperties thisAttributes = attributes;
        XmlNameMapProperties thatAttributes = that.getAttributes();
        if (thisAttributes != null ? !thisAttributes.equals(thatAttributes) : thatAttributes != null) {
            return false;
        }
        XmlName thisName = name;
        XmlName thatName = that.getName();
        if (thisName != null ? !thisName.equals(thatName) : thatName != null) {
            return false;
        }
        List<StaxerXmlElement> thisSubelements = subElements;
        List<StaxerXmlElement> thatSubelements = that.getSubElements();
        if (thisSubelements != null ? !thisSubelements.equals(thatSubelements) : thatSubelements != null) {
            return false;
        }
        String thisText = text;
        String thatText = that.getText();
        return !(thisText != null ? !thisText.equals(thatText) : thatText != null);

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
        sb.append("<ListStaxerXmlElement>\n");
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
        sb.append("</ListStaxerXmlElement>\n");

        return sb.toString();
    }
}
