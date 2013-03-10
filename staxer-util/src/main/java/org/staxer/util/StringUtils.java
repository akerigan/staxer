package org.staxer.util;

import org.apache.commons.codec.binary.Base64;
import org.staxer.util.xml.StaxerXmlStreamException;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 28.04.2008
 * Time: 10:44:23
 */
public class StringUtils {

    public static final String REG_EXP_LATINIC = "^[a-zA-Z\\s\\']+$";
    public static final String REG_EXP_CYRILLIC = "^[а-яА-Я\\s\\']+$";
    public static final String ANTI_XSS_PATTERN = "[<>=\\n]";
    public static final String DEFAULT_SPLIT_PATTERN = "\\s*[;,/\\\\]\\s*";
    private static final Map<String, String> CAPITALIZE_SPLIT_PATTERNS_MAP = new HashMap<String, String>();
    private static final char[] HEX_BASE = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
    private static final Map<Character, String> TRANSLITERATE_MAP;
    private static final char[] CYRILLIC_CHARS = new char[]{
            'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о',
            'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я',
            'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О',
            'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'
    };
    private static final String[] LATIN_CHARS = new String[]{
            "a", "b", "v", "g", "d", "e", "yo", "zh", "z", "i", "y", "k", "l", "m", "n", "o",
            "p", "r", "s", "t", "u", "f", "kh", "ts", "ch", "sh", "shch", "\"", "y", "'", "e", "yu", "ya",
            "A", "B", "V", "G", "D", "E", "Yo", "Zh", "Z", "I", "Y", "K", "L", "M", "N", "O",
            "P", "R", "S", "T", "U", "F", "Kh", "Ts", "Ch", "Sh", "Shch", "\"", "Y", "'", "E", "Yu", "Ya"
    };

