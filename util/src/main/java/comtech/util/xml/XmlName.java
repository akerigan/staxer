package comtech.util.xml;

import comtech.util.StringUtils;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-27 14:18 (Europe/Moscow)
 */
public class XmlName {

    private String namespaceURI;
    private String localPart;
    private String prefix;

    public XmlName(String localPart) {
        this.localPart = localPart;
    }

    public XmlName(String namespaceURI, String localPart) {
        this.namespaceURI = StringUtils.notEmptyElseNull(namespaceURI);
        this.localPart = localPart;
    }

    public XmlName(String namespaceURI, String localPart, String prefix) {
        this.namespaceURI = StringUtils.notEmptyElseNull(namespaceURI);
        this.localPart = localPart;
        this.prefix = StringUtils.notEmptyElseNull(prefix);
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = StringUtils.notEmptyElseNull(namespaceURI);
    }

    public String getLocalPart() {
        return localPart;
    }

    public void setLocalPart(String localPart) {
        this.localPart = localPart;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = StringUtils.notEmptyElseNull(prefix);
    }

    public final boolean equals(Object objectToTest) {
        if (objectToTest == this) {
            return true;
        }
        if (objectToTest == null || !(objectToTest instanceof XmlName)) {
            return false;
        }
        XmlName xmlName = (XmlName) objectToTest;
        if (namespaceURI != null) {
            return localPart.equals(xmlName.localPart)
                   && namespaceURI.equals(xmlName.namespaceURI);
        } else {
            return localPart.equals(xmlName.localPart);
        }
    }

    public final int hashCode() {
        if (namespaceURI != null) {
            return namespaceURI.hashCode() ^ localPart.hashCode();
        } else {
            return localPart.hashCode();
        }
    }

    public String toString() {
        if (namespaceURI == null) {
            return localPart;
        } else {
            return "{" + namespaceURI + "}" + localPart;
        }
    }

}
