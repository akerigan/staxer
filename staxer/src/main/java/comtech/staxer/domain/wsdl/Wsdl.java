package comtech.staxer.domain.wsdl;

import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 12:27:45
 */
@XmlRootElement(name = "definitions", namespace = "http://schemas.xmlsoap.org/wsdl/")
@XmlAccessorType(XmlAccessType.FIELD)
public class Wsdl {

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "elementFormDefault")
    private String elementFormDefault = "unqualified";

    @XmlAttribute(name = "attributeFormDefault")
    private String attributeFormDefault = "unqualified";

    @XmlAttribute(name = "targetNamespace")
    private String targetNamespace;

    @XmlElement(name = "import", namespace = "http://schemas.xmlsoap.org/wsdl/")
    private List<WsdlImport> imports = new LinkedList<WsdlImport>();

    @XmlElement(name = "types", namespace = "http://schemas.xmlsoap.org/wsdl/")
    private WsdlTypes Types;

    @XmlElement(name = "message", namespace = "http://schemas.xmlsoap.org/wsdl/")
    private List<WsdlMessage> messages = new ArrayList<WsdlMessage>();

    @XmlElement(name = "portType", namespace = "http://schemas.xmlsoap.org/wsdl/")
    private List<WsdlPortType> portTypes = new ArrayList<WsdlPortType>();

    @XmlTransient
    private Map<String, String> prefixXmlnsMap = new LinkedHashMap<String, String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getElementFormDefault() {
        return elementFormDefault;
    }

    public void setElementFormDefault(String elementFormDefault) {
        this.elementFormDefault = elementFormDefault;
    }

    public String getAttributeFormDefault() {
        return attributeFormDefault;
    }

    public void setAttributeFormDefault(String attributeFormDefault) {
        this.attributeFormDefault = attributeFormDefault;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public List<WsdlImport> getImports() {
        return imports;
    }

    public WsdlTypes getTypes() {
        return Types;
    }

    public void setTypes(WsdlTypes types) {
        Types = types;
    }

    public List<WsdlMessage> getMessages() {
        return messages;
    }

    public List<WsdlPortType> getPortTypes() {
        return portTypes;
    }

    public Map<String, String> getPrefixXmlnsMap() {
        return prefixXmlnsMap;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Wsdl>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<elementFormDefault>");
        sb.append(elementFormDefault);
        sb.append("</elementFormDefault>\n");
        sb.append("<attributeFormDefault>");
        sb.append(attributeFormDefault);
        sb.append("</attributeFormDefault>\n");
        sb.append("<targetNamespace>");
        sb.append(targetNamespace);
        sb.append("</targetNamespace>\n");
        if (imports != null) {
            sb.append("<imports>");
            for (Object obj : imports) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</imports>\n");
        } else {
            sb.append("<imports/>\n");
        }
        sb.append("<Types>");
        sb.append(Types);
        sb.append("</Types>\n");
        if (messages != null) {
            sb.append("<messages>");
            for (Object obj : messages) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</messages>\n");
        } else {
            sb.append("<messages/>\n");
        }
        if (portTypes != null) {
            sb.append("<portTypes>");
            for (Object obj : portTypes) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</portTypes>\n");
        } else {
            sb.append("<portTypes/>\n");
        }
        if (prefixXmlnsMap != null) {
            sb.append("<prefixXmlnsMap>");
            for (Object key : prefixXmlnsMap.keySet()) {
                sb.append("<entry key=\"");
                sb.append(key);
                sb.append("\">");
                sb.append(prefixXmlnsMap.get(key));
                sb.append("</entry>\n");
            }
            sb.append("</prefixXmlnsMap>\n");
        } else {
            sb.append("<prefixXmlnsMap/>\n");
        }
        sb.append("</Wsdl>\n");

        return sb.toString();
    }
}
