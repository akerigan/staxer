package comtech.util.xml;

import comtech.util.props.XmlNameMapProperties;

/**
 * @author Vlad Vinichenko(akerigan@gmail.com)
 * @since 2011-10-14 22:11 (Europe/Moscow)
 */
public interface StaxerReadXml {

    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException;

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException;

}
