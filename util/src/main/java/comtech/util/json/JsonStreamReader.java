package comtech.util.json;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2010-12-06 17:51:43 (Europe/Moscow)
 */
public class JsonStreamReader {

    public static final int TYPE_STRING = 1;
    public static final int TYPE_NUMBER = 2;
    public static final int TYPE_BOOLEAN = 3;
    public static final int TYPE_NULL = 4;

    private static final char[] VALID_CHARS_TRUE = new char[]{'r', 'u', 'e'};
    private static final char[] VALID_CHARS_FALSE = new char[]{'a', 'l', 's', 'e'};
    private static final char[] VALID_CHARS_NULL = new char[]{'u', 'l', 'l'};

    public static final int EVENT_VALUE = 1;
    public static final int EVENT_OBJECT_START = 2;
    public static final int EVENT_OBJECT_FIELD_NAME = 3;
    public static final int EVENT_OBJECT_FIELD_VALUE = 4;
    public static final int EVENT_OBJECT_FIELD_NEXT = 5;
    public static final int EVENT_OBJECT_END = 6;
    public static final int EVENT_LIST_START = 7;
    public static final int EVENT_LIST_ITEM_NEXT = 8;
    public static final int EVENT_LIST_END = 9;
    public static final int EVENT_END = 10;

    private static final byte LEVEL_UNDEFINED = 0;
    private static final byte LEVEL_OBJECT = 1;
    private static final byte LEVEL_OBJECT_FIELD_NAME = 2;
    private static final byte LEVEL_OBJECT_FIELD_VALUE = 3;
    private static final byte LEVEL_LIST = 4;

    private Reader reader;

    private int line;
    private int col = -1;
    private int valueType;
    private StringBuilder value;

    private char ch;

    private byte[] levels = new byte[100];
    private byte level = -1;

    public JsonStreamReader(Reader reader) {
        this.reader = reader;
    }

    public int getValueType() {
        return valueType;
    }