    static {
        CAPITALIZE_SPLIT_PATTERNS_MAP.put(" ", "\\s+");
        CAPITALIZE_SPLIT_PATTERNS_MAP.put("-", "\\s*-\\s*");
        CAPITALIZE_SPLIT_PATTERNS_MAP.put("_", "\\s*_\\s*");
        CAPITALIZE_SPLIT_PATTERNS_MAP.put(".", "\\s*.\\s*");
        CAPITALIZE_SPLIT_PATTERNS_MAP.put(",", "\\s*,\\s*");
        CAPITALIZE_SPLIT_PATTERNS_MAP.put(":", "\\s*:\\s*");

        TRANSLITERATE_MAP = new HashMap<Character, String>();
        for (int i = 0; i < CYRILLIC_CHARS.length; i++) {
            char ch = CYRILLIC_CHARS[i];
            String str = LATIN_CHARS[i];
            TRANSLITERATE_MAP.put(ch, str);
        }
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static String notEmptyElseNull(String value) {
        return isEmpty(value) ? null : value;
    }

    public static String notEmptyTrimmedElseNull(String value) {
        return isEmpty(value) ? null : value.trim();
    }

    public static String notEmptyElseDefault(String value, String defaultValue) {
        return isEmpty(value) ? defaultValue : value;
    }

    public static String notEmptyTrimmedElseDefault(String value, String defaultValue) {
        return isEmpty(value) ? defaultValue : value.trim();
    }

    public static int parseInt(String value, int defaultValue) {
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static Integer parseIntInstance(String value, Integer defaultValue) {
        if (value != null) {
            try {
                return Integer.decode(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static Long parseLongInstance(String value, Long defaultValue) {
        if (value != null) {
            try {
                return Long.decode(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static float parseFloat(String value, float defaultValue) {
        if (value != null) {
            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static double parseDouble(String value, double defaultValue) {
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static boolean parseBoolean(String value, boolean defaultValue) {
        return value != null
                ? "true".equalsIgnoreCase(value.trim()) || parseInt(value, 0) > 0
                : defaultValue;
    }

    public static int[] parseInts(String[] values, int defaultValue) {
        if (values != null) {
            int[] result = new int[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = parseInt(values[i], defaultValue);
            }
            return result;
        } else {
            return null;
        }
    }

    public static double[] parseDoubles(String[] values, double defaultValue) {
        if (values != null) {
            double[] result = new double[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = parseDouble(values[i], defaultValue);
            }
            return result;
        } else {
            return null;
        }
    }

    public static boolean[] parseBooleans(String[] values, boolean defaultValue) {
        if (values != null) {
            boolean[] result = new boolean[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = parseBoolean(values[i], defaultValue);
            }
            return result;
        } else {
            return null;
        }
    }

    public static <T> String join(T[] objs, String delimiter) {
        if (objs != null) {
            return join(objs, 0, objs.length, delimiter);
        } else {
            return null;
        }
    }

    public static <T> String join(T[] objs, int startIdx, int count, String delimiter) {
        if (objs != null) {
            StringBuilder builder = new StringBuilder();

            int endIdx = Math.min(objs.length, startIdx + count) - 1;
            for (int i = Math.max(0, startIdx); i <= endIdx; ++i) {
                builder.append(objs[i]);
                if (i != endIdx) {
                    builder.append(delimiter);
                }
            }

            return builder.toString();
        } else {
            return null;
        }
    }

    public static <T> String join(Collection<T> objs, String delimiter) {
        if (objs != null) {
            StringBuilder builder = new StringBuilder();

            for (Iterator<T> it = objs.iterator(); it.hasNext(); ) {
                T obj = it.next();
                if (obj != null) {
                    builder.append(obj);
                    if (delimiter != null && it.hasNext()) {
                        builder.append(delimiter);
                    }
                }
            }

            return builder.toString();
        } else {
            return null;
        }
    }

    public static <T> String join(Collection<T> objs, String delimiter, String prefix, String suffix) {
        if (objs != null) {
            StringBuilder builder = new StringBuilder();

            for (Iterator<T> it = objs.iterator(); it.hasNext(); ) {
                T obj = it.next();
                if (obj != null) {
                    if (prefix != null) {
                        builder.append(prefix);
                    }
                    builder.append(obj);
                    if (suffix != null) {
                        builder.append(suffix);
                    }
                    if (delimiter != null && it.hasNext()) {
                        builder.append(delimiter);
                    }
                }
            }

            return builder.toString();
        } else {
            return null;
        }
    }

    public static String capitalize(String src) {
        if (src != null) {
            return src.substring(0, 1).toUpperCase() + src.substring(1);
        } else {
            return null;
        }
    }

    public static String capitalize2(String src, boolean useDelimiters) {
        if (src != null) {
            StringBuilder result = new StringBuilder(src);
            boolean processed = false;
            for (String patternReplacement : CAPITALIZE_SPLIT_PATTERNS_MAP.keySet()) {
                String[] splitted = result.toString().split(CAPITALIZE_SPLIT_PATTERNS_MAP.get(patternReplacement));
                if (splitted.length > 1) {
                    result = new StringBuilder();
                    processed = true;
                    for (String subString : splitted) {
                        if (useDelimiters && result.length() != 0) {
                            result.append(patternReplacement);
                        }
                        result.append(capitalize(subString));
                    }
                    break;
                }
            }
            if (processed) {
                return result.toString();
            } else {
                return capitalize(src);
            }
        } else {
            return null;
        }
    }

    public static String capitalize3(String src) {
        if (src != null) {
            String[] splitted = src.split("\\s*[\\s\\(\\)_-]+\\s*");
            if (splitted.length > 1) {
                StringBuilder result = new StringBuilder();
                for (String subString : splitted) {
                    result.append(capitalize(subString));
                }
                return result.toString();
            } else {
                return capitalize(src);
            }
        } else {
            return null;
        }
    }

    public static String toEnumName(String src) {
        if (src != null) {
            String[] splitted = src.split("\\s*[\\s\\(\\)_-]+\\s*");
            StringBuilder result = new StringBuilder();
            for (String subString : splitted) {
                if (result.length() > 0) {
                    result.append('_');
                }
                result.append(toEnumName2(subString));
            }
            return result.toString();
        } else {
            throw new IllegalArgumentException("Argument is empty");
        }
    }

    public static String toEnumName2(String src) {
        if (src != null) {
            boolean prevLower = false;
            StringBuilder result = new StringBuilder();
            for (int i = 0, len = src.length(); i < len; ++i) {
                char ch = src.charAt(i);
                if (Character.isUpperCase(ch) && prevLower) {
                    result.append('_');
                    prevLower = false;
                } else if (Character.isLowerCase(ch)) {
                    prevLower = true;
                }
                result.append(Character.toUpperCase(ch));
            }
            return result.toString();
        } else {
            throw new IllegalArgumentException("Argument is empty");
        }
    }

    public static String decapitalize(String src) {
        return src.substring(0, 1).toLowerCase() + src.substring(1);
    }

    public static String resolveEntities(String source) {
        return source == null ? null : source.replaceAll("&nbsp;", " ")
                .replaceAll("&quot;", "\"")
                .replaceAll("&laquo;", "«")
                .replaceAll("&raquo;", "»")
                .replaceAll("&#8470;", "№");
    }

    public static String removeBirthdayFromName(String name) {
        if (name == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String part : name.trim().split("\\s+")) {
            if (part != null && part.length() > 0) {
                char c = part.charAt(0);
                if (c < '0' || c > '9') {
                    if (sb.length() > 0) {
                        sb.append(' ');
                    }
                    sb.append(part);
                }
            }
        }
        return sb.toString();
    }

    public static String preventXSS(String original) {
        if (original != null) {
            String decoded;
            try {
                decoded = URLDecoder.decode(original, "UTF-8");
            } catch (Exception e) {
                decoded = original;
            }
            String[] splitted = decoded.split(ANTI_XSS_PATTERN);
            StringBuilder sb = new StringBuilder();
            for (String s : splitted) {
                if (s != null) {
                    s = s.trim();
                    if (s.length() > 0) {
                        if (sb.length() > 0) {
                            sb.append("");
                        }
                        sb.append(s);
                    }
                }
            }
            return sb.toString();
        } else {
            return null;
        }
    }

    public static boolean validateEmail(String email) {
        //return email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$");
        return email.matches("^[a-zA-Z0-9,!#\\$%&'\\*\\+/=\\?\\^_`\\{\\|}~-]+(\\.[a-zA-Z0-9,!#\\$%&'\\*\\+/=\\?\\^_`\\{\\|}~-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.([a-zA-Z]{2,})$");
    }

// Encode a byte array as a hex encoded string.

    public static String hexencode(byte[] bs) {
        StringBuilder sb = new StringBuilder(bs.length * 2);
        for (byte element : bs) {
            int c = element & 0xFF;
            if (c < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(c));
        }

        return sb.toString();
    }

    public static String quotedPrintable(String s, String charset) throws UnsupportedEncodingException {
        if (s != null) {
            byte[] bs = s.getBytes(charset);
            StringBuilder sb = new StringBuilder(bs.length * 3);
            for (byte element : bs) {
                int c = element & 0xFF;
                sb.append('=');
                if (c < 16) {
                    sb.append('0');
                }
                sb.append(Integer.toHexString(c));
            }

            return sb.toString().toUpperCase();
        }
        return null;
    }

    public static boolean isDigital(String ss) {
        return ss.matches("^[\\-\\d]+$");
    }

    public static boolean isNumeric(String ss) {
        return ss.matches("^\\-?[\\d]+(\\.[\\d]+)?$");
    }

    public static String md5Base64(String text) throws StaxerXmlStreamException {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return Base64.encodeBase64String(md5.digest(text.getBytes())).trim();
        } catch (NoSuchAlgorithmException e) {
            throw new StaxerXmlStreamException("Can't initialize md5 algorithm", e);
        }
    }

    public static String md5Hex(String text) throws StaxerXmlStreamException {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return hexencode(md5.digest(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new StaxerXmlStreamException("Can't initialize md5 algorithm", e);
        }
    }

    public static String getHost(String url) {
        if (url != null) {
            String[] splitted = url.split("/+");
            if (splitted.length > 1) {
                return splitted[1];
            } else {
                return url;
            }
        } else {
            return null;
        }
    }

    public static String translate(String src, Map<Character, Character> translatorMap) {
        if (src == null) {
            return null;
        }
        StringBuilder result = new StringBuilder(src.length());
        int count = src.length();
        for (int i = 0; i < count; ++i) {
            char ch = src.charAt(i);
            if (translatorMap.containsKey(ch)) {
                result.append(translatorMap.get(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    public static String substring(String s, int startIdx) {
        if (s != null) {
            if (startIdx < 0) {
                startIdx += s.length();
            }
            try {
                return s.substring(startIdx);
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
        return null;
    }

    public static String substring(String s, int startIdx, int endIdx) {
        if (s != null) {
            int length = s.length();
            if (startIdx < 0) {
                startIdx += length;
            }
            if (endIdx < 0) {
                endIdx += length;
            }
            if (startIdx >= length) {
                startIdx = length;
            }
            if (endIdx >= length) {
                endIdx = length;
            }
            return s.substring(Math.min(startIdx, endIdx), Math.max(startIdx, endIdx));
        } else {
            return null;
        }
    }

    public static String join(String delimiter, String... values) {
        StringBuilder sb = new StringBuilder();
        if (values != null) {
            for (String value : values) {
                if (!isEmpty(value)) {
                    if (sb.length() > 0) {
                        sb.append(delimiter);
                    }
                    sb.append(value);
                }
            }
        }
        return sb.toString();
    }

    public static String joinUpper(String delimiter, String... values) {
        if (values != null) {
            for (int i = 0, len = values.length; i < len; ++i) {
                values[i] = StringUtils.toUpperCase(values[i]);
            }
        }
        return join(delimiter, values);
    }

    public static boolean isLatinic(String str) {
        return str != null && str.matches(REG_EXP_LATINIC);

    }

    public static boolean isCyrillic(String str) {
        return str != null && str.matches(REG_EXP_CYRILLIC);
    }

    public static int compare(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return 0;
        } else if (s1 == null) {
            return -1;
        } else if (s2 == null) {
            return 1;
        } else {
            return s1.compareTo(s2);
        }
    }

    public static String toUpperCase(String str) {
        if (str != null) {
            return str.toUpperCase();
        } else {
            return null;
        }
    }

    public static String toLowerCase(String str) {
        if (str != null) {
            return str.toLowerCase();
        } else {
            return null;
        }
    }

    public static boolean isAlpha(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEngPhrase(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if ((ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && ch != 32) {
                return false;
            }
        }
        return true;
    }

    public static String getInvertedHexColor(String color) {
        if (color != null) {
            int intColor;
            if (color.startsWith("0x")) {
                intColor = Integer.decode(color);
            } else {
                intColor = Integer.decode("0x" + color);
            }
            StringBuilder hexColor = new StringBuilder();
            while (intColor > 0) {
                int rem = intColor % 16;
                hexColor.insert(0, HEX_BASE[15 - rem]);
                intColor /= 16;
            }
            for (int i = hexColor.length(); i < 6; ++i) {
                hexColor.insert(0, 'f');
            }
            return hexColor.toString();
        } else {
            return null;
        }
    }

    public static boolean contains(String s, String substring) {
        return s != null && substring != null && s.toUpperCase().contains(substring.toUpperCase());
    }

    public static String[] split(String s, String splitRegex) {
        if (!StringUtils.isEmpty(s)) {
            return s.trim().split(splitRegex);
        } else {
            return null;
        }
    }

    public static String normalizePath(String path) {
        String trimmed = notEmptyTrimmedElseNull(path);
        if (trimmed != null && trimmed.charAt(0) != '/') {
            return '/' + trimmed;
        } else {
            return trimmed;
        }
    }

    public static String firstNotEmpty(String... ss) {
        if (ss != null) {
            for (String s : ss) {
                if (!isEmpty(s)) {
                    return s;
                }
            }
        }
        return null;
    }

    public static String firstNotEmptyTrimmed(String... ss) {
        if (ss != null) {
            for (String s : ss) {
                s = notEmptyTrimmedElseNull(s);
                if (s != null) {
                    return s;
                }
            }
        }
        return null;
    }

    public static int indexOf(CharSequence string, CharSequence subString) {
        if (string == null || subString == null) {
            return -1;
        }
        int stringLength = string.length();
        int subStringLength = subString.length();
        if (stringLength < subStringLength) {
            return -1;
        }

        long patternHash = 0;
        long currentHash = 0;

        for (int i = 0; i < subStringLength; i++) {
            patternHash += subString.charAt(i);
            currentHash += string.charAt(i);
        }

        int end = stringLength - subStringLength + 1;
        for (int i = 0; i < end; ++i) {
            if (patternHash == currentHash) {
                boolean matches = true;
                for (int j = 0, k = i; j < subStringLength; ++k, ++j) {
                    if (subString.charAt(j) != string.charAt(k)) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    return i;
                }
            }
            currentHash -= string.charAt(i);
            if (i != end - 1) {
                currentHash += string.charAt(i + subStringLength);
            }
        }

        return -1;
    }

    public static List<String> extractValues(String message, String template) {
        if (isEmpty(message) || isEmpty(template) || message.charAt(0) != message.charAt(0)) {
            return null;
        }
        List<String> result = new LinkedList<String>();
        StringBuilder value = new StringBuilder();
        for (int i = 0, j = 0, ml = message.length(), tl = template.length(); i < ml; ++i) {
            char cm = message.charAt(i);
            char ct;
            if (j < tl) {
                ct = template.charAt(j);
            } else {
                ct = 0;
            }
            if (cm == ct) {
                if (value.length() > 0) {
                    result.add(value.toString());
                    value = new StringBuilder();
                }
                ++j;
            } else {
                if (value.length() == 0) {
                    j += 2;
                }
                value.append(cm);
            }
        }
        if (value.length() > 0) {
            result.add(value.toString());
        }
        return result;
    }

    public static String toString(Object o) {
        if (o != null) {
            return o.toString();
        } else {
            return null;
        }
    }

    // замена кавычек в строке (на ХТМЛ-коды)
    public static String qoute(String str) {
        if (str == null) {
            return "";
        }
        String outstr = str;
        outstr = outstr.replaceAll("\'", "&quot;"); // одинарные
        outstr = outstr.replaceAll("\"", "&quot;"); // двойные простые
        outstr = outstr.replaceAll("«", "&laquo;"); // двойные парные угловые...
        outstr = outstr.replaceAll("»", "&raquo;");
        outstr = outstr.replaceAll("№", "&#8470;"); // знак номера - на всяк случай сюда же... %)
        return outstr;
    }

    // обратная замена (из ХТМЛ в обычный текст)
    public static String unqoute(String str) {
        if (str == null) {
            return "";
        }
        String outstr = str;
        outstr = outstr.replaceAll("&quot;", "\""); // двойные простые
        outstr = outstr.replaceAll("&laquo;", "«"); // двойные парные угловые...
        outstr = outstr.replaceAll("&raquo;", "»");
        outstr = outstr.replaceAll("&#8470;", "№"); // знак номера
        return outstr;
    }

    public static BigDecimal parseBigDecimal(String value, BigDecimal defaultValue) {
        if (value != null) {
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static Character parseCharacter(String value, Character defaultValue) {
        if (value != null) {
            try {
                return value.charAt(0);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static Float parseFloatInstance(String value, Float defaultValue) {
        if (value != null) {
            try {
                return Float.valueOf(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static Double parseDoubleInstance(String value, Double defaultValue) {
        if (value != null) {
            try {
                return Double.valueOf(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static Boolean parseBooleanInstance(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        } else if ("false".equalsIgnoreCase(value)) {
            return false;
        } else {
            return null;
        }
    }

    public static Boolean parseBooleanInstance(String value, Boolean defaultValue) {
        if (value != null) {
            return Boolean.valueOf(value);
        } else {
            return defaultValue;
        }
    }

    public static Integer[] parseIntInstances(String[] values, Integer defaultValue) {
        if (values != null) {
            Integer[] result = new Integer[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = parseIntInstance(values[i], defaultValue);
            }
            return result;
        } else {
            return null;
        }
    }

    public static Double[] parseDoubleInstances(String[] values, Double defaultValue) {
        if (values != null) {
            Double[] result = new Double[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = parseDoubleInstance(values[i], defaultValue);
            }
            return result;
        } else {
            return null;
        }
    }

    public static Float[] parseFloatInstances(String[] values, Float defaultValue) {
        if (values != null) {
            Float[] result = new Float[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = parseFloatInstance(values[i], defaultValue);
            }
            return result;
        } else {
            return null;
        }
    }

    public static Boolean[] parseBooleanInstances(String[] values, Boolean defaultValue) {
        if (values != null) {
            Boolean[] result = new Boolean[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = parseBooleanInstance(values[i], defaultValue);
            }
            return result;
        } else {
            return null;
        }
    }

    public static BigDecimal[] parseBigDecimalInstances(String[] values, BigDecimal defaultValue) {
        if (values != null) {
            BigDecimal[] result = new BigDecimal[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = parseBigDecimal(values[i], defaultValue);
            }
            return result;
        } else {
            return null;
        }
    }

    public static Character[] parseCharacterInstances(String[] values, Character defaultValue) {
        if (values != null) {
            Character[] result = new Character[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = parseCharacter(values[i], defaultValue);
            }
            return result;
        } else {
            return null;
        }
    }

    public static String normalizePhone(String phone) {
        if (isEmpty(phone)) {
            return null;
        }
        return removeChars(phone, ' ', '\t', '\n', '\r');
    }

    public static String removeChars(String s, char... chars) {
        if (s != null) {
            Set<Character> set = new HashSet<Character>();
            for (char ch : chars) {
                set.add(ch);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0, len = s.length(); i < len; ++i) {
                char ch = s.charAt(i);
                if (!set.contains(ch)) {
                    sb.append(ch);
                }
            }
            return sb.toString();
        } else {
            return null;
        }
    }

    public static byte[] decodeBase64(String base64String) {
        if (base64String != null) {
            return Base64.decodeBase64(base64String);
        } else {
            return null;
        }
    }

    public static String escapeHtml(String htmlString) {
        if (!isEmpty(htmlString)) {
            StringBuilder result = new StringBuilder();
            char[] chars = htmlString.toCharArray();
            for (int i = 0, size = chars.length; i < size; ++i) {
                char ch = chars[i];
                switch (ch) {
                    case '<':
                        result.append("&lt;");
                        break;
                    case '>':
                        result.append("&gt;");
                        break;
                    case '&':
                        result.append("&amp;");
                        break;
                    default:
                        result.append(ch);
                }
            }
            return result.toString();
        } else {
            return null;
        }
    }

    public static String unescapeHtml(String escapedHtml) {
        if (!isEmpty(escapedHtml)) {
            StringBuilder result = new StringBuilder();
            StringBuilder entity = null;
            char[] chars = escapedHtml.toCharArray();
            for (int i = 0, length = chars.length; i < length; ++i) {
                char ch = chars[i];
                if (ch == '&') {
                    entity = new StringBuilder();
                } else if (ch == ';') {
                    if (entity != null) {
                        String entityS = entity.toString();
                        if ("gt".equals(entityS)) {
                            result.append('>');
                        } else if ("lt".equals(entityS)) {
                            result.append('<');
                        } else if ("amp".equals(entityS)) {
                            result.append('&');
                        } else {
                            result.append(entityS);
                        }
                        entity = null;
                    } else {
                        result.append(ch);
                    }
                } else if (entity != null) {
                    entity.append(ch);
                } else {
                    result.append(ch);
                }
            }
            return result.toString();
        } else {
            return null;
        }
    }

    public static String resolveSystemProperties(String s) {
        if (!isEmpty(s)) {
            StringBuilder result = new StringBuilder();
            StringBuilder property = null;
            char[] chars = s.toCharArray();
            for (int i = 0, length = chars.length; i < length; ++i) {
                char ch = chars[i];
                if (ch == '$') {
                    if (i < (length - 1) && chars[i + 1] == '{') {
                        property = new StringBuilder();
                        i += 1;
                    } else {
                        result.append(ch);
                    }
                } else if (ch == '}') {
                    if (property != null) {
                        result.append(System.getProperty(property.toString(), ""));
                        property = null;
                    } else {
                        result.append(ch);
                    }
                } else if (property != null) {
                    property.append(ch);
                } else {
                    result.append(ch);
                }
            }
            return result.toString();
        } else {
            return null;
        }
    }

    public static String escapeJson(String value) {
        return escapeJson(value, false);
    }

    public static String escapeJson(String value, boolean addQuotes) {
        if (!isEmpty(value)) {
            StringBuilder result = new StringBuilder();
            result.append('"');
            char[] chars = value.toCharArray();
            for (int i = 0, size = chars.length; i < size; ++i) {
                char ch = chars[i];
                switch (ch) {
                    case '"':
                        result.append("\\\"");
                        break;
                    case '\\':
                        result.append("\\\\");
                        break;
                    case '\n':
                        result.append("\\n");
                        break;
                    default:
                        result.append(ch);
                }
            }
            result.append('"');
            return result.toString();
        } else if (addQuotes) {
            return "\"\"";
        } else {
            return "";
        }
    }

    public static String createParametersString(
            String prefix, List<String> params
    ) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder(prefix);
        if (params != null && !params.isEmpty()) {
            boolean odd = true;
            for (String additionalParam : params) {
                if (odd) {
                    if (result.indexOf("?") < 0) {
                        result.append("?");
                    } else {
                        result.append("&");
                    }
                    result.append(URLEncoder.encode(additionalParam, "UTF-8"));
                    odd = false;
                } else {
                    result.append("=");
                    if (additionalParam != null) {
                        result.append(URLEncoder.encode(additionalParam, "UTF-8"));
                    }
                    odd = true;
                }
            }
        }
        return result.toString();
    }

    public static String transliterate(String str) {
        if (str != null) {
            StringBuilder result = new StringBuilder();
            for (int i = 0, length = str.length(); i < length; i += 1) {
                char ch = str.charAt(i);
                String latCh = TRANSLITERATE_MAP.get(ch);
                if (latCh != null) {
                    result.append(latCh);
                } else {
                    result.append(ch);
                }
            }
            return result.toString();
        }
        return null;
    }

    public static String escapeTabsNewLines(String value) {
        if (value != null) {
            StringBuilder result = new StringBuilder();
            for (int i = 0, length = value.length(); i < length; i += 1) {
                char ch = value.charAt(i);
                if (ch == '\n') {
                    result.append("\\n");
                } else if (ch == '\t') {
                    result.append("\\t");
                } else {
                    result.append(ch);
                }
            }
            return result.toString();
        } else {
            return null;
        }
    }
}
