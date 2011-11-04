package comtech.util.adapter;

import java.util.Date;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 15.08.2008
 * Time: 16:08:12
 */
public class ShortDateAdapter extends DateAdapter {

    @Override
    public Date unmarshal(String v) throws Exception {
        if (v != null) {
            String[] splitted = v.split("\\.");
            if (splitted[2].length() == 2) {
                return super.unmarshal(splitted[0] + '.' + splitted[1] + ".20" + splitted[2]);
            } else {
                return super.unmarshal(v);
            }
        } else {
            return null;
        }
    }
}
