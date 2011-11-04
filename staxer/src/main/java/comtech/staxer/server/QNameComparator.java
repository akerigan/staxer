package comtech.staxer.server;

import javax.xml.namespace.QName;
import java.util.Comparator;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 16.11.2009
 * Time: 9:38:44
 */
public class QNameComparator implements Comparator<QName> {

    public int compare(QName qName1, QName qName2) {
        if (qName1 == null && qName2 == null) {
            return 0;
        } else if (qName1 == null) {
            return -1;
        } else if (qName2 == null) {
            return 1;
        } else {
            return qName1.toString().compareTo(qName2.toString());
        }
    }

}
