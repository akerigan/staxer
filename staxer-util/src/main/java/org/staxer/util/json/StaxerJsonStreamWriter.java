package org.staxer.util.json;

import org.staxer.util.StringUtils;

import java.io.*;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-12-29 12:21 (Europe/Moscow)
 */
public class StaxerJsonStreamWriter {

    private static final int STATE_DOCUMENT_START = 1;
    private static final int STATE_DOCUMENT_END = 2;
    private static final int STATE_OBJECT_STARTED = STATE_DOCUMENT_END * 2;
    private static final int STATE_OBJECT_ENTRY_WRITED = STATE_OBJECT_STARTED * 2;
    private static final int STATE_OBJECT_ENDED = STATE_OBJECT_ENTRY_WRITED * 2;
    private static final int STATE_LIST_STARTED = STATE_OBJECT_ENDED * 2;
    private static final int STATE_LIST_ENDED = STATE_LIST_STARTED * 2;
    private static final int STATE_LIST_ITEM_WRITED = STATE_LIST_ENDED * 2;
    private Writer writer;
    private int indentSize;
    private int state;
    private int level;
    private int[] nesting = new int[100];

    public StaxerJsonStreamWriter(
            OutputStream outputStream, String charset, int indentSize
    ) throws StaxerJsonStreamException {
        try {
            this.writer = new OutputStreamWriter(outputStream, charset);
            this.indentSize = indentSize;
        } catch (UnsupportedEncodingException e) {
            throw new StaxerJsonStreamException(e);
        }
    }

    public StaxerJsonStreamWriter(Writer writer, int indentSize) {
        this.writer = writer;
        this.indentSize = indentSize;
    }

    private void writeIndents() throws StaxerJsonStreamException {
        try {
            if (indentSize > 0
                    && ((state & (
                    STATE_OBJECT_STARTED
                            | STATE_OBJECT_ENTRY_WRITED
                            | STATE_LIST_STARTED
                            | STATE_LIST_ITEM_WRITED
            )) > 0)) {
                writer.write("\n");
                for (int i = 0; i < level; ++i) {
                    for (int j = 0; j < indentSize; ++j) {
                        writer.write(' ');
                    }
                }
            }
        } catch (IOException e) {
            throw new StaxerJsonStreamException(e);
        }
    }

    public void startObject() throws StaxerJsonStreamException {
        startComposite('{', STATE_OBJECT_STARTED);
    }

    public void endObject() throws StaxerJsonStreamException {
        endComposite('}', STATE_OBJECT_ENDED);
    }

    public void startList() throws StaxerJsonStreamException {
        startComposite('[', STATE_LIST_STARTED);
    }

    public void endList() throws StaxerJsonStreamException {
        endComposite(']', STATE_LIST_ENDED);
    }

    private void startComposite(
            char ch, int startState
    ) throws StaxerJsonStreamException {
        try {
            if (state != STATE_DOCUMENT_END) {
                writeIndents();
                writer.write(ch);
                state = startState;
                nesting[level] = startState;
                level += 1;
            }
        } catch (IOException e) {
            throw new StaxerJsonStreamException(e);
        }
    }

    private void endComposite(char ch, int endState) throws StaxerJsonStreamException {
        try {
            if ((state & (STATE_DOCUMENT_END | STATE_DOCUMENT_START)) == 0) {
                level -= 1;
                writeIndents();
                writer.write(ch);
                if (level > 0) {
                    state = endState;
                } else {
                    state = STATE_DOCUMENT_END;
                }
            }
        } catch (IOException e) {
            throw new StaxerJsonStreamException(e);
        }
    }

    public void objectEntry(String fieldName, String fieldValue) throws StaxerJsonStreamException {
        if (fieldName != null && fieldValue != null) {
            objectEntryLocal(fieldName, StringUtils.escapeJson(fieldValue, true));
        }
    }

    public void objectEntry(String fieldName, Number fieldValue) throws StaxerJsonStreamException {
        if (fieldName != null && fieldValue != null) {
            objectEntryLocal(fieldName, fieldValue.toString());
        }
    }

    public void objectEntry(String fieldName, boolean fieldValue) throws StaxerJsonStreamException {
        if (fieldName != null) {
            objectEntryLocal(fieldName, Boolean.toString(fieldValue));
        }
    }

