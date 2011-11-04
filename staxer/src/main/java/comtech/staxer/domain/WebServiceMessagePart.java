package comtech.staxer.domain;

import comtech.util.xml.XmlName;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-17 19:05 (Europe/Moscow)
 */
public class WebServiceMessagePart {

    private XmlName name;
    private XmlName element;

    public XmlName getName() {
        return name;
    }

    public void setName(XmlName name) {
        this.name = name;
    }

    public XmlName getElement() {
        return element;
    }

    public void setElement(XmlName element) {
        this.element = element;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WebServiceMessagePart>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<element>");
        sb.append(element);
        sb.append("</element>\n");
        sb.append("</WebServiceMessagePart>\n");

        return sb.toString();
    }
}
