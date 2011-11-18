package comtech.igniter.db.beans;

import comtech.util.NumberUtils;
import comtech.util.props.XmlNameMapProperties;
import comtech.util.xml.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class BasicDataSourceXml implements StaxerReadXml, StaxerWriteXml {

    public static final XmlName XML_NAME_NAME = new XmlName("name");
    public static final XmlName XML_NAME_DB_DRIVER_CLASS_NAME = new XmlName("dbDriverClassName");
    public static final XmlName XML_NAME_DB_URL = new XmlName("dbUrl");
    public static final XmlName XML_NAME_USERNAME = new XmlName("username");
    public static final XmlName XML_NAME_PASSWORD = new XmlName("password");
    public static final XmlName XML_NAME_MIN_IDLE = new XmlName("minIdle");
    public static final XmlName XML_NAME_MAX_ACTIVE = new XmlName("maxActive");

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "dbDriverClassName")
    private String dbDriverClassName;

    @XmlAttribute(name = "dbUrl")
    private String dbUrl;

    @XmlAttribute(name = "username")
    private String username;

    @XmlAttribute(name = "password")
    private String password;

    @XmlAttribute(name = "minIdle")
    private Integer minIdle;

    @XmlAttribute(name = "maxActive")
    private Integer maxActive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDbDriverClassName() {
        return dbDriverClassName;
    }

    public void setDbDriverClassName(String dbDriverClassName) {
        this.dbDriverClassName = dbDriverClassName;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Integer getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public void readXmlAttributes(
            XmlNameMapProperties attributes
    ) throws StaxerXmlStreamException {
        name = attributes.get(XML_NAME_NAME);
        dbDriverClassName = attributes.get(XML_NAME_DB_DRIVER_CLASS_NAME);
        dbUrl = attributes.get(XML_NAME_DB_URL);
        username = attributes.get(XML_NAME_USERNAME);
        password = attributes.get(XML_NAME_PASSWORD);
        minIdle = NumberUtils.parseInteger(attributes.get(XML_NAME_MIN_IDLE));
        maxActive = NumberUtils.parseInteger(attributes.get(XML_NAME_MAX_ACTIVE));
    }

    public void readXmlContent(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
    }

    public boolean readXmlContentElement(
            StaxerXmlStreamReader xmlReader
    ) throws StaxerXmlStreamException {
        return false;
    }

    public void writeXmlAttributes(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
        xmlWriter.attribute(XML_NAME_NAME, name);
        xmlWriter.attribute(XML_NAME_DB_DRIVER_CLASS_NAME, dbDriverClassName);
        xmlWriter.attribute(XML_NAME_DB_URL, dbUrl);
        xmlWriter.attribute(XML_NAME_USERNAME, username);
        xmlWriter.attribute(XML_NAME_PASSWORD, password);
        xmlWriter.attribute(XML_NAME_MIN_IDLE, minIdle);
        xmlWriter.attribute(XML_NAME_MAX_ACTIVE, maxActive);
    }

    public void writeXmlContent(
            StaxerXmlStreamWriter xmlWriter
    ) throws StaxerXmlStreamException {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<BasicDataSourceXml>\n");
        toString(sb);
        sb.append("</BasicDataSourceXml>\n");
        return sb.toString();
    }

    public void toString(StringBuilder sb) {
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<dbDriverClassName>");
        sb.append(dbDriverClassName);
        sb.append("</dbDriverClassName>\n");
        sb.append("<dbUrl>");
        sb.append(dbUrl);
        sb.append("</dbUrl>\n");
        sb.append("<username>");
        sb.append(username);
        sb.append("</username>\n");
        sb.append("<password>");
        sb.append(password);
        sb.append("</password>\n");
        sb.append("<minIdle>");
        sb.append(minIdle);
        sb.append("</minIdle>\n");
        sb.append("<maxActive>");
        sb.append(maxActive);
        sb.append("</maxActive>\n");
    }

}
