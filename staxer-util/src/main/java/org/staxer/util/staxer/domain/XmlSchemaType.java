package org.staxer.util.staxer.domain;

import org.staxer.util.xml.XmlName;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-14 18:32 (Europe/Moscow)
 */
public interface XmlSchemaType {

    XmlName getXmlName();

    XmlName getSuperTypeXmlName();

    String getJavaName();

    void setJavaName(String javaName);

    String getJavaPackage();

    String getDocumentation();

    void setDocumentation(String documentation);

    boolean isComplexType();

    boolean isEnumSimpleType();

}
