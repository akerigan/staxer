package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author Anton Proshin (proshin.anton@gmail.com)
 * @since 2011-10-04 15:31 (Moscow/Europe)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Union {

    @XmlAttribute(name = "memberTypes")
    private String memberTypes;

    public String getMemberTypes() {
        return memberTypes;
    }

    public void setMemberTypes(String memberTypes) {
        this.memberTypes = memberTypes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Union>\n");
        sb.append("<memberTypes>");
        sb.append(memberTypes);
        sb.append("</memberTypes>\n");
        sb.append("</Union>\n");

        return sb.toString();
    }
}
