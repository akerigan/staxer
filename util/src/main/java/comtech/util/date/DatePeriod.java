package comtech.util.date;

import java.util.Date;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2010-11-18 14:01:36 (Europe/Moscow)
 */
public class DatePeriod {

    private Date from;
    private Date to;

    public DatePeriod() {
    }

    public DatePeriod(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }
}
