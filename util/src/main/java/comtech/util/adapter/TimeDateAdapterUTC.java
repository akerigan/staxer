package comtech.util.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 *         Date: 15.08.2008
 *         Time: 16:08:12
 */
public class TimeDateAdapterUTC extends BaseDateAdapter {

    DateFormat getDateFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat;
    }

}
