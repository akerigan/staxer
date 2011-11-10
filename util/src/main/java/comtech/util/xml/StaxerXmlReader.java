package comtech.util.xml;

import comtech.util.props.StringMapProperties;

/**
 * @author Vlad Vinichenko(akerigan@gmail.com)
 * @since 2011-10-14 22:11 (Europe/Moscow)
 */
public interface StaxerXmlReader {

    public void readXmlAttributes(
            StringMapProperties attributes
    ) throws StaxerXmlStreamException;

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException;

}
