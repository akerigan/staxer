package staxer.sample.test;

import comtech.util.xml.StaxerXmlStreamException;
import comtech.util.xml.StaxerXmlStreamWriter;
import comtech.util.xml.XmlName;
import comtech.util.xml.XmlUtils;
import junit.framework.TestCase;
import staxer.sample.bean.EchoXsdTypesRequest;
import staxer.sample.bean.XsdTypes;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

/**
 * @author Vlad Vinichenko(akerigan@gmail.com)
 * @since 2011-11-10 23:02 (Europe/Moscow)
 */
public class SampleTests extends TestCase {

    public static final XmlName XML_NAME_XSD_TYPES = new XmlName("http://staxer.sample/", "xsdType");
    public static final XmlName XML_NAME_ECHO_XSD_TYPES = new XmlName("http://staxer.sample/", "echoXsdType");

    public static final String CONSTANT_STRING = "АБВГДЕ";
    private static final boolean CONSTANT_BOOLEAN = true;
    private static final int CONSTANT_CHARACTER = 123;
    private static final BigDecimal CONSTANT_DECIMAL = new BigDecimal(123123123);
    private static final double CONSTANT_DOUBLE = 123.123123;
    private static final float CONSTANT_FLOAT = 123.123123f;
    private static final int CONSTANT_INTEGER = 123123;

    public void fillXsdTypes(XsdTypes xsdTypes) throws UnsupportedEncodingException {
        xsdTypes.setAttBase64Binary(CONSTANT_STRING.getBytes("UTF-8"));
        xsdTypes.setAttBoolean(CONSTANT_BOOLEAN);
        xsdTypes.setAttCharacter(CONSTANT_CHARACTER);
        xsdTypes.setAttDecimal(CONSTANT_DECIMAL);
        xsdTypes.setAttDouble(CONSTANT_DOUBLE);
        xsdTypes.setAttFloat(CONSTANT_FLOAT);
        xsdTypes.setAttInteger(CONSTANT_INTEGER);
        xsdTypes.setAttString(CONSTANT_STRING);
        xsdTypes.setElemBase64Binary(CONSTANT_STRING.getBytes("UTF-8"));
        xsdTypes.setElemBoolean(CONSTANT_BOOLEAN);
        xsdTypes.setElemCharacter(CONSTANT_CHARACTER);
        xsdTypes.setElemDecimal(CONSTANT_DECIMAL);
        xsdTypes.setElemDouble(CONSTANT_DOUBLE);
        xsdTypes.setElemFloat(CONSTANT_FLOAT);
        xsdTypes.setElemInteger(CONSTANT_INTEGER);
        xsdTypes.setElemString(CONSTANT_STRING);
        xsdTypes.getLstBase64Binary().add(CONSTANT_STRING.getBytes("UTF-8"));
        xsdTypes.getLstBase64Binary().add(CONSTANT_STRING.getBytes("UTF-8"));
        xsdTypes.getLstBase64Binary().add(CONSTANT_STRING.getBytes("UTF-8"));
        xsdTypes.getLstBoolean().add(CONSTANT_BOOLEAN);
        xsdTypes.getLstBoolean().add(CONSTANT_BOOLEAN);
        xsdTypes.getLstBoolean().add(CONSTANT_BOOLEAN);
        xsdTypes.getLstCharacter().add(CONSTANT_CHARACTER);
        xsdTypes.getLstCharacter().add(CONSTANT_CHARACTER);
        xsdTypes.getLstCharacter().add(CONSTANT_CHARACTER);
        xsdTypes.getLstDecimal().add(CONSTANT_DECIMAL);
        xsdTypes.getLstDecimal().add(CONSTANT_DECIMAL);
        xsdTypes.getLstDecimal().add(CONSTANT_DECIMAL);
        xsdTypes.getLstDouble().add(CONSTANT_DOUBLE);
        xsdTypes.getLstDouble().add(CONSTANT_DOUBLE);
        xsdTypes.getLstDouble().add(CONSTANT_DOUBLE);
        xsdTypes.getLstFloat().add(CONSTANT_FLOAT);
        xsdTypes.getLstFloat().add(CONSTANT_FLOAT);
        xsdTypes.getLstFloat().add(CONSTANT_FLOAT);
        xsdTypes.getLstInteger().add(CONSTANT_INTEGER);
        xsdTypes.getLstInteger().add(CONSTANT_INTEGER);
        xsdTypes.getLstInteger().add(CONSTANT_INTEGER);
        xsdTypes.getLstString().add(CONSTANT_STRING);
        xsdTypes.getLstString().add(CONSTANT_STRING);
        xsdTypes.getLstString().add(CONSTANT_STRING);
    }

    public String createXsdTypesXml(XsdTypes xsdTypes) throws StaxerXmlStreamException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StaxerXmlStreamWriter xmlWriter = new StaxerXmlStreamWriter(baos, "UTF-8", 4);
        xmlWriter.startDocument();
        XmlUtils.writeXmlElement(xmlWriter, xsdTypes, XML_NAME_XSD_TYPES);
        xmlWriter.endDocument();
        return new String(baos.toByteArray(), "UTF-8");
    }

    public String createEchoXsdTypesXml(EchoXsdTypesRequest echoXsdTypesRequest) throws StaxerXmlStreamException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StaxerXmlStreamWriter xmlWriter = new StaxerXmlStreamWriter(baos, "UTF-8", 4);
        xmlWriter.startDocument();
        XmlUtils.writeXmlElement(xmlWriter, echoXsdTypesRequest, XML_NAME_ECHO_XSD_TYPES);
        xmlWriter.endDocument();
        return new String(baos.toByteArray(), "UTF-8");
    }

    public void testXsdTypes() throws UnsupportedEncodingException, StaxerXmlStreamException {
        XsdTypes xsdTypes1 = new XsdTypes();
        fillXsdTypes(xsdTypes1);
        String xml1 = createXsdTypesXml(xsdTypes1);
        System.out.println("xml1 = " + xml1);

        XsdTypes xsdTypes2 = XmlUtils.readXml(new StringReader(xml1), XsdTypes.class, XML_NAME_XSD_TYPES);
        String xml2 = createXsdTypesXml(xsdTypes2);
        System.out.println("xml2 = " + xml2);

        assertEquals(xml1, xml2);
    }

    public void testEchoXsdTypes() throws UnsupportedEncodingException, StaxerXmlStreamException {
        EchoXsdTypesRequest echoXsdTypesRequest1 = new EchoXsdTypesRequest();
        fillXsdTypes(echoXsdTypesRequest1);

        String xml1 = createEchoXsdTypesXml(echoXsdTypesRequest1);
        System.out.println("xml1 = " + xml1);

        EchoXsdTypesRequest echoXsdTypesRequest2 = XmlUtils.readXml(new StringReader(xml1), EchoXsdTypesRequest.class, XML_NAME_ECHO_XSD_TYPES);
        String xml2 = createEchoXsdTypesXml(echoXsdTypesRequest2);
        System.out.println("xml2 = " + xml2);

        assertEquals(xml1, xml2);
    }
}
