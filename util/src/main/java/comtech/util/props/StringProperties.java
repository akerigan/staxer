package comtech.util.props;

import comtech.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 08.09.2009
 * Time: 15:44:14
 */
public abstract class StringProperties {

    static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public abstract String get(String name);

    public String get(String name, String defaultValue) {
        String value = validate(name);
        return value != null ? value : defaultValue;
    }

    public String validate(String name) {
        return StringUtils.notEmptyTrimmedElseNull(get(name));
    }

    public boolean exists(String name) {
        return validate(name) != null;
    }

    public int getInteger(String name, int defaultValue) {
        String value = validate(name);
        if (value != null) {
            return Integer.parseInt(value);
        } else {
            return defaultValue;
        }
    }

    public int getInteger(String name) {
        return getInteger(name, 0);
    }

    public long getLong(String name, long defaultValue) {
        String value = validate(name);
        if (value != null) {
            return Long.parseLong(value);
        } else {
            return defaultValue;
        }
    }

    public long getLong(String name) {
        return getLong(name, 0);
    }

    public double getDouble(String name) {
        return getDouble(name, 0);
    }

    public double getDouble(String name, double defaultValue) {
        String value = validate(name);
        return value != null ? Double.parseDouble(value) : defaultValue;
    }

    public float getFloat(String name) {
        return getFloat(name, 0);
    }

    public float getFloat(String name, float defaultValue) {
        String value = validate(name);
        return value != null ? Float.parseFloat(value) : defaultValue;
    }

    public boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        String value = validate(name);
        return value != null ? "true".equals(value) || "1".equals(value) : defaultValue;
    }

    public Date getDate(String name, String format) {
        String value = validate(name);
        if (value != null) {
            try {
                SimpleDateFormat dateFormat;
                if (format == null) {
                    dateFormat = DEFAULT_DATE_FORMAT;
                } else {
                    dateFormat = new SimpleDateFormat(format);
                }
                return dateFormat.parse(value);
            } catch (ParseException ignored) {
            }
        }
        return null;
    }

    public List<String> getSplitted(String name) {
        return getSplitted(name, StringUtils.DEFAULT_SPLIT_PATTERN);
    }

    public List<String> getSplitted(String name, String pattern) {
        String value = validate(name);
        return value != null ? Arrays.asList(value.split(pattern)) : null;
    }

}
