package comtech.util.xml;

/**
 * @author Vlad Vinichenko(akerigan@gmail.com)
 * @since 2011-10-14 22:13 (Europe/Moscow)
 */
public interface StaxerXmlWriter {

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException;

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException;

}
