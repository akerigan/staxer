package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 27.11.2009
 * Time: 10:47:06
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ComplexContent {

    @XmlElement(name = "extension", namespace = "http://www.w3.org/2001/XMLSchema")
    private ExtensionComplex extension;

    public ExtensionComplex getExtension() {
        return extension;
    }

    public void setExtension(ExtensionComplex extension) {
        this.extension = extension;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<ComplexContent>\n");
        sb.append("<extension>");
        sb.append(extension);
        sb.append("</extension>\n");
        sb.append("</ComplexContent>\n");

        return sb.toString();
    }
}
