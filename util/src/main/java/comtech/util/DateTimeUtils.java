package comtech.util;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 24.04.2008
 * Time: 15:45:29
 */
public class DateTimeUtils {

    private static final long ONE_HOUR = 60 * 60 * 1000L;
    private static final Map<Integer, String[]> TIME_ZONES_MAP = new TreeMap<Integer, String[]>();
    private static final Date toDstShiftDate;
    private static final Date fromDstShiftDate;
    private static String[] russianMonths = new String[]{
            "Января",
            "Февраля",
            "Марта",
            "Апреля",
            "Мая",
            "Июня",
            "Июля",
            "Августа",
            "Сентября",
            "Октября",
            "Ноября",
            "Декабря"
    };

    private static String[] russianShortMonths = new String[]{
            "Янв",
            "Фев",
            "Мар",
            "Апр",
            "Мая",
            "Июн",
            "Июл",
            "Авг",
            "Сен",
            "Окт",
            "Ноя",
            "Дек"
    };

    private static String[] russianWeeks = new String[]{
            "Воскресенье",
            "Понедельник",
            "Вторник",
            "Среда",
            "Четверг",
            "Пятница",
            "Суббота"
    };

    private static String[] russianShortWeeks = new String[]{
            "Вос",
            "Пон",
            "Втр",
            "Срд",
            "Чтв",
            "Пят",
            "Суб"
    };

