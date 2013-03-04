package comtech.util.staxer.domain;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-21 14:43 (Europe/Moscow)
 */
public class XmlSchemaEnumValue {

    private String javaName;
    private String value;

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<XmlSchemaEnumValue>\n");
        sb.append("<javaName>");
        sb.append(javaName);
        sb.append("</javaName>\n");
        sb.append("<value>");
        sb.append(value);
        sb.append("</value>\n");
        sb.append("</XmlSchemaEnumValue>\n");

        return sb.toString();
    }
}
