package comtech.staxer.domain.schema;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.09.2009
 * Time: 11:47:28
 */
@XmlRootElement(name = "schema", namespace = "http://www.w3.org/2001/XMLSchema")
@XmlAccessorType(XmlAccessType.FIELD)
public class Schema {

    @XmlAttribute(name = "targetNamespace")
    private String targetNamespace = "";

    @XmlAttribute(name = "elementFormDefault")
    private String elementFormDefault = "";

    @XmlAttribute(name = "attributeFormDefault")
    private String attributeFormDefault = "";

    @XmlElement(name = "import", namespace = "http://www.w3.org/2001/XMLSchema")
    private List<SchemaImport> schemaImports = new ArrayList<SchemaImport>();

    @XmlElement(name = "include", namespace = "http://www.w3.org/2001/XMLSchema")
    private List<SchemaInclude> schemaIncludes = new ArrayList<SchemaInclude>();

    @XmlElement(name = "complexType", namespace = "http://www.w3.org/2001/XMLSchema")
    private List<ComplexType> complexTypes = new ArrayList<ComplexType>();

    @XmlElement(name = "simpleType", namespace = "http://www.w3.org/2001/XMLSchema")
    private List<SimpleType> simpleTypes = new ArrayList<SimpleType>();

    @XmlElement(name = "element", namespace = "http://www.w3.org/2001/XMLSchema")
    private List<Element> elements = new ArrayList<Element>();

    @XmlTransient
    private String pathname;

    @XmlTransient
    private Map<String, String> prefixXmlnsMap = new LinkedHashMap<String, String>();

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

    public List<SchemaImport> getSchemaImports() {
        return schemaImports;
    }

    public List<SchemaInclude> getSchemaIncludes() {
        return schemaIncludes;
    }

    public List<ComplexType> getComplexTypes() {
        return complexTypes;
    }

    public List<SimpleType> getSimpleTypes() {
        return simpleTypes;
    }

    public List<Element> getElements() {
        return elements;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    public Map<String, String> getPrefixXmlnsMap() {
        return prefixXmlnsMap;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<Schema>\n");
        sb.append("<targetNamespace>");
        sb.append(targetNamespace);
        sb.append("</targetNamespace>\n");
        sb.append("<elementFormDefault>");
        sb.append(elementFormDefault);
        sb.append("</elementFormDefault>\n");
        sb.append("<attributeFormDefault>");
        sb.append(attributeFormDefault);
        sb.append("</attributeFormDefault>\n");
        if (schemaImports != null) {
            sb.append("<schemaImports>");
            for (Object obj : schemaImports) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</schemaImports>\n");
        } else {
            sb.append("<schemaImports/>\n");
        }
        if (schemaIncludes != null) {
            sb.append("<schemaIncludes>");
            for (Object obj : schemaIncludes) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</schemaIncludes>\n");
        } else {
            sb.append("<schemaIncludes/>\n");
        }
        if (complexTypes != null) {
            sb.append("<complexTypes>");
            for (Object obj : complexTypes) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</complexTypes>\n");
        } else {
            sb.append("<complexTypes/>\n");
        }
        if (simpleTypes != null) {
            sb.append("<simpleTypes>");
            for (Object obj : simpleTypes) {
                sb.append("<item>");
                sb.append(obj);
                sb.append("</item>\n");
            }
            sb.append("</simpleTypes>\n");
        } else {
            sb.append("<simpleTypes/>\n");
        }
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
        sb.append("</Schema>\n");

        return sb.toString();
    }

}
