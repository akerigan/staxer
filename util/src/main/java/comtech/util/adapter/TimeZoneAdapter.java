package comtech.util.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.TimeZone;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com) (akerigan@gmail.com)
 * Date: 18.08.2008
 * Time: 22:36:48
 */
public class TimeZoneAdapter extends XmlAdapter<String, TimeZone> {

    public TimeZone unmarshal(String v) throws Exception {
        return TimeZone.getTimeZone("GMT " + v);
    }

    public String marshal(TimeZone v) throws Exception {
        return v.toString();
    }
}