    private void objectEntryLocal(String fieldName, String fieldValue) throws StaxerJsonStreamException {
        try {
            if ((state & (STATE_OBJECT_STARTED | STATE_OBJECT_ENTRY_WRITED)) > 0) {
                if (state == STATE_OBJECT_ENTRY_WRITED) {
                    writer.write(", ");
                }
                writeIndents();
                writer.write('"');
                writer.write(fieldName);
                writer.write("\": ");
                writer.write(fieldValue);
                state = STATE_OBJECT_ENTRY_WRITED;
            }
        } catch (IOException e) {
            throw new StaxerJsonStreamException(e);
        }
    }

    public void objectEntry(String fieldName, StaxerWriteJson value) throws StaxerJsonStreamException {
        if (fieldName != null && value != null) {
            try {
                if ((state & (STATE_OBJECT_STARTED | STATE_OBJECT_ENTRY_WRITED)) > 0) {
                    if (state == STATE_OBJECT_ENTRY_WRITED) {
                        writer.write(", ");
                    }
                    writeIndents();
                    writer.write('"');
                    writer.write(fieldName);
                    writer.write("\": ");
                    value.writeJson(this);
                    state = STATE_OBJECT_ENTRY_WRITED;
                }
            } catch (IOException e) {
                throw new StaxerJsonStreamException(e);
            }
        }
    }

    public void objectEntryStartList(String fieldName) throws StaxerJsonStreamException {
        if (fieldName != null) {
            try {
                if ((state & (STATE_OBJECT_STARTED | STATE_OBJECT_ENTRY_WRITED)) > 0) {
                    if (state == STATE_OBJECT_ENTRY_WRITED) {
                        writer.write(", ");
                    }
                    writeIndents();
                    writer.write('"');
                    writer.write(fieldName);
                    writer.write("\": ");
                    startList();
                }
            } catch (IOException e) {
                throw new StaxerJsonStreamException(e);
            }
        }
    }

    public void objectEntryEndList() throws StaxerJsonStreamException {
        if ((state & (STATE_LIST_ITEM_WRITED)) > 0) {
            endList();
            state = STATE_OBJECT_ENTRY_WRITED;
        }
    }

    /*
        public void objectEntry(String fieldName, JsonObject value) throws StaxerJsonStreamException {
            if (fieldName != null && value != null) {
                try {
                    if ((state & (STATE_OBJECT_STARTED | STATE_OBJECT_ENTRY_WRITED)) > 0) {
                        if (state == STATE_OBJECT_ENTRY_WRITED) {
                            writer.write(", ");
                        }
                        writeIndents();
                        writer.write('"');
                        writer.write(fieldName);
                        writer.write("\": ");
                        JsonUtils.serialize(value, writer, indentSize > 0);
                        state = STATE_OBJECT_ENTRY_WRITED;
                    }
                } catch (Exception e) {
                    throw new StaxerJsonStreamException(e);
                }
            }
        }

    */
    public void listItem(String fieldValue) throws StaxerJsonStreamException {
        if (fieldValue != null) {
            listItemLocal(StringUtils.escapeJson(fieldValue, true));
        }
    }

    public void listItem(Number fieldValue) throws StaxerJsonStreamException {
        if (fieldValue != null) {
            listItemLocal(fieldValue.toString());
        }
    }

    public void listItem(boolean fieldValue) throws StaxerJsonStreamException {
        listItemLocal(Boolean.toString(fieldValue));
    }

    private void listItemLocal(String itemValue) throws StaxerJsonStreamException {
        if (itemValue != null) {
            try {
                if ((state & (STATE_LIST_STARTED | STATE_LIST_ITEM_WRITED)) > 0) {
                    if (state == STATE_LIST_ITEM_WRITED) {
                        writer.write(", ");
                    }
                    writeIndents();
                    writer.write(itemValue);
                    state = STATE_LIST_ITEM_WRITED;
                }
            } catch (IOException e) {
                throw new StaxerJsonStreamException(e);
            }
        }
    }

    public void listItem(StaxerWriteJson value) throws StaxerJsonStreamException {
        if (value != null) {
            try {
                if ((state & (STATE_LIST_STARTED | STATE_LIST_ITEM_WRITED)) > 0) {
                    if (state == STATE_LIST_ITEM_WRITED) {
                        writer.write(", ");
                    }
                    value.writeJson(this);
                    state = STATE_LIST_ITEM_WRITED;
                }
            } catch (IOException e) {
                throw new StaxerJsonStreamException(e);
            }
        }
    }

    public void close() throws StaxerJsonStreamException {
        try {
            while (level > 0) {
                if (nesting[level] == STATE_OBJECT_STARTED) {
                    endObject();
                } else if (nesting[level] == STATE_LIST_STARTED) {
                    endList();
                }
                level -= 1;
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new StaxerJsonStreamException(e);
        }
    }

}
