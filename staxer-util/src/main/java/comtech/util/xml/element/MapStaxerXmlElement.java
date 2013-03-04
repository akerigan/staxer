package comtech.util.xml.element;

import comtech.util.NumberUtils;
import comtech.util.StringUtils;
import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.*;

import java.util.*;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 07.04.2010
 * Time: 9:52:16
 */
public class MapStaxerXmlElement implements StaxerXmlElement {

    private XmlName name;
    private String text;
    private XmlNameMapProperties attributes;
    private LinkedHashMap<XmlName, TreeMap<Integer, StaxerXmlElement>> subElements;

    public MapStaxerXmlElement() {
    }

    public MapStaxerXmlElement(XmlName name) {
        this.name = name;
    }

    public MapStaxerXmlElement(String name) {
        this(new XmlName(name));
    }

    public MapStaxerXmlElement(XmlName name, Object text) {
        this.name = name;
        if (text != null) {
            this.text = text.toString();
        }
    }

    public MapStaxerXmlElement(String name, Object text) {
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

    public List<StaxerXmlElement> getSubElements() {
        ArrayList<StaxerXmlElement> result = new ArrayList<StaxerXmlElement>();
        for (Map<Integer, StaxerXmlElement> integerMapStaxerXmlElementMap : subElements.values()) {
            for (StaxerXmlElement mapStaxerXmlElement : integerMapStaxerXmlElementMap.values()) {
                result.add(mapStaxerXmlElement);
            }
        }
        return result;
    }

    public Collection<StaxerXmlElement> getSubElements(XmlName xmlName) {
        if (subElements != null) {
            Map<Integer, StaxerXmlElement> map = subElements.get(xmlName);
            if (map != null) {
                return map.values();
            }
        }
        return null;
    }

    public StaxerXmlElement getSubElement(XmlName xmlName, int index) {
        if (subElements != null) {
            Map<Integer, StaxerXmlElement> map = subElements.get(xmlName);
            if (map != null) {
                return map.get(index);
            }
        }
        return null;
    }

    public void addSubElement(StaxerXmlElement childStaxerXmlElement) {
        addSubElement(-1, childStaxerXmlElement);
    }

    public void addSubElement(int index, XmlName name, String value) {
        addSubElement(index, new MapStaxerXmlElement(name, value));
    }

    public void addSubElement(int index, StaxerXmlElement subElement) {
        if (subElement != null) {
            if (subElements == null) {
                subElements = new LinkedHashMap<XmlName, TreeMap<Integer, StaxerXmlElement>>();
            }
            XmlName subElementName = subElement.getName();
            TreeMap<Integer, StaxerXmlElement> indexedElementsMap = subElements.get(subElementName);
            if (indexedElementsMap == null) {
                indexedElementsMap = new TreeMap<Integer, StaxerXmlElement>();
                subElements.put(subElementName, indexedElementsMap);
            }
            if (index >= 0) {
                indexedElementsMap.put(index, subElement);
            } else {
                indexedElementsMap.put(indexedElementsMap.lastEntry().getKey() + 1, subElement);
            }
        }
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
            for (Map<Integer, StaxerXmlElement> subElementsMap : subElements.values()) {
                for (StaxerXmlElement subElement : subElementsMap.values()) {
                    XmlUtils.writeXmlElement(xmlWriter, subElement, subElement.getName());
                }
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
        Map<XmlName, Integer> subElementsIndexesMap = null;
        while (xmlReader.readNext() && !xmlReader.elementEnded(name)) {
            if (xmlReader.elementStarted()) {
                MapStaxerXmlElement subElement = XmlUtils.readXml(xmlReader, MapStaxerXmlElement.class, null);
                if (subElement != null) {
                    XmlName subElementName = subElement.getName();
                    if (subElementsIndexesMap == null) {
                        subElementsIndexesMap = new LinkedHashMap<XmlName, Integer>();
                    }
                    int index = NumberUtils.toSimpleInteger(subElementsIndexesMap.get(subElementName));
                    addSubElement(index, subElement);
                    subElementsIndexesMap.put(subElementName, index + 1);
                }
            } else {
                appendText(xmlReader.charactersRead());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        MapStaxerXmlElement that = (MapStaxerXmlElement) o;
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
            for (Object obj : subElements.values()) {
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
