package org.staxer.util.date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staxer.util.StringUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.text.*;
import java.util.*;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 24.04.2008
 * Time: 15:45:29
 */
public class DateTimeUtils {

    private static final long ONE_HOUR = 60 * 60 * 1000L;
    private static final Map<Integer, String[]> TIME_ZONES_MAP = new TreeMap<Integer, String[]>();
    private static final Date TO_DST_SHIFT_DATE;
    private static final Date FROM_DST_SHIFT_DATE;
    private static final String[] RUSSIAN_MONTHS = new String[]{
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

    private static final String[] RUSSIAN_SHORT_MONTHS = new String[]{
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

    private static final String[] RUSSIAN_WEEKS = new String[]{
            "",
            "Воскресенье",
            "Понедельник",
            "Вторник",
            "Среда",
            "Четверг",
            "Пятница",
            "Суббота"
    };

    private static final String[] RUSSIAN_SHORT_WEEKS = new String[]{
            "",
            "Вос",
            "Пон",
            "Втр",
            "Срд",
            "Чтв",
            "Пят",
            "Суб"
    };

    public static final TimeZone TIME_ZONE_MOSCOW = TimeZone.getTimeZone("Europe/Moscow");
    public static final TimeZone TIME_ZONE_UTC = TimeZone.getTimeZone("UTC");

    private static Logger log = LoggerFactory.getLogger(DateTimeUtils.class);

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
        TO_DST_SHIFT_DATE = calendar.getTime();
        calendar.set(Calendar.MONTH, 10);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, dayOfWeek == 1 ? -7 : -dayOfWeek + 1);
        FROM_DST_SHIFT_DATE = calendar.getTime();
    }

    public static String formatDate(Date date, String format, Locale locale) {
        if (locale != null) {
            return formatDate(date, format, locale, null);
        } else {
            return formatDate(date, format);
        }
    }

    public static String formatDate(Date date, String format) {
        return formatDate(date, format, null, null);
    }

    public static String formatDate(Date date, String format, TimeZone timeZone) {
        return formatDate(date, format, null, timeZone);
    }

    public static String formatDate(
            Date date, String format,
            Locale locale, TimeZone timeZone
    ) {
        if (date == null) {
            return null;
        }
        DateFormatSymbols symbols = new DateFormatSymbols();
        if (locale == null) {
            locale = Locale.getDefault();
        }
        SimpleDateFormat formatter;
        if ("ru".equalsIgnoreCase(locale.getLanguage())) {
            symbols.setMonths(RUSSIAN_MONTHS);
            symbols.setShortMonths(RUSSIAN_SHORT_MONTHS);
            symbols.setWeekdays(RUSSIAN_WEEKS);
            symbols.setShortWeekdays(RUSSIAN_SHORT_WEEKS);
            formatter = new SimpleDateFormat(format, symbols);
        } else {
            formatter = new SimpleDateFormat(format, locale);
        }
        if (timeZone != null) {
            formatter.setTimeZone(timeZone);
        }
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
        symbols.setMonths(RUSSIAN_MONTHS);
        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM yyyy", symbols);
        return formatter.format(date);
    }

    public static String formatDate3(Date date) {
        return formatDate(date, "yyyy-MM-dd");
    }

    public static String formatDateTime(Date date) {
        return formatDate(date, "dd.MM.yyyy HH:mm");
    }

