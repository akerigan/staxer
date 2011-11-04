package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 11:51:51
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Element {

    @XmlAttribute(name = "ref")
    private String ref;

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "type")
    private String type;

    @XmlAttribute(name = "nillable")
    private boolean nillable;

    @XmlAttribute(name = "minOccurs")
    private int minOccurs;

    @XmlAttribute(name = "maxOccurs")
    private String maxOccurs = "1";

    @XmlElement(name = "complexType", namespace = "http://www.w3.org/2001/XMLSchema")
    private ComplexType complexType;

    @XmlElement(name = "simpleType", namespace = "http://www.w3.org/2001/XMLSchema")
    private SimpleType simpleType;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public int getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }

    public String getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(String maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public ComplexType getComplexType() {
        return complexType;
    }

    public void setComplexType(ComplexType complexType) {
        this.complexType = complexType;
    }

    public SimpleType getSimpleType() {
        return simpleType;
    }

    public void setSimpleType(SimpleType simpleType) {
        this.simpleType = simpleType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Element>\n");
        sb.append("<ref>");
        sb.append(ref);
        sb.append("</ref>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<type>");
        sb.append(type);
        sb.append("</type>\n");
        sb.append("<nillable>");
        sb.append(nillable);
        sb.append("</nillable>\n");
        sb.append("<minOccurs>");
        sb.append(minOccurs);
        sb.append("</minOccurs>\n");
        sb.append("<maxOccurs>");
        sb.append(maxOccurs);
        sb.append("</maxOccurs>\n");
        sb.append("<complexType>");
        sb.append(complexType);
        sb.append("</complexType>\n");
        sb.append("<simpleType>");
        sb.append(simpleType);
        sb.append("</simpleType>\n");
        sb.append("</Element>\n");

        return sb.toString();
    }

}
