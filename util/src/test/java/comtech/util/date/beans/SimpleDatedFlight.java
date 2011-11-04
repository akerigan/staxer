package comtech.util.date.beans;

import comtech.util.date.DatedFlight;
import comtech.util.date.DayShiftedTime;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 21.07.2010
 * Time: 12:32:22
 */
public class SimpleDatedFlight implements DatedFlight {

    private DayShiftedTime departureTime;
    private DayShiftedTime arrivalTime;
    private int flightTime;

    public DayShiftedTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(DayShiftedTime departureTime) {
        this.departureTime = departureTime;
    }

    public DayShiftedTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(DayShiftedTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getFlightTime() {
        return flightTime;
    }

    public void setFlightTime(int flightTime) {
        this.flightTime = flightTime;
    }

    public DayShiftedTime getDepartureShiftedTime() {
        return departureTime;
    }

    public DayShiftedTime getArrivalShiftedTime() {
        return arrivalTime;
    }

    public int getFlightTimeInMinutes() {
        return flightTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<SimpleDatedFlight>\n");
        sb.append("<departureTime>");
        sb.append(departureTime);
        sb.append("</departureTime>\n");
        sb.append("<arrivalTime>");
        sb.append(arrivalTime);
        sb.append("</arrivalTime>\n");
        sb.append("<flightTime>");
        sb.append(flightTime);
        sb.append("</flightTime>\n");
        sb.append("</SimpleDatedFlight>\n");

        return sb.toString();
    }
}
