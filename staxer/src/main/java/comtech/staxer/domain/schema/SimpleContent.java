package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.11.2009
 * Time: 16:22:44
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleContent {

    @XmlElement(name = "extension", namespace = "http://www.w3.org/2001/XMLSchema")
    private ExtensionSimple extension;

    public ExtensionSimple getExtension() {
        return extension;
    }

    public void setExtension(ExtensionSimple extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<SimpleContent>\n");
        sb.append("<extension>");
        sb.append(extension);
        sb.append("</extension>\n");
        sb.append("</SimpleContent>\n");

        return sb.toString();
    }
}
