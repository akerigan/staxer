package org.staxer.sample.test;

import org.staxer.util.xml.*;
import junit.framework.TestCase;
import org.staxer.sample.bean.*;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

/**
 * @author Vlad Vinichenko(akerigan@gmail.com)
 * @since 2011-11-10 23:02 (Europe/Moscow)
 */
public class SampleTests extends TestCase {

    public static final XmlName XML_NAME_XSD_TYPES = new XmlName("http://sample.staxer.org/", "xsdTypes");
    public static final XmlName XML_NAME_ECHO_XSD_TYPES = new XmlName("http:///sample.staxer.org/", "echoXsdTypes");
    public static final XmlName XML_NAME_CUSTOM_TYPES = new XmlName("http://sample.staxer.org/", "customTypes");
    public static final XmlName XML_NAME_ECHO_CUSTOM_TYPES = new XmlName("http://sample.staxer.org/", "echoCustomTypes");

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

    public void fillCustomTypes(CustomTypes customTypes) throws UnsupportedEncodingException {
        customTypes.setAttEnum(EnumType.ONE);
        customTypes.setElemEnum(EnumType.ONE);
        customTypes.setElemOval(createOval());
        customTypes.setElemValue(createValue());
        customTypes.getLstEnum().add(EnumType.ONE);
        customTypes.getLstEnum().add(EnumType.ONE);
        customTypes.getLstEnum().add(EnumType.ONE);
        customTypes.getLstOval().add(createOval());
        customTypes.getLstOval().add(createOval());
        customTypes.getLstOval().add(createOval());
        customTypes.getLstValue().add(createValue());
        customTypes.getLstValue().add(createValue());
        customTypes.getLstValue().add(createValue());
    }

    public Oval createOval() {
        Oval result = new Oval();
        result.setX(CONSTANT_DOUBLE);
        result.setY(CONSTANT_DOUBLE);
        result.setRadius(CONSTANT_DOUBLE);
        result.setSecondRadius(CONSTANT_DOUBLE);
        return result;
    }

    private ValueType createValue() {
        ValueType result = new ValueType();
        result.setAttBoolean(CONSTANT_BOOLEAN);
//        result.setValue(EnumType.ONE);
        return result;
    }

    public String createXml(
            StaxerWriteXml staxerWriteXml, XmlName elementName
    ) throws StaxerXmlStreamException, UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StaxerXmlStreamWriter xmlWriter = new StaxerXmlStreamWriter(baos, "UTF-8", 4);
        xmlWriter.startDocument();
        XmlUtils.writeXmlElement(xmlWriter, staxerWriteXml, elementName);
        xmlWriter.endDocument();
        return new String(baos.toByteArray(), "UTF-8");
    }

    public void testXsdTypes() throws UnsupportedEncodingException, StaxerXmlStreamException {
        XsdTypes xsdTypes1 = new XsdTypes();
        fillXsdTypes(xsdTypes1);
        String xml1 = createXml(xsdTypes1, XML_NAME_XSD_TYPES);
        System.out.println("xml1 = " + xml1);

        XsdTypes xsdTypes2 = XmlUtils.readXml(new StringReader(xml1), XsdTypes.class, XML_NAME_XSD_TYPES);
        String xml2 = createXml(xsdTypes2, XML_NAME_XSD_TYPES);
        System.out.println("xml2 = " + xml2);

        assertEquals(xml1, xml2);
    }

    public void testEchoXsdTypes() throws UnsupportedEncodingException, StaxerXmlStreamException {
        EchoXsdTypesRequest echoXsdTypesRequest1 = new EchoXsdTypesRequest();
        fillXsdTypes(echoXsdTypesRequest1);

        String xml1 = createXml(echoXsdTypesRequest1, XML_NAME_ECHO_XSD_TYPES);
        System.out.println("xml1 = " + xml1);

        EchoXsdTypesRequest echoXsdTypesRequest2 = XmlUtils.readXml(new StringReader(xml1), EchoXsdTypesRequest.class, XML_NAME_ECHO_XSD_TYPES);
        String xml2 = createXml(echoXsdTypesRequest2, XML_NAME_ECHO_XSD_TYPES);
        System.out.println("xml2 = " + xml2);

        assertEquals(xml1, xml2);
    }

    public void testCustomTypes() throws UnsupportedEncodingException, StaxerXmlStreamException {
        CustomTypes customTypes1 = new CustomTypes();
        fillCustomTypes(customTypes1);
        String xml1 = createXml(customTypes1, XML_NAME_CUSTOM_TYPES);
        System.out.println("xml1 = " + xml1);

        CustomTypes customTypes2 = XmlUtils.readXml(new StringReader(xml1), CustomTypes.class, XML_NAME_CUSTOM_TYPES);
        String xml2 = createXml(customTypes2, XML_NAME_CUSTOM_TYPES);
        System.out.println("xml2 = " + xml2);

        assertEquals(xml1, xml2);
    }

}
