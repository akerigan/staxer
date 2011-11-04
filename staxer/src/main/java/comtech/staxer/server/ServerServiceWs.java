package comtech.staxer.server;

import comtech.util.xml.XmlName;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-04-19 13:46 (Europe/Moscow)
 */
public interface ServerServiceWs {

    public Class getClass(XmlName qName);

    public String getMethodName(XmlName qName);

}
