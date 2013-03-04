package org.staxer.util.json;

import java.io.IOException;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-04-08 12:46 (Europe/Moscow)
 */
public class JsonStreamWriter {

    private Appendable appendable;
    private boolean pretty;
    private int level = -1;
    private int[] itemsCount = new int[100];
    private boolean objectFieldStarted;

    public JsonStreamWriter(Appendable appendable, boolean pretty) {
        this.appendable = appendable;
        this.pretty = pretty;
    }

    public void startObject() throws IOException {
        if (!objectFieldStarted) {
            if (level >= 0) {
                if (itemsCount[level] > 0) {
                    appendable.append(',');
                }
                itemsCount[level] += 1;
            }
            level += 1;
            if (pretty) {
                appendable.append("\n");
                appendable.append(JsonUtils.INDENTS[level]);
            }
        } else {
            objectFieldStarted = false;
            level += 1;
        }
        appendable.append('{');
    }

    public void endObject() throws IOException {
        if (pretty) {
            appendable.append("\n");
            appendable.append(JsonUtils.INDENTS[level]);
        }
        appendable.append('}');
        itemsCount[level] = 0;
        level -= 1;
    }

    public void objectField(
            String fieldName, Object fieldValue
    ) throws JsonException {
        if (fieldValue != null) {
            try {
                if (itemsCount[level] > 0) {
                    appendable.append(',');
                }
                itemsCount[level] += 1;
                if (pretty) {
                    appendable.append("\n");
                    appendable.append(JsonUtils.INDENTS[level + 1]);
                }
                appendable.append('"');
                appendable.append(fieldName);
                appendable.append('"');
                appendable.append(':');
                JsonUtils.recursiveSerialize(fieldValue, appendable, level + 1, pretty);
            } catch (IOException e) {
                throw new JsonException(e);
            }
        }
    }

    public void objectFieldName(
            String fieldName
    ) throws IOException {
        if (itemsCount[level] > 0) {
            appendable.append(',');
        }
        itemsCount[level] += 1;
        if (pretty) {
            appendable.append("\n");
            appendable.append(JsonUtils.INDENTS[level + 1]);
        }
        appendable.append('"');
        appendable.append(fieldName);
        appendable.append('"');
        appendable.append(':');
        objectFieldStarted = true;
    }

    public void objectFieldValue(
            Object fieldValue
    ) throws JsonException {
        JsonUtils.recursiveSerialize(fieldValue, appendable, level + 1, pretty);
    }

    public void startList() throws IOException {
        if (!objectFieldStarted) {
            if (level >= 0) {
                if (itemsCount[level] > 0) {
                    appendable.append(',');
                }
                itemsCount[level] += 1;
            }
            level += 1;
            if (pretty) {
                appendable.append("\n");
                appendable.append(JsonUtils.INDENTS[level]);
            }
        } else {
            objectFieldStarted = false;
            level += 1;
        }
        appendable.append('[');
    }

    public void endList() throws IOException {
        if (pretty) {
            appendable.append("\n");
            appendable.append(JsonUtils.INDENTS[level]);
        }
        appendable.append(']');
        itemsCount[level] = 0;
        level -= 1;
    }

    public void listItem(
            Object item
    ) throws JsonException {
        if (item != null) {
            if (itemsCount[level] > 0) {
                try {
                    appendable.append(',');
                } catch (IOException e) {
                    throw new JsonException(e);
                }
            }
            itemsCount[level] += 1;
            JsonUtils.recursiveSerialize(item, appendable, level + 1, pretty);
        }
    }

    public void object(
            Object object
    ) throws JsonException {
        JsonUtils.serialize(object, appendable, level + 1, pretty);
    }

}
