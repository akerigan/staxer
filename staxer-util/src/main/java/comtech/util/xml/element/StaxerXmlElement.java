package comtech.util.xml.element;

import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.StaxerReadXml;
import comtech.util.xml.StaxerWriteXml;
import comtech.util.xml.XmlName;

import java.util.List;

/**
 * @author Vlad Vinichenko
 * @since 2012-12-14 18:03
 */
public interface StaxerXmlElement extends StaxerWriteXml, StaxerReadXml {

    XmlName getName();

    void setName(XmlName xmlName);

    XmlNameMapProperties getAttributes();

    void addAttribute(XmlName xmlName, String value);

    List<StaxerXmlElement> getSubElements();

    StaxerXmlElement getSubElement(XmlName xmlName, int elementIndex);

    void addSubElement(StaxerXmlElement childStaxerXmlElement);

    void addSubElement(int elementIndex, StaxerXmlElement childStaxerXmlElement);

    String getText();

    void setText(String value);

}