    public String getStringValue() {
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    public int getIntValue() {
        if (value != null) {
            BigDecimal decimal = new BigDecimal(value.toString());
            return decimal.intValue();
        } else {
            return 0;
        }
    }

    public float getFloatValue() {
        if (value != null) {
            BigDecimal decimal = new BigDecimal(value.toString());
            return decimal.floatValue();
        } else {
            return 0;
        }
    }

    public Number getNumberValue() {
        if (value != null) {
            return new BigDecimal(value.toString());
        } else {
            return 0;
        }
    }

    public boolean getBooleanValue() {
        return value != null && Boolean.parseBoolean(value.toString());
    }

    public JsonObject getJsonObjectValue() {
        switch (valueType) {
            case TYPE_STRING:
                return new JsonObjectString(getStringValue());
            case TYPE_NUMBER:
                return new JsonObjectNumber(getNumberValue());
            case TYPE_BOOLEAN:
                return new JsonObjectBoolean(getBooleanValue());
        }
        return JsonObjectNull.getInstance();
    }

    public int next() throws IOException {
        value = null;
        if (valueType != TYPE_NUMBER || isWhiteSpace()) {
            read(true);
        }
        valueType = 0;
        if (ch == 0) {
            return EVENT_END;
        }
        switch (ch) {
            case '{':
                levels[++level] = LEVEL_OBJECT;
                return EVENT_OBJECT_START;
            case '}':
                if (levels[level] != LEVEL_OBJECT_FIELD_VALUE) {
                    throw new IllegalStateException("Unbalanced brace at (" + line + "," + col + ")");
                }
                levels[level--] = LEVEL_UNDEFINED;
                if (levels[level] != LEVEL_OBJECT) {
                    throw new IllegalStateException("Unbalanced brace at (" + line + "," + col + ")");
                }
                levels[level--] = LEVEL_UNDEFINED;
                return EVENT_OBJECT_END;
            case '[':
                levels[++level] = LEVEL_LIST;
                return EVENT_LIST_START;
            case ']':
                if (levels[level] != LEVEL_LIST) {
                    throw new IllegalStateException("Unbalanced square bracket at (" + line + "," + col + ")");
                }
                levels[level--] = LEVEL_UNDEFINED;
                return EVENT_LIST_END;
            case ':':
                if (levels[level] != LEVEL_OBJECT_FIELD_NAME) {
                    throw new IllegalStateException("Unexpected symbol ':' at (" + line + "," + col + ")");
                }
                levels[level] = LEVEL_OBJECT_FIELD_VALUE;
                return EVENT_OBJECT_FIELD_VALUE;
            case ',':
                if (levels[level] == LEVEL_OBJECT_FIELD_VALUE) {
                    levels[level--] = LEVEL_UNDEFINED;
                    return EVENT_OBJECT_FIELD_NEXT;
                } else if (levels[level] == LEVEL_LIST) {
                    return EVENT_LIST_ITEM_NEXT;
                } else {
                    throw new IllegalStateException("Unexpected symbol ',' at (" + line + "," + col + ")");
                }
            default:
                if (level >= 0 && levels[level] == LEVEL_OBJECT) {
                    readValue();
                    levels[++level] = LEVEL_OBJECT_FIELD_NAME;
                    return EVENT_OBJECT_FIELD_NAME;
                } else {
                    readValue();
                    return EVENT_VALUE;
                }
        }
    }

    private void read(boolean ignoreWhiteSpaces) throws IOException {
        int i;
        do {
            i = reader.read();
            if (i == -1) {
                ch = 0;
            } else {
                ch = (char) i;
            }
            if (ch == '\n') {
                col = 0;
                line += 1;
            } else if (ch != 0) {
                col += 1;
            }
        } while (ignoreWhiteSpaces && (isWhiteSpace()));
    }

    private boolean isWhiteSpace() {
        return ch == ' ' || ch == '\t' || ch == '\n';
    }

    private boolean readValue() throws IOException {
        switch (ch) {
            case '"':
                value = new StringBuilder();
                read(false);
                while (ch != '"' && ch != 0) {
                    value.append(ch);
                    read(false);
                }
                valueType = TYPE_STRING;
                return true;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                value = new StringBuilder();
                int dotCount = 0;
                while ((ch >= '0' && ch <= '9') || ch == '.') {
                    if (ch == '.') {
                        dotCount += 1;
                        if (dotCount > 1) {
                            throw new IllegalStateException("Invalid number too many dots at (" + line + "," + col + ")");
                        }
                    }
                    value.append(ch);
                    read(false);
                }
                valueType = TYPE_NUMBER;
                return true;
            case 't':
                value = new StringBuilder();
                value.append(ch);
                for (int i = 0, len = VALID_CHARS_TRUE.length; i < len; ++i) {
                    read(false);
                    if (ch != VALID_CHARS_TRUE[i]) {
                        throw new IllegalStateException("Invalid 'true' value at (" + line + "," + col + ")");
                    }
                    value.append(ch);
                }
                valueType = TYPE_BOOLEAN;
                return true;
            case 'f':
                value = new StringBuilder();
                value.append(ch);
                for (int i = 0, len = VALID_CHARS_FALSE.length; i < len; ++i) {
                    read(false);
                    if (ch != VALID_CHARS_FALSE[i]) {
                        throw new IllegalStateException("Invalid 'false' value at (" + line + "," + col + ")");
                    }
                    value.append(ch);
                }
                valueType = TYPE_BOOLEAN;
                return true;
            case 'n':
                value = new StringBuilder();
                value.append(ch);
                for (int i = 0, len = VALID_CHARS_NULL.length; i < len; ++i) {
                    read(false);
                    if (ch != VALID_CHARS_NULL[i]) {
                        throw new IllegalStateException("Invalid 'null' value at (" + line + "," + col + ")");
                    }
                    value.append(ch);
                }
                valueType = TYPE_NULL;
                return true;
            default:
                throw new IllegalStateException("Cant continue parsing at (" + line + "," + col + "): indefinite state");
        }
    }

}
