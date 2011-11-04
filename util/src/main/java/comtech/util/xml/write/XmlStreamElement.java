package comtech.util.xml.write;

import javax.xml.stream.XMLStreamException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 07.04.2010
 * Time: 9:52:16
 */
public class XmlStreamElement {

    private String name;
    private String text;
    private Map<String, String> attributes;
    private List<XmlStreamElement> subElements;

    public XmlStreamElement(String name) {
        this.name = name;
    }

    public XmlStreamElement(String name, Object text) {
        this.name = name;
        if (text != null) {
            this.text = text.toString();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void addAttribute(String name, String value) {
        if (name != null) {
            if (attributes == null) {
                attributes = new TreeMap<String, String>();
            }
            if (value != null) {
                attributes.put(name, value);
            } else {
                attributes.put(name, "");
            }
        }
    }

    public List<XmlStreamElement> getSubElements() {
        return subElements;
    }

    public void addSubElement(String name, String value) {
        addSubElement(new XmlStreamElement(name, value));
    }

    public void addSubElement(XmlStreamElement subElement) {
        if (subElement != null) {
            if (subElements == null) {
                subElements = new LinkedList<XmlStreamElement>();
            }
            subElements.add(subElement);
        }
    }

    public void serialize(DocumentXmlStreamWriter document) throws XMLStreamException {
        document.startElement(name);
        if (attributes != null) {
            for (Map.Entry<String, String> attributeEntry : attributes.entrySet()) {
                document.attribute(attributeEntry.getKey(), attributeEntry.getValue());
            }
        }
        if (subElements != null) {
            for (XmlStreamElement subElement : subElements) {
                subElement.serialize(document);
            }
        }
        if (text != null) {
            document.text(text);
        }
        document.endElement();
    }

    @Override
    public boolean equals(Object o) {
        XmlStreamElement that = (XmlStreamElement) o;
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
        sb.append("<XmlStreamElement>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<text>");
        sb.append(text);
        sb.append("</text>\n");
        if (attributes != null) {
            sb.append("<attributes>");
            for (Object key : attributes.keySet()) {
                sb.append("<entry key=\"");
                sb.append(key);
                sb.append("\">");
                sb.append(attributes.get(key));
                sb.append("</entry>\n");
            }
            sb.append("</attributes>\n");
        } else {
            sb.append("<attributes/>\n");
        }
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
        sb.append("</XmlStreamElement>\n");

        return sb.toString();
    }
}
