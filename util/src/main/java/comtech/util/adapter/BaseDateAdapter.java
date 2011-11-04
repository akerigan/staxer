package comtech.util.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.DateFormat;
import java.util.Date;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 15.08.2008
 * Time: 16:08:12
 */
public abstract class BaseDateAdapter extends XmlAdapter<String, Date> {

    abstract DateFormat getDateFormat();

    public String marshal(Date v) throws Exception {
        if (v == null) {
            return null;
        } else {
            return getDateFormat().format(v);
        }
    }

    public Date unmarshal(String v) throws Exception {
        if (v == null) {
            return null;
        } else {
            return getDateFormat().parse(v);
        }
    }
}
