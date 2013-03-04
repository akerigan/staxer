package org.staxer.util.staxer.server;

import org.staxer.util.http.helper.ReadHttpParameters;
import org.staxer.util.xml.StaxerReadXml;
import org.staxer.util.xml.XmlName;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-04-19 13:46 (Europe/Moscow)
 */
public interface ServerServiceWs {

    public Class<? extends StaxerReadXml> getReadXmlClass(XmlName requestXmlName);

    public Class<? extends ReadHttpParameters> getReadHttpParametersClass(XmlName requestXmlName);

    public String getMethodName(XmlName requestXmlName);

    public XmlName getResponseXmlName(XmlName requestXmlName);

}
