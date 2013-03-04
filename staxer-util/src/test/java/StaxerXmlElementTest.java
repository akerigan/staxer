import comtech.util.xml.StaxerXmlStreamException;
import comtech.util.xml.StaxerXmlStreamReader;
import comtech.util.xml.XmlName;
import comtech.util.xml.XmlUtils;
import comtech.util.xml.element.ListStaxerXmlElement;
import junit.framework.TestCase;

import java.io.StringReader;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-12-09 16:23 (Europe/Moscow)
 */
public class StaxerXmlElementTest extends TestCase {

    public static final String XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<test first=\"asd\" second=\"sdf\">\n" +
                    "    <first first=\"asd\" second=\"sdf\">asd</first>\n" +
                    "    <second first=\"asd\" second=\"sdf\">\n" +
                    "        <third first=\"asd\" second=\"sdf\">sdf</third>\n" +
                    "        <third first=\"asd\" second=\"sdf\">\n" +
                    "            <forth first=\"asd\" second=\"sdf\">xcv</forth>\n" +
                    "            <forth first=\"asd\" second=\"sdf\">wer</forth>\n" +
                    "        </third>\n" +
                    "    </second>\n" +
                    "</test>";

    public void testXmlRead() throws StaxerXmlStreamException {
        StringReader sr = new StringReader(XML);
        StaxerXmlStreamReader xmlReader = new StaxerXmlStreamReader(sr);
        ListStaxerXmlElement test = XmlUtils.readXml(xmlReader, ListStaxerXmlElement.class, new XmlName("test"));
        System.out.println("test = " + test);
    }
}
