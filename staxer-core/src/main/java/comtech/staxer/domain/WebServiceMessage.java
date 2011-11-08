package comtech.staxer.domain;

import comtech.util.xml.XmlName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-14 18:37 (Europe/Moscow)
 */
public class WebServiceMessage {

    private XmlName name;
    private List<WebServiceMessagePart> parts = new ArrayList<WebServiceMessagePart>();

    public XmlName getName() {
        return name;
    }

    public void setName(XmlName name) {
        this.name = name;
    }

    public List<WebServiceMessagePart> getParts() {
        return parts;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WebServiceMessage>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        if (parts != null) {
            sb.append("<parts>");
            for (Object obj : parts) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</parts>\n");
        } else {
            sb.append("<parts/>\n");
        }
        sb.append("</WebServiceMessage>\n");

        return sb.toString();
    }
}
