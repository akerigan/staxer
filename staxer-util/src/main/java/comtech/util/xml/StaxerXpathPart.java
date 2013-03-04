package comtech.util.xml;

/**
 * @author Vlad Vinichenko
 * @since 2012-11-28 10:03
 */
public class StaxerXpathPart {

    private StaxerXpathPartType type = StaxerXpathPartType.NODE_ELEMENT;
    private XmlName xmlName;
    private Integer index;
    private String indexParam;
    private String function;

    public StaxerXpathPartType getType() {
        return type;
    }

    public StaxerXpathPart setType(StaxerXpathPartType type) {
        this.type = type;
        if (type == StaxerXpathPartType.FUNCTION) {
            xmlName = null;
            index = null;
        } else {
            function = null;
        }
        return this;
    }

    public XmlName getXmlName() {
        return xmlName;
    }

    public StaxerXpathPart setXmlName(XmlName xmlName) {
        this.xmlName = xmlName;
        if (type == StaxerXpathPartType.FUNCTION) {
            function = null;
            type = StaxerXpathPartType.NODE_ELEMENT;
        }
        return this;
    }

    public Integer getIndex() {
        return index;
    }

    public StaxerXpathPart setIndex(Integer index) {
        this.index = index;
        setType(StaxerXpathPartType.NODE_ELEMENT);
        return this;
    }

    public String getIndexParam() {
        return indexParam;
    }

    public void setIndexParam(String indexParam) {
        this.indexParam = indexParam;
    }

    public String getFunction() {
        return function;
    }

    public StaxerXpathPart setFunction(String function) {
        this.function = function;
        setType(StaxerXpathPartType.FUNCTION);
        return this;
    }

    public boolean isElement() {
        return type == StaxerXpathPartType.NODE_ELEMENT;
    }

    public boolean isAttribute() {
        return type == StaxerXpathPartType.NODE_ATTRIBUTE;
    }

    public boolean isFunction() {
        return type == StaxerXpathPartType.FUNCTION;
    }

    public boolean isTextFunction() {
        return type == StaxerXpathPartType.FUNCTION && "text()".equals(function);
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (type == StaxerXpathPartType.FUNCTION) {
            result.append(function);
        } else if (type == StaxerXpathPartType.NODE_ATTRIBUTE) {
            result.append('@');
            String xmlNamePrefix = xmlName.getPrefix();
            if (xmlNamePrefix != null) {
                result.append(xmlNamePrefix);
                result.append(':');
                result.append(xmlName.getLocalPart());
            } else {
                result.append(xmlName);
            }
        } else if (type == StaxerXpathPartType.NODE_ELEMENT) {
            String xmlNamePrefix = xmlName.getPrefix();
            if (xmlNamePrefix != null) {
                result.append(xmlNamePrefix);
                result.append(':');
                result.append(xmlName.getLocalPart());
            } else {
                result.append(xmlName);
            }
            if (index != null) {
                result.append('[');
                result.append(index);
                result.append(']');
            } else if (indexParam != null) {
                result.append('[');
                result.append(indexParam);
                result.append(']');
            }
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StaxerXpathPart that = (StaxerXpathPart) o;

        if (function != null ? !function.equals(that.function) : that.function != null) return false;
        if (index != null ? !index.equals(that.index) : that.index != null) return false;
        if (type != that.type) return false;
        if (xmlName != null ? !xmlName.equals(that.xmlName) : that.xmlName != null) return false;

        return true;
    }

    public boolean generallyEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StaxerXpathPart that = (StaxerXpathPart) o;

        if (function != null ? !function.equals(that.function) : that.function != null) return false;
        if (type != that.type) return false;
        if (xmlName != null ? !xmlName.equals(that.xmlName) : that.xmlName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (xmlName != null ? xmlName.hashCode() : 0);
        result = 31 * result + (index != null ? index.hashCode() : 0);
        result = 31 * result + (function != null ? function.hashCode() : 0);
        return result;
    }
}
