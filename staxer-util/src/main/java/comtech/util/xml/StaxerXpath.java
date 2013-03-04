package comtech.util.xml;

import comtech.util.StringUtils;

import java.util.*;

/**
 * @author Vlad Vinichenko
 * @since 2012-11-28 10:03
 */
public class StaxerXpath {

    protected static final int MODE_XPATH = 1;
    protected static final int MODE_NAMESPACES = 2;
    private List<StaxerXpathPart> parts = new ArrayList<StaxerXpathPart>();
    private String partsString;

    public StaxerXpath() {
    }

    public StaxerXpath(String expression) {
        expression = StringUtils.notEmptyTrimmedElseNull(expression);
        if (expression != null) {
            StringBuilder sb = new StringBuilder();
            String ns = null;
            int mode = MODE_XPATH;
            Map<String, String> namespaces = new HashMap<String, String>();
            for (int i = 0, length = expression.length(); i <= length; i += 1) {
                char ch = 0;
                if (i < length) {
                    ch = expression.charAt(i);
                }
                if (mode == MODE_XPATH) {
                    if (ch == '/' || ch == ';' || ch == 0) {
                        if (sb.length() > 0) {
                            StaxerXpathPart xpathPart = new StaxerXpathPart();
                            parts.add(xpathPart);
                            String part = sb.toString();
                            if (part.endsWith(")")) {
                                xpathPart.setFunction(part);
                            } else if (ns != null && ns.startsWith("@")) {
                                xpathPart.setXmlName(new XmlName(ns.substring(1), part));
                                xpathPart.setType(StaxerXpathPartType.NODE_ATTRIBUTE);
                            } else if (part.startsWith("@")) {
                                xpathPart.setXmlName(new XmlName(ns, part.substring(1)));
                                xpathPart.setType(StaxerXpathPartType.NODE_ATTRIBUTE);
                            } else if (part.endsWith("]")) {
                                int chIdx = part.indexOf("[");
                                String indexString = StringUtils.substring(part, chIdx + 1, -1);
                                try {
                                    Integer index = Integer.valueOf(
                                            indexString
                                    );
                                    xpathPart.setIndex(index);
                                } catch (NumberFormatException e) {
                                    xpathPart.setIndexParam(indexString);
                                }
                                xpathPart.setXmlName(new XmlName(ns, part.substring(0, chIdx)));
                            } else {
                                xpathPart.setXmlName(new XmlName(ns, part));
                            }
                            sb = new StringBuilder();
                        }
                    } else if (ch == ':') {
                        ns = sb.toString();
                        sb = new StringBuilder();
                    } else {
                        sb.append(ch);
                    }
                } else if (mode == MODE_NAMESPACES) {
                    if (ch == ',' || ch == ';' || ch == 0) {
                        if (sb.length() > 0) {
                            if (ns != null) {
                                namespaces.put(ns, sb.toString());
                            }
                            sb = new StringBuilder();
                        }
                    } else if (ch == '=') {
                        ns = sb.toString();
                        sb = new StringBuilder();
                    } else {
                        sb.append(ch);
                    }
                }
                if (ch == ';') {
                    mode += 1;
                }
            }
            if (!namespaces.isEmpty()) {
                for (Map.Entry<String, String> entry : namespaces.entrySet()) {
                    for (StaxerXpathPart part : parts) {
                        XmlName xmlName = part.getXmlName();
                        if (xmlName != null && entry.getKey().equals(xmlName.getNamespaceURI())) {
                            xmlName.setNamespaceURI(entry.getValue());
                        }
                    }
                }
            }
        }
    }

    public List<StaxerXpathPart> getParts() {
        return parts;
    }

    public void addPart(StaxerXpathPart xpathPart) {
        if (xpathPart != null) {
            parts.add(xpathPart);
        }
    }

    @Override
    public String toString() {
        if (partsString == null) {
            StringBuilder result = new StringBuilder("/");
            int prefixIdx = 1;
            Map<String, String> namespaces = new LinkedHashMap<String, String>();
            for (StaxerXpathPart part : parts) {
                XmlName xmlName = part.getXmlName();
                if (xmlName.getPrefix() == null) {
                    String namespaceURI = xmlName.getNamespaceURI();
                    String prefix = namespaces.get(namespaceURI);
                    if (prefix == null) {
                        prefix = "ns" + prefixIdx;
                        prefixIdx += 1;
                        namespaces.put(namespaceURI, prefix);
                    }
                    xmlName.setPrefix(prefix);
                }
            }
            for (StaxerXpathPart part : parts) {
                if (result.length() > 0) {
                    result.append('/');
                }
                result.append(part.toString());
            }
            result.append(';');
            for (Map.Entry<String, String> entry : namespaces.entrySet()) {
                if (result.charAt(result.length() - 1) != ';') {
                    result.append(',');
                }
                result.append(entry.getValue());
                result.append('=');
                result.append(entry.getKey());
            }
            partsString = result.toString();
        }
        return partsString;
    }

    public boolean childOf(StaxerXpath baseXpath) {
        if (baseXpath != null) {
            List<StaxerXpathPart> parts1 = baseXpath.getParts();
            int basePartsSize = parts1.size();
            if (basePartsSize > parts.size()) {
                return false;
            }
            for (int i = 0; i < basePartsSize; i+=1) {
                StaxerXpathPart xpathPart1 = parts1.get(i);
                StaxerXpathPart xpathPart2 = parts.get(i);
                if (!xpathPart1.equals(xpathPart2)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean generallyChildOf(StaxerXpath baseXpath) {
        if (baseXpath != null) {
            List<StaxerXpathPart> parts1 = baseXpath.getParts();
            int basePartsSize = parts1.size();
            if (basePartsSize > parts.size()) {
                return false;
            }
            for (int i = 0; i < basePartsSize; i+=1) {
                StaxerXpathPart xpathPart1 = parts1.get(i);
                StaxerXpathPart xpathPart2 = parts.get(i);
                if (!xpathPart1.generallyEquals(xpathPart2)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        String s1 = toString();
        String s2 = o.toString();
        if (s1 != null) {
            return s1.equals(s2);
        } else {
            return s2 == null;
        }
    }

    public boolean generallyEquals(StaxerXpath otherStaxerXpath) {
        if (this == otherStaxerXpath) {
            return true;
        }
        if (otherStaxerXpath == null) {
            return false;
        }
        if (otherStaxerXpath != null) {
            List<StaxerXpathPart> parts1 = otherStaxerXpath.getParts();
            int basePartsSize = parts1.size();
            if (basePartsSize != parts.size()) {
                return false;
            }
            for (int i = 0; i < basePartsSize; i+=1) {
                StaxerXpathPart xpathPart1 = parts1.get(i);
                StaxerXpathPart xpathPart2 = parts.get(i);
                if (!xpathPart1.generallyEquals(xpathPart2)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString() != null ? toString().hashCode() : 0;
    }

}
