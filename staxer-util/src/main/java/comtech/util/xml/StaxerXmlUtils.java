package comtech.util.xml;

import comtech.util.NumberUtils;
import comtech.util.StringUtils;
import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.element.ListStaxerXmlElement;
import comtech.util.xml.element.MapStaxerXmlElement;
import comtech.util.xml.element.StaxerXmlElement;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.*;

/**
 * @author Vlad Vinichenko
 * @since 2012-11-28 10:00
 */
public class StaxerXmlUtils {

    public static List<String> splitXml(
            String xml
    ) throws Exception {
        if (!StringUtils.isEmpty(xml)) {
            StaxerXmlStreamReader xmlReader = new StaxerXmlStreamReader(new StringReader(xml));

            XmlName elementXmlName = xmlReader.readStartElement();
            StaxerXmlElement staxerXmlElement = XmlUtils.readXml(
                    xmlReader, ListStaxerXmlElement.class, elementXmlName
            );

            xmlReader.close();
            return splitXml(staxerXmlElement);
        } else {
            return null;
        }
    }

    public static List<String> splitXml(
            StaxerXmlElement staxerXmlElement
    ) throws Exception {
        if (staxerXmlElement != null) {
            ArrayList<String> result = new ArrayList<String>();
            XmlName xmlName = staxerXmlElement.getName();
            Map<String, String> namespaces = new HashMap<String, String>();
            String xpathString = "";
            String namespacesString = "";
            writeStaxerXmlElement(
                    xmlName, -1, true,
                    namespaces, 0, xpathString,
                    namespacesString, staxerXmlElement,
                    null, result
            );
            return result;
        } else {
            return null;
        }
    }

    private static void writeStaxerXmlElement(
            XmlName xmlName, int elementIndex, boolean isElement,
            Map<String, String> namespaces,
            int prefixIndex, String xpathString,
            String namespacesString, StaxerXmlElement staxerXmlElement,
            String value, List<String> result
    ) throws Exception {
        String xmlNameLocalPart;
        String namespacePrefix = null;
        if (xmlName != null) {
            xmlNameLocalPart = xmlName.getLocalPart();
            String namespaceURI = xmlName.getNamespaceURI();
            namespacePrefix = null;
            if (!StringUtils.isEmpty(namespaceURI)) {
                namespacePrefix = namespaces.get(namespaceURI);
                if (namespacePrefix == null) {
                    prefixIndex += 1;
                    namespacePrefix = "ns" + prefixIndex;
                    namespaces.put(namespaceURI, namespacePrefix);
                    if (StringUtils.isEmpty(namespacesString)) {
                        namespacesString += namespacePrefix + "=" + namespaceURI;
                    } else {
                        namespacesString += "," + namespacePrefix + "=" + namespaceURI;
                    }
                }
            }
        } else {
            isElement = true;
            xmlNameLocalPart = "text()";
        }
        if (isElement) {
            if (namespacePrefix != null) {
                xpathString += "/" + namespacePrefix + ":" + xmlNameLocalPart;
            } else {
                xpathString += "/" + xmlNameLocalPart;
            }
            if (elementIndex >= 0) {
                xpathString += "[" + elementIndex + "]";
            }
        } else {
            if (namespacePrefix != null) {
                xpathString = xpathString + "/@" + namespacePrefix + ":" + xmlNameLocalPart;
            } else {
                xpathString = xpathString + "/@" + xmlNameLocalPart;
            }
        }
        if (staxerXmlElement != null) {
            XmlNameMapProperties staxerXmlElementAttributes = staxerXmlElement.getAttributes();
            if (staxerXmlElementAttributes != null) {
                Map<XmlName, String> staxerXmlElementAttributesMap = staxerXmlElementAttributes.getMap();
                if (staxerXmlElementAttributesMap != null && !staxerXmlElementAttributesMap.isEmpty()) {
                    for (Map.Entry<XmlName, String> entry : staxerXmlElementAttributesMap.entrySet()) {
                        writeStaxerXmlElement(
                                entry.getKey(), -1,
                                false, namespaces,
                                prefixIndex, xpathString,
                                namespacesString, null,
                                entry.getValue(), result
                        );
                    }
                }
            }
            List<StaxerXmlElement> subElements = staxerXmlElement.getSubElements();
            if (subElements != null && !subElements.isEmpty()) {
                int subElementIndex = 0;
                for (StaxerXmlElement subElement : subElements) {
                    writeStaxerXmlElement(
                            subElement.getName(), subElementIndex,
                            true, namespaces,
                            prefixIndex, xpathString,
                            namespacesString, subElement,
                            subElement.getText(), result
                    );
                }
            }
        }
        if (value != null) {
            result.add(xpathString + ";" + namespacesString + ";" + value);
        }
    }

    public static MapStaxerXmlElement addToRootStaxerXmlElement(
            MapStaxerXmlElement rootElement,
            StaxerXpath staxerXpath,
            String value
    ) {
        StaxerXmlElement currentElement = null;
        boolean assigned = false;
        for (StaxerXpathPart xpathPart : staxerXpath.getParts()) {
            if (!xpathPart.isFunction()) {
                XmlName xmlName = xpathPart.getXmlName();
                if (xmlName != null) {
                    if (rootElement == null) {
                        rootElement = new MapStaxerXmlElement();
                        rootElement.setName(xmlName);
                    }
                    if (currentElement == null) {
                        currentElement = rootElement;
                        continue;
                    }
                    if (!xpathPart.isElement()) {
                        currentElement.addAttribute(xmlName, value);
                        assigned = true;
                    } else {
                        int elementIndex = NumberUtils.toSimpleInteger(xpathPart.getIndex());
                        StaxerXmlElement childStaxerXmlElement = currentElement.getSubElement(
                                xmlName,
                                elementIndex
                        );
                        if (childStaxerXmlElement == null) {
                            childStaxerXmlElement = new MapStaxerXmlElement(xmlName);
                            currentElement.addSubElement(elementIndex, childStaxerXmlElement);
                        }
                        currentElement = childStaxerXmlElement;
                    }
                }
            }
        }
        if (!assigned) {
            if (currentElement != null) {
                currentElement.setText(value);
            }
        }
        return rootElement;
    }

    public static String serializeToString(
            StaxerWriteXml staxerWriteXml, XmlName xmlName
    ) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlUtils.writeXml(baos, "UTF-8", 2, staxerWriteXml, xmlName);
        return new String(baos.toByteArray(), "UTF-8");
    }

    public static String indentXml(String xml) throws Exception {
        StringReader stringReader = new StringReader(xml);
        ListStaxerXmlElement xmlElement = XmlUtils.readXml(stringReader, ListStaxerXmlElement.class, null);
        return serializeToString(xmlElement, xmlElement.getName());
    }

}
