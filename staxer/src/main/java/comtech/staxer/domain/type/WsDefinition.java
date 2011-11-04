package comtech.staxer.domain.type;

import javax.xml.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 22.04.2010
 * Time: 16:24:22
 */
@XmlRootElement(name = "definition")
@XmlAccessorType(XmlAccessType.FIELD)
public class WsDefinition {

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "targetNamespace")
    private String targetNamespace;

    @XmlElement(name = "elementFormDefault")
    private String elementFormDefault = "unqualified";

    @XmlElement(name = "attributeFormDefault")
    private String attributeFormDefault = "unqualified";

    @XmlElementWrapper(name = "types")
    @XmlElement(name = "type")
    private List<WsType> types = new LinkedList<WsType>();

    @XmlElementWrapper(name = "methods")
    @XmlElement(name = "method")
    private List<WsMethod> methods = new LinkedList<WsMethod>();

    //    TODO useless?
    // <xmlns, <typeName, WsType>>
    @XmlTransient
    private Map<String, Map<String, WsType>> xmlnsTypesMap = new LinkedHashMap<String, Map<String, WsType>>();

    // <packageNameLastPart, <prefix, xmlns>>
    @XmlTransient
    private Map<String, Map<String, String>> packageXmlnsMap = new LinkedHashMap<String, Map<String, String>>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
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

    public List<WsType> getTypes() {
        return types;
    }

    public List<WsMethod> getMethods() {
        return methods;
    }

    public Map<String, Map<String, WsType>> getXmlnsTypesMap() {
        return xmlnsTypesMap;
    }

    public Map<String, Map<String, String>> getPackageXmlnsMap() {
        return packageXmlnsMap;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WsDefinition>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<targetNamespace>");
        sb.append(targetNamespace);
        sb.append("</targetNamespace>\n");
        sb.append("<elementFormDefault>");
        sb.append(elementFormDefault);
        sb.append("</elementFormDefault>\n");
        sb.append("<attributeFormDefault>");
        sb.append(attributeFormDefault);
        sb.append("</attributeFormDefault>\n");
        if (types != null) {
            sb.append("<types>");
            for (Object obj : types) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</types>\n");
        } else {
            sb.append("<types/>\n");
        }
        if (methods != null) {
            sb.append("<methods>");
            for (Object obj : methods) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</methods>\n");
        } else {
            sb.append("<methods/>\n");
        }
        if (xmlnsTypesMap != null) {
            sb.append("<xmlnsTypesMap>");
            for (Object key : xmlnsTypesMap.keySet()) {
                sb.append("<entry key=\"");
                sb.append(key);
                sb.append("\">");
                sb.append(xmlnsTypesMap.get(key));
                sb.append("</entry>\n");
            }
            sb.append("</xmlnsTypesMap>\n");
        } else {
            sb.append("<xmlnsTypesMap/>\n");
        }
        if (packageXmlnsMap != null) {
            sb.append("<packageXmlnsMap>");
            for (Object key : packageXmlnsMap.keySet()) {
                sb.append("<entry key=\"");
                sb.append(key);
                sb.append("\">");
                sb.append(packageXmlnsMap.get(key));
                sb.append("</entry>\n");
            }
            sb.append("</packageXmlnsMap>\n");
        } else {
            sb.append("<packageXmlnsMap/>\n");
        }
        sb.append("</WsDefinition>\n");

        return sb.toString();
    }

}
