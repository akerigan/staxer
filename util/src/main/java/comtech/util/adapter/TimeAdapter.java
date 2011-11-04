package comtech.util.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 15.08.2008
 * Time: 16:08:12
 */
public class TimeAdapter extends BaseDateAdapter {

    DateFormat getDateFormat() {
        return new SimpleDateFormat("HH:mm");
    }

}
