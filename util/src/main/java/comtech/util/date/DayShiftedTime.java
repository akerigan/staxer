package comtech.util.date;

import java.util.Date;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 21.07.2010
 * Time: 11:59:47
 */
public interface DayShiftedTime {

    public int getDayShift();

    public int getTimeInMinutes();

    public Date getTimeAsDate();

    public String getTimeAsString();

}
