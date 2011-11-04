package comtech.util.xml.read;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 08.09.2009
 * Time: 15:53:00
 */
public class Element extends StartElement {

    String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