    static {
        for (String timezoneId : TimeZone.getAvailableIDs()) {
            TimeZone timeZone = TimeZone.getTimeZone(timezoneId);
            if (timeZone.getRawOffset() % 60000 == 0) {
                int minutesOffset = timeZone.getRawOffset() / 60000;
                String[] timeZoneIds = TIME_ZONES_MAP.get(minutesOffset);
                if (timeZoneIds == null) {
                    timeZoneIds = new String[2];
                    TIME_ZONES_MAP.put(minutesOffset, timeZoneIds);
                }
                timeZoneIds[timeZone.getDSTSavings() > 0 ? 1 : 0] = timezoneId;
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 3);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR, 3);
        calendar.set(Calendar.MINUTE, 0);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, dayOfWeek == 1 ? -7 : -dayOfWeek + 1);
        toDstShiftDate = calendar.getTime();
        calendar.set(Calendar.MONTH, 10);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, dayOfWeek == 1 ? -7 : -dayOfWeek + 1);
        fromDstShiftDate = calendar.getTime();
    }

    public static String formatDate(Date date, String format) {
        return formatDate(date, format, "ru");
    }

    public static String formatDate(Date date, String format, String language) {
        if (date == null) {
            return null;
        }
        DateFormatSymbols symbols = new DateFormatSymbols();
        if ("ru".equalsIgnoreCase(language)) {
            symbols.setMonths(russianMonths);
            symbols.setShortMonths(russianShortMonths);
            symbols.setWeekdays(russianWeeks);
            symbols.setShortWeekdays(russianShortWeeks);
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format, symbols);
        return formatter.format(date);
    }

    public static String formatDate(Date date, String format, Locale locale) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format, locale);
        return formatter.format(date);
    }

    public static String formatDate(Date date) {
        return formatDate(date, "dd.MM.yyyy");
    }

    public static String formatShortDate(Date date) {
        return formatDate(date, "dd.MM.yy");
    }

    public static String formatDate2(Date date) {
        if (date == null) {
            return null;
        }
        DateFormatSymbols symbols = new DateFormatSymbols();
        symbols.setMonths(russianMonths);
        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy", symbols);
        return formatter.format(date);
    }

    public static String formatDateTime(Date date) {
        return formatDate(date, "dd.MM.yyyy HH:mm");
    }

    public static String formatDateTime2(Date date) {
        return formatDate(date, "dd.MM.yyyy HH:mm:ss:SSS");
    }

    public static String formatDateTime3(Date date) {
        return formatDate(date, "dd.MM.yyyy HHmm");
    }

    public static String formatDateTime4(Date date) {
        return formatDate(date, "dd.MM.yy HH:mm");
    }

    public static String formatTimeDate(Date date) {
        return formatDate(date, "HH:mm dd.MM.yyyy");
    }

    public static String formatTime(Date date) {
        return formatDate(date, "HH:mm");
    }

    public static String formatTime2(Date date) {
        return formatDate(date, "HHmm");
    }

    public static String formatTimeZoneId(String genericTimeZoneId, boolean daylightSavings) {
        return getTimeZoneId(getMinutesOffset(genericTimeZoneId), daylightSavings);
    }

    public static Date parseDate(String date, String format) throws ParseException {
        if (date == null || date.length() == 0) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(date);
    }

    public static Date parseDate(String date) throws ParseException {
        return parseDate(date, "dd.MM.yyyy");
    }

    public static Date parseShortDate(String date) throws ParseException {
        return parseDate(date, "dd.MM.yy");
    }

    public static Date parseTime(String date) throws ParseException {
        return parseDate(date, "HH:mm");
    }

    public static Date parseDateTime(String date) throws ParseException {
        return parseDate(date, "dd.MM.yyyy HH:mm");
    }

    public static Date parseTimeDate(String date) throws ParseException {
        return parseDate(date, "HH:mm dd.MM.yyyy");
    }

    public static Date[] parseDates(String[] values, String format) throws ParseException {
        if (values != null) {
            Date[] result = new Date[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = parseDate(values[i]);
            }
            return result;
        } else {
            return null;
        }
    }

    public static Date fixDate(Date original) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(original);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTimeInMillis(0);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    public static List<Date> getDatesRange(SortedSet<Date> dates) {
        if (dates != null && dates.size() > 0) {
            long days = getDaysBetween(dates.first(), dates.last());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dates.first());
            List<Date> result = new ArrayList<Date>();
            result.add(dates.first());
            for (int i = 0; i < days; i++) {
                calendar.add(Calendar.DATE, 1);
                result.add(calendar.getTime());
            }
            return result;
        } else {
            return null;
        }
    }

    public static String getTimeZoneId(String genericTimeZoneId, boolean daylightSavings) {
        return getTimeZoneId(getMinutesOffset(genericTimeZoneId), daylightSavings);
    }

    public static String getTimeZoneId(int minutesOffset, boolean daylightSavings) {
        String[] timeZones = TIME_ZONES_MAP.get(minutesOffset);
        String timeZoneId;
        if (daylightSavings && timeZones[1] != null) {
            timeZoneId = timeZones[1];
        } else {
            timeZoneId = timeZones[0];
        }
        return timeZoneId;
    }

    public static int getDayTimeInMinutes(String time) {
        return getDayTimeInMinutes(time, 0);
    }

    public static int getDayTimeInMinutes(String time, int dayShift) {
        if (time == null) {
            return 0;
        }
        String[] ar = time.trim().split(":");
        if (ar.length != 2) {
            return 0;
        }
        try {
            int hours = Integer.valueOf(ar[0]);
            int minutes = Integer.valueOf(ar[1]);
            return hours * 60 + minutes + dayShift * 24 * 60;
        } catch (Exception ignored) {
            return 0;
        }
    }

    public static int getDayTimeInMinutes(Date time) {
        return getDayTimeInMinutes(time, 0);
    }

    public static int getDayTimeInMinutes(Date time, int dayShift) {
        if (time == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        try {
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            return (hours * 60 + minutes + dayShift * 24 * 60);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public static String getGenericTimeZoneId(TimeZone timeZone) {
        int minutesOffset = (timeZone.getRawOffset()) / 60000;
        if (timeZone.useDaylightTime()) {
            Date now = new Date();
            if (now.after(toDstShiftDate) && now.before(fromDstShiftDate)) {
                minutesOffset += 60;
            }
        }
        return String.format("%s%02d:%02d", minutesOffset < 0 ? '-' : '+', minutesOffset / 60, minutesOffset % 60);
    }

    public static int getMinutesOffset(String genericTimeZoneId) {
        int startIdx = genericTimeZoneId.length() - 5;
        String[] strings = genericTimeZoneId.substring(startIdx).split(":");
        int offset = Integer.parseInt(strings[0]) * 60 + Integer.parseInt(strings[1]);
        if ('-' == genericTimeZoneId.charAt(startIdx - 1)) {
            return -offset;
        } else {
            return offset;
        }
    }

    public static Date convertDate(Date date, TimeZone from, TimeZone to) {
        if (date == null) {
            return null;
        }
        if (from == null || to == null) {
            return date;
        }
        Date result = new Date();
        result.setTime(date.getTime() + to.getRawOffset() + to.getDSTSavings() - from.getRawOffset() - from.getDSTSavings());
        return result;
    }

    public static long getDaysBetween(Date d1, Date d2) {
        return ((d2.getTime() - d1.getTime() + ONE_HOUR) / (ONE_HOUR * 24));
    }

    public static Calendar getAsCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar getWithMinutesDelta(int minutes) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, minutes);

        return c;
    }

    public static Date joinDateAndTime(Date date, Date time) {
        if (time == null) {
            return date;
        }
        if (date == null) {
            return time;
        }
        Calendar dateCalendar = getAsCalendar(date);
        Calendar timeCalendar = getAsCalendar(time);

        dateCalendar.set(Calendar.HOUR, timeCalendar.get(Calendar.HOUR_OF_DAY));
        dateCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

        return dateCalendar.getTime();
    }

    public static Date addYears(Date date, int years) {
        return addToDate(date, Calendar.YEAR, years);
    }

    public static Date addMonths(Date date, int months) {
        return addToDate(date, Calendar.MONTH, months);
    }

    public static Date addDays(Date date, int days) {
        return addToDate(date, Calendar.DATE, days);
    }

    public static Date addHours(Date date, int hours) {
        return addToDate(date, Calendar.HOUR_OF_DAY, hours);
    }

    public static Date addMinutes(Date date, int minutes) {
        return addToDate(date, Calendar.MINUTE, minutes);
    }

    public static Date addToDate(Date date, int field, int value) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(field, value);
            return calendar.getTime();
        } else {
            return null;
        }
    }

    public static int getDeltaInMinutes(Date dateTime1, Date dateTime2) {
        if (dateTime1 == null || dateTime2 == null) {
            return 0;
        }
        return (int) ((dateTime2.getTime() - dateTime1.getTime()) / 60000);
    }

    public static int getDeltaInDays(Date dateTime1, Date dateTime2) {
        return getDeltaInMinutes(fixDate(dateTime1), fixDate(dateTime2)) / (24 * 60);
    }

    public static Date eraseTime(Date dateTime) {
        if (dateTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTime);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        } else {
            return null;
        }
    }

    public static Date parseXmlDate(String s) {
        return null;
    }

    public static Date formatXmlDate(Date date) {
        return formatXmlDate(date, null);
    }

    public static Date formatXmlDate(Date date, TimeZone timezone) {
        return null;
    }

}
