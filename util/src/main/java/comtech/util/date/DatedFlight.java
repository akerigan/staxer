package comtech.util.date;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 21.07.2010
 * Time: 12:13:20
 */
public interface DatedFlight {

    public DayShiftedTime getDepartureShiftedTime();

    public DayShiftedTime getArrivalShiftedTime();

    public int getFlightTimeInMinutes();

}
