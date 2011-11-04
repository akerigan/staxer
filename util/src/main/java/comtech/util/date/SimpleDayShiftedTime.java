package comtech.util.date;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 21.07.2010
 * Time: 11:59:47
 */
public class SimpleDayShiftedTime implements DayShiftedTime {

    private int hours;
    private int minutes;
    private int dayShift;

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setTime(String time) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(DateTimeUtils.parseTime(time));
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
        } catch (ParseException ignored) {
        }
    }

    public int getDayShift() {
        return dayShift;
    }

    public void setDayShift(int dayShift) {
        this.dayShift = dayShift;
    }

    public int getTimeInMinutes() {
        return hours * 60 + minutes + dayShift * 24 * 60;
    }

    public Date getTimeAsDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public String getTimeAsString() {
        return String.format("%02d:%02d", hours, minutes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTimeAsString());
        if (dayShift != 0) {
            sb.append(" ");
            sb.append(dayShift);
        }
        return sb.toString();
    }
}
