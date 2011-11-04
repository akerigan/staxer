package comtech.util.adapter;

import comtech.util.adapter.BaseDateAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 *         Date: 15.08.2008
 *         Time: 16:08:12
 */
public class DateTimeAdapter3 extends BaseDateAdapter {

    DateFormat getDateFormat() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    }

}
