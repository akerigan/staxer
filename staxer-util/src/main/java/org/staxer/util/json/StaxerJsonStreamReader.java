package org.staxer.util.json;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-12-30 13:35 (Europe/Moscow)
 */
public class StaxerJsonStreamReader {

    private JsonStreamReader reader;
    private int lastEvent;

    public StaxerJsonStreamReader(JsonStreamReader reader) {
        this.reader = reader;
    }

    public StaxerJsonStreamReader(Reader reader) {
        this.reader = new JsonStreamReader(reader);
    }

    public int getLevel() {
        return reader.getLevel();
    }

    public boolean readObjectStart() throws StaxerJsonStreamException {
        do {
            if (lastEvent == JsonStreamReader.EVENT_OBJECT_START) {
                return true;
            }
        } while (readNext());
        return false;
    }

    public boolean readObjectStart(int objectLevel) throws StaxerJsonStreamException {
        do {
            if (lastEvent == JsonStreamReader.EVENT_OBJECT_START && reader.getLevel() == objectLevel) {
                return true;
            }
        } while (readNext());
        return false;
    }

    public boolean objectEnded(int objectLevel) throws StaxerJsonStreamException {
        return lastEvent == JsonStreamReader.EVENT_OBJECT_END && reader.getLevel() <= objectLevel;
    }

    public String readFieldName(int objectLevel) throws StaxerJsonStreamException {
        do {
            if (lastEvent == JsonStreamReader.EVENT_OBJECT_FIELD_NAME) {
                return reader.getStringValue();
            }
        } while (readNext() && !objectEnded(objectLevel));
        return null;
    }

    public String readFieldValue(int objectLevel) throws StaxerJsonStreamException {
        do {
            if (lastEvent == JsonStreamReader.EVENT_VALUE) {
                return reader.getStringValue();
            }
        } while (readNext() && !objectEnded(objectLevel));
        return null;
    }

    public boolean readListStart() throws StaxerJsonStreamException {
        do {
            if (lastEvent == JsonStreamReader.EVENT_LIST_START) {
                return true;
            }
        } while (readNext());
        return false;
    }

    public boolean readListStart(int listLevel) throws StaxerJsonStreamException {
        do {
            if (lastEvent == JsonStreamReader.EVENT_LIST_START && reader.getLevel() == listLevel) {
                return true;
            }
        } while (readNext());
        return false;
    }

    public boolean listStarted(int listLevel) throws StaxerJsonStreamException {
        return lastEvent == JsonStreamReader.EVENT_LIST_START && reader.getLevel() == listLevel;
    }

    public boolean listEnded(int listLevel) throws StaxerJsonStreamException {
        return lastEvent == JsonStreamReader.EVENT_LIST_END && reader.getLevel() <= listLevel;
    }

    public String readItemValue() throws StaxerJsonStreamException {
        while (readNext() && lastEvent != JsonStreamReader.EVENT_OBJECT_FIELD_NEXT) {
            if (lastEvent == JsonStreamReader.EVENT_VALUE) {
                return reader.getStringValue();
            }
        }
        return null;
    }

    public boolean readNext() throws StaxerJsonStreamException {
        try {
            lastEvent = reader.next();
            return lastEvent != JsonStreamReader.EVENT_END;
        } catch (IOException e) {
            throw new StaxerJsonStreamException(e);
        }
    }

}
