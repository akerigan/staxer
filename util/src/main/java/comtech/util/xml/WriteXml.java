package comtech.util.xml;

/**
 * @author Vlad Vinichenko(akerigan@gmail.com)
 * @since 2011-10-14 22:13 (Europe/Moscow)
 */
public interface WriteXml {

    public void writeXml(
            XmlStreamWriter writer, XmlName elementName
    ) throws Exception;

}
