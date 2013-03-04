package comtech.util.staxer.client;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-02-07 15:46 (Europe/Moscow)
 */
public class HttpRequestHeader {

    private String name;
    private String value;

    public HttpRequestHeader() {
    }

    public HttpRequestHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
