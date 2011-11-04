package comtech.util.date;

import comtech.util.date.beans.SimpleDatedFlight;
import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 21.07.2010
 * Time: 12:28:36
 */
public class DateTimeUtilsTest extends TestCase {

    public void testTotalFlightTime() {
        SimpleDatedFlight flight1 = new SimpleDatedFlight();
        SimpleDatedFlight flight2 = new SimpleDatedFlight();
        List<DatedFlight> flights = new LinkedList<DatedFlight>();
        flights.add(flight1);
        flights.add(flight2);

        SimpleDayShiftedTime departureTime = new SimpleDayShiftedTime();
        departureTime.setHours(22);
        departureTime.setMinutes(0);
        flight1.setDepartureTime(departureTime);

        SimpleDayShiftedTime arrivalTime = new SimpleDayShiftedTime();
        arrivalTime.setHours(2);
        arrivalTime.setMinutes(10);
        arrivalTime.setDayShift(1);
        flight1.setArrivalTime(arrivalTime);

        // 02:10
        flight1.setFlightTime(2 * 60 + 10);

        departureTime = new SimpleDayShiftedTime();
        departureTime.setHours(23);
        departureTime.setMinutes(10);
        departureTime.setDayShift(2);
        flight2.setDepartureTime(departureTime);

        arrivalTime = new SimpleDayShiftedTime();
        arrivalTime.setHours(13);
        arrivalTime.setMinutes(30);
        arrivalTime.setDayShift(3);
        flight2.setArrivalTime(arrivalTime);

        // 09:20
        flight2.setFlightTime(9 * 60 + 20);

        DayShiftedTime time = DateTimeUtils.getTotalFlightTime(flights);
        assertEquals("08:30 2", time.toString());
    }

    public void testTotalFlightTime2() {
        LinkedList<DatedFlight> flights = new LinkedList<DatedFlight>();
        SimpleDatedFlight flight = new SimpleDatedFlight();
        flights.add(flight);

        SimpleDayShiftedTime shiftedTime = new SimpleDayShiftedTime();
        flight.setDepartureTime(shiftedTime);
        shiftedTime.setHours(11);
        shiftedTime.setMinutes(15);
        shiftedTime = new SimpleDayShiftedTime();
        shiftedTime.setHours(12);
        shiftedTime.setMinutes(50);
        flight.setFlightTime(95);
        flight.setArrivalTime(shiftedTime);

        flight = new SimpleDatedFlight();
        flights.add(flight);
        shiftedTime = new SimpleDayShiftedTime();
        flight.setDepartureTime(shiftedTime);
        shiftedTime.setHours(16);
        shiftedTime.setMinutes(35);
        shiftedTime = new SimpleDayShiftedTime();
        shiftedTime.setHours(17);
        shiftedTime.setMinutes(35);
        flight.setFlightTime(3 * 60);
        flight.setArrivalTime(shiftedTime);

        DayShiftedTime time = DateTimeUtils.getTotalFlightTime(flights);
        assertEquals("08:20", time.toString());
    }

}