    public static String formatDateTimeUTC(Date date) {
        return formatDate(date, "dd.MM.yyyy HH:mm", TIME_ZONE_UTC);
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

    public static String formatDateYear(Date date) {
        return formatDate(date, "yyyy");
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

    public static Date parseDate(String date, String format) {
        return parseDate(date, format, null);
    }

    public static Date parseDate(String date, String format, TimeZone timeZone) {
        if (date == null || date.length() == 0) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        if (timeZone != null) {
            dateFormat.setTimeZone(timeZone);
        }
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            log.error("Cant parse date '" + date + "' with format '" + format + "'");
            return null;
        }
    }

    public static Date parseDate(String date) {
        return parseDate(date, "dd.MM.yyyy");
    }

    public static Date parseDate2(String date) {
        return parseDate(date, "dd.MM.yy");
    }

    public static Date parseDate3(String date) {
        return parseDate(date, "yyyy-MM-dd");
    }

    public static Date parseTime(String date) {
        return parseDate(date, "HH:mm");
    }

    public static Date parseDateTime(String date) {
        return parseDate(date, "dd.MM.yyyy HH:mm");
    }

    public static Date parseDateTime2(String date) {
        return parseDate(date, "dd.MM.yyyy HH:mm:ss");
    }

    public static Date parseDateTime3(String date) {
        return parseDate(date, "yyyy-MM-dd HH:mm");
    }

    public static Date parseTimeDate(String date) {
        return parseDate(date, "HH:mm dd.MM.yyyy");
    }

    public static Date parseTimeDate2(String date) {
        return parseDate(date, "HH:mm:ss dd.MM.yyyy");
    }

    public static Date parseTimeDateUTC(String date) {
        return parseDate(date, "HH:mm dd.MM.yyyy", TIME_ZONE_UTC);
    }

    public static Date parseTimeDateMSK(String date) {
        return parseDate(date, "HH:mm dd.MM.yyyy", TIME_ZONE_MOSCOW);
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
            if (now.after(TO_DST_SHIFT_DATE) && now.before(FROM_DST_SHIFT_DATE)) {
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

    public static Date addSeconds(Date date, int seconds) {
        return addToDate(date, Calendar.SECOND, seconds);
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

    public static DatePeriod validateDatePeriod(Date from, Date to, Integer lastDays) {
        Date now = addMinutes(addDays(eraseTime(new Date()), 1), -1);
        if (from != null && to != null) {
            if (from.after(to)) {
                Date tmp = from;
                from = to;
                to = tmp;
            }
            if (to.after(now)) {
                to = now;
            }
            return new DatePeriod(from, to);
        }
        if (lastDays == null) {
            lastDays = 7;
        }
        Date defaultFrom = addDays(eraseTime(now), -lastDays);
        if (from != null) {
            if (from.after(now)) {
                return new DatePeriod(defaultFrom, now);
            }
            return new DatePeriod(from, now);
        } else if (to != null) {
            if (to.after(now)) {
                return new DatePeriod(defaultFrom, now);
            }
            return new DatePeriod(addDays(eraseTime(to), -lastDays), to);
        } else {
            return new DatePeriod(defaultFrom, now);
        }
    }

    public static DatePeriod validateDatePeriod2(Date from, Date to) {
        from = eraseTime(from);
        if (to == null) {
            to = new Date();
        }
        to = addMinutes(addDays(eraseTime(to), 1), -1);
        if (from != null && to != null && from.after(to)) {
            return new DatePeriod(eraseTime(to), addMinutes(addDays(eraseTime(from), 1), -1));
        } else {
            return new DatePeriod(eraseTime(from), addMinutes(addDays(eraseTime(to), 1), -1));
        }
    }

    public static String convertReceiptDate(String date) {
        if (!StringUtils.isEmpty(date)) {
            String dateTrimmed = date.trim();
            if (dateTrimmed.length() > 7) {
                return dateTrimmed.substring(6, 8) + "." + dateTrimmed.substring(4, 6)
                        + "." + dateTrimmed.substring(0, 4);
            } else {
                return dateTrimmed;
            }
        } else {
            return "";
        }
    }

    public static String convertReceiptTime(String dateTime) {
        if (!StringUtils.isEmpty(dateTime)) {
            String dateTimeTrimmed = dateTime.trim();
            if (dateTimeTrimmed.length() > 11) {
                return dateTimeTrimmed.substring(8, 10) + ":" + dateTimeTrimmed.substring(10, 12);
            } else {
                return dateTimeTrimmed;
            }
        } else {
            return "";
        }
    }

    public static java.sql.Date toSqlDate(Date date) {
        if (date != null) {
            return new java.sql.Date(date.getTime());
        } else {
            return null;
        }
    }

    public static Timestamp toSqlTimestamp(Date date) {
        if (date != null) {
            return new Timestamp(date.getTime());
        } else {
            return null;
        }
    }

    public static XMLGregorianCalendar parseXMLGregorianCalendar(String date) throws DatatypeConfigurationException {
        if (date != null) {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
        } else {
            return null;
        }
    }


    public static XMLGregorianCalendar[] parseXMLGregorianCalendars(String[] values) throws DatatypeConfigurationException {
        if (values != null) {
            XMLGregorianCalendar[] result = new XMLGregorianCalendar[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = parseXMLGregorianCalendar(values[i]);
            }
            return result;
        } else {
            return null;
        }
    }

    public static Date parseXmlDate(String date) {
        if (!StringUtils.isEmpty(date)) {
            int len = date.length();
            SimpleDateFormat dateFormat;
            TimeZone timeZone;
            if (date.charAt(len - 1) == 'Z') {
                timeZone = TIME_ZONE_UTC;
                date = StringUtils.substring(date, 0, -1);
            } else if (date.charAt(len - 6) == '+') {
                timeZone = TimeZone.getTimeZone("GMT" + date.substring(len - 6));
                date = StringUtils.substring(date, 0, -6);
            } else {
                timeZone = TimeZone.getDefault();
            }
            len = date.length();
            if (len == 10) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            } else {
                if (len > 19 && date.charAt(19) == '.') {
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                } else {
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                }
                dateFormat.setTimeZone(timeZone);
            }
            try {
                return dateFormat.parse(date);
            } catch (ParseException e) {
                log.error("Cant parse xml date '" + date + "'", e);
            }
        }
        return null;
    }

    public static String formatXmlDate(Date date) {
        return formatXmlDate(date, null);
    }

    public static String formatXmlDate(Date date, TimeZone timeZone) {
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dateFormat.setTimeZone(timeZone);
        StringBuilder result = new StringBuilder();
        if (date != null) {
            result.append(dateFormat.format(date));
            int minutesOffset = (timeZone.getRawOffset()) / 60000;
            if (minutesOffset == 0) {
                result.append('Z');
            } else {
                if (minutesOffset > 0) {
                    result.append('+');
                } else {
                    result.append('-');
                }
                NumberFormat numberFormat = new DecimalFormat("00");
                result.append(numberFormat.format(minutesOffset / 60));
                result.append(':');
                result.append(numberFormat.format(minutesOffset % 60));
            }
        }
        return result.toString();
    }

    public static Date beginOfDay(Date date) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        } else {
            return null;
        }
    }

    public static Date endOfDay(Date date) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            return calendar.getTime();
        } else {
            return null;
        }
    }

    public static XMLGregorianCalendar toXmlDate(Date date) {
        return toXmlDate(date, null);
    }

    public static XMLGregorianCalendar toXmlDate(Date date, TimeZone toTimezone) {
        if (date != null) {
            DatatypeFactory dataTypeFactory;
            try {
                dataTypeFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException(e);
            }
            GregorianCalendar gc = new GregorianCalendar();
            if (toTimezone != null) {
                Date localDate = convertDate(date, toTimezone, TimeZone.getDefault());
                gc.setTimeInMillis(localDate.getTime());
            } else {
                gc.setTimeInMillis(date.getTime());
            }
            return dataTypeFactory.newXMLGregorianCalendar(gc);
        } else {
            return null;
        }
    }

    public static Date fromXmlDate(XMLGregorianCalendar xmlDate) {
        if (xmlDate != null) {
            return xmlDate.toGregorianCalendar().getTime();
        } else {
            return null;
        }
    }

    public static int compareDates(Date d1, Date d2) {
        if (d1 == null && d2 == null) {
            return 0;
        } else if (d1 == null) {
            return -1;
        } else if (d2 == null) {
            return 1;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d1);
            Integer y1 = calendar.get(Calendar.YEAR) * 10000 + calendar.get(Calendar.MONTH) * 100 + calendar.get(Calendar.DATE);
            calendar.setTime(d2);
            Integer y2 = calendar.get(Calendar.YEAR) * 10000 + calendar.get(Calendar.MONTH) * 100 + calendar.get(Calendar.DATE);
            return y1.compareTo(y2);
        }
    }

    public static Number asNumber(Date d) {
        if (d != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            return calendar.get(
                    Calendar.YEAR) * 10000
                    + (calendar.get(Calendar.MONTH) + 1) * 100
                    + calendar.get(Calendar.DATE);
        } else {
            return null;
        }
    }

    public static Date fromNumber(Number n) {
        if (n != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(0L);
            int year = n.intValue() / 10000;
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, (n.intValue() - year * 10000) / 100);
            calendar.set(Calendar.DATE, n.intValue() % 100);
            return calendar.getTime();
        } else {
            return null;
        }
    }


}
