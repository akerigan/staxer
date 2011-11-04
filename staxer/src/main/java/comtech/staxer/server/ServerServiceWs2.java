package comtech.staxer.server;

import comtech.util.urlparams.ReadHttpParameters;
import comtech.util.xml.ReadXml;
import comtech.util.xml.XmlName;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-04-19 13:46 (Europe/Moscow)
 */
public interface ServerServiceWs2 {

    public Class<? extends ReadXml> getReadXmlClass(XmlName requestXmlName);

    public Class<? extends ReadHttpParameters> getReadHttpParametersClass(XmlName requestXmlName);

    public String getMethodName(XmlName requestXmlName);

    public XmlName getResponseXmlName(XmlName requestXmlName);

}
