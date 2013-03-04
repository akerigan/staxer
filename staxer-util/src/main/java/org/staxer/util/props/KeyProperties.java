package org.staxer.util.props;

import org.staxer.util.StringUtils;

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
public abstract class KeyProperties<T> {

    static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public abstract String get(T key);

    public String get(T key, String defaultValue) {
        String value = validate(key);
        return value != null ? value : defaultValue;
    }

    public String validate(T key) {
        return StringUtils.notEmptyTrimmedElseNull(get(key));
    }

    public boolean exists(T key) {
        return validate(key) != null;
    }

    public int getInteger(T key, int defaultValue) {
        String value = validate(key);
        if (value != null) {
            return Integer.parseInt(value);
        } else {
            return defaultValue;
        }
    }

    public int getInteger(T key) {
        return getInteger(key, 0);
    }

    public long getLong(T key, long defaultValue) {
        String value = validate(key);
        if (value != null) {
            return Long.parseLong(value);
        } else {
            return defaultValue;
        }
    }

    public long getLong(T key) {
        return getLong(key, 0);
    }

    public double getDouble(T key) {
        return getDouble(key, 0);
    }

    public double getDouble(T key, double defaultValue) {
        String value = validate(key);
        return value != null ? Double.parseDouble(value) : defaultValue;
    }

    public float getFloat(T key) {
        return getFloat(key, 0);
    }

    public float getFloat(T key, float defaultValue) {
        String value = validate(key);
        return value != null ? Float.parseFloat(value) : defaultValue;
    }

    public boolean getBoolean(T key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(T key, boolean defaultValue) {
        String value = validate(key);
        return value != null ? "true".equals(value) || "1".equals(value) : defaultValue;
    }

    public Date getDate(T key, String format) {
        String value = validate(key);
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

    public List<String> getSplitted(T key) {
        return getSplitted(key, StringUtils.DEFAULT_SPLIT_PATTERN);
    }

    public List<String> getSplitted(T key, String pattern) {
        String value = validate(key);
        return value != null ? Arrays.asList(value.split(pattern)) : null;
    }

}
