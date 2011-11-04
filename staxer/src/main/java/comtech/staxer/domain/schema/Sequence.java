package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 11:50:41
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Sequence {

    @XmlElement(name = "element", namespace = "http://www.w3.org/2001/XMLSchema")
    private List<Element> elements = new ArrayList<Element>();

    public List<Element> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Sequence>\n");
        if (elements != null) {
            sb.append("<elements>");
            for (Object obj : elements) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</elements>\n");
        } else {
            sb.append("<elements/>\n");
        }
        sb.append("</Sequence>\n");

        return sb.toString();
    }
}
