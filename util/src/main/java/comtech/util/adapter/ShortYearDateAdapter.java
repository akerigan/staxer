package comtech.util.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Date with 2-digit year format
 * </p>
 *
 * @author Anton Proshin (proshin.anton@gmail.com)
 * @since 2011-03-25 16:02 (Moscow/Europe)
 */
public class ShortYearDateAdapter extends DateAdapter {

    DateFormat getDateFormat() {
        return new SimpleDateFormat("dd.MM.yy");
    }
}

