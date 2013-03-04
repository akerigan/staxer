package org.staxer.util.xml;

import org.staxer.util.StringUtils;

import javax.xml.namespace.QName;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-27 14:18 (Europe/Moscow)
 */
public class XmlName implements Comparable<XmlName> {

    private String namespaceURI;
    private String localPart;
    private String prefix;

    private int hashCode;
    private String toStringValue;

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

    public XmlName(QName qName) {
        this.namespaceURI = StringUtils.notEmptyElseNull(qName.getNamespaceURI());
        this.localPart = qName.getLocalPart();
        this.prefix = StringUtils.notEmptyElseNull(qName.getPrefix());
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = StringUtils.notEmptyElseNull(namespaceURI);
        this.hashCode = 0;
        this.toStringValue = null;
    }

    public String getLocalPart() {
        return localPart;
    }

    public void setLocalPart(String localPart) {
        this.localPart = localPart;
        this.hashCode = 0;
        this.toStringValue = null;
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
        if (hashCode() == objectToTest.hashCode()) {
            XmlName xmlName = (XmlName) objectToTest;
            if (namespaceURI != null) {
                return localPart.equals(xmlName.localPart)
                        && namespaceURI.equals(xmlName.namespaceURI);
            } else {
                return localPart.equals(xmlName.localPart);
            }
        } else {
            return false;
        }
    }

    public final int hashCode() {
        if (hashCode == 0) {
            if (namespaceURI != null) {
                hashCode = namespaceURI.hashCode() ^ localPart.hashCode();
            } else {
                hashCode = localPart.hashCode();
            }
        }
        return hashCode;
    }

    public String toString() {
        if (toStringValue == null) {
            if (namespaceURI == null) {
                toStringValue = localPart;
            } else {
                toStringValue = "{" + namespaceURI + "}" + localPart;
            }
        }
        return toStringValue;
    }

    public int compareTo(XmlName otherXmlName) {
        if (otherXmlName == null) {
            return 1;
        }
        return toString().compareTo(otherXmlName.toString());
    }
}
