package comtech.util.json;

import comtech.util.StringUtils;
import comtech.util.xml.StaxerXmlStreamException;
import comtech.util.xml.StaxerXmlStreamWriter;
import comtech.util.xml.XmlName;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2010-11-23 17:03:12 (Europe/Moscow)
 */
public class JsonUtils {

    public static final String DATE_FORMAT_DEFAULT = "dd.MM.yyyy HH:mm";
    public static final Set<String> DENIED_METHODS = new HashSet<String>(Arrays.asList(
            "getClass"
    ));
    public static final int INDENT = 2;
    public static final String[] INDENTS = new String[100];
    public static final XmlName XML_NAME_ITEM = new XmlName("item");

    private static final Map<Class, List<String>> methodsCache = new HashMap<Class, List<String>>();
    private static final Lock methodsLock = new ReentrantLock();

    static {
        StringBuilder sb = new StringBuilder(INDENT);
        for (int i = 0; i < INDENT; i++) {
            sb.append(' ');
        }
        String indentString = sb.toString();
        sb = new StringBuilder(INDENTS.length * INDENT);
        for (int i = 0; i < INDENTS.length; i++) {
            INDENTS[i] = sb.toString();
            sb.append(indentString);
        }
    }

    public static void serialize(
            Object o, Appendable a
    ) throws JsonException {
        serialize(o, a, 0, true);
    }

    public static void serialize(
            Object o, Appendable a, boolean pretty
    ) throws JsonException {
        serialize(o, a, 0, pretty);
    }

    public static void serialize(
            Object o, Appendable a, int level, boolean pretty
    ) throws JsonException {
        if (o != null) {
            if (o instanceof JsonObject) {
                recursiveSerialize((JsonObject) o, a, level, pretty);
            } else {
                recursiveSerialize(o, a, level, pretty);
            }
        } else {
            try {
                a.append(JsonObjectNull.NULL_JSON);
            } catch (IOException e) {
                throw new JsonException(e);
            }
        }
    }

    public static void recursiveSerialize(
            JsonObject o, Appendable appendable, int level, boolean pretty
    ) throws JsonException {
        try {
            if (o == null || (!o.isList() && o.isNull())) {
                appendable.append(JsonObjectNull.NULL_JSON);
            } else if (o.isSimple()) {
                ((JsonObjectSimple) o).toJsonString(appendable);
            } else if (o.isList()) {
                JsonObjectList listJson = (JsonObjectList) o;
                if (pretty) {
                    appendable.append("\n");
                    appendable.append(INDENTS[level]);
                }
                appendable.append('[');
                boolean notFirst = false;
                for (int i = 0, size = listJson.size(); i < size; ++i) {
                    if (notFirst) {
                        appendable.append(',');
                    } else {
                        notFirst = true;
                    }
                    recursiveSerialize(listJson.get(i), appendable, level + 1, pretty);
                }
                if (pretty) {
                    appendable.append("\n");
                    appendable.append(INDENTS[level]);
                }
                appendable.append(']');
            } else if (o.isMap()) {
                JsonObjectMap mapJson = (JsonObjectMap) o;
                Iterator<String> iterator = mapJson.fieldsNames();
                if (iterator == null) {
                    appendable.append('{');
                }
                if (iterator != null) {
                    if (pretty) {
                        appendable.append("\n");
                        appendable.append(INDENTS[level]);
                    }
                    appendable.append('{');
                    boolean notFirst = false;
                    while (iterator.hasNext()) {
                        String fieldName = iterator.next();
                        JsonObject fieldValue = mapJson.get(fieldName);
                        if (fieldValue != null) {
                            if (notFirst) {
                                appendable.append(',');
                            } else {
                                notFirst = true;
                            }
                            if (pretty) {
                                appendable.append("\n");
                                appendable.append(INDENTS[level + 1]);
                            }
                            appendable.append('"');
                            appendable.append(fieldName);
                            appendable.append('"');
                            appendable.append(':');
                            recursiveSerialize(fieldValue, appendable, level + 1, pretty);
                        }
                    }
                    if (pretty) {
                        appendable.append("\n");
                        appendable.append(INDENTS[level]);
                    }
                }
                appendable.append('}');
            } else if (o.isNull()) {
                appendable.append(JsonObjectNull.NULL_JSON);
            }
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static void recursiveSerialize(
            Object o, Appendable appendable, int level, boolean pretty
    ) throws JsonException {
        try {
            if (o == null) {
                appendable.append(JsonObjectNull.NULL_JSON);
                return;
            }
            Class<?> objClass = o.getClass();
            if (o instanceof Number || o instanceof Boolean) {
                appendable.append(o.toString());
            } else if (o instanceof String) {
                appendable.append('"');
                appendable.append(((String) o).replaceAll("\"", "\\\\\""));
                appendable.append('"');
            } else if (objClass.isEnum()) {
                appendable.append('"');
                appendable.append(o.toString());
                appendable.append('"');
            } else if (objClass.isArray()) {
                if (pretty) {
                    appendable.append("\n");
                    appendable.append(INDENTS[level]);
                }
                appendable.append('[');
                boolean notFirst = false;
                for (Object o1 : (Object[]) o) {
                    if (notFirst) {
                        appendable.append(',');
                    } else {
                        notFirst = true;
                    }
                    recursiveSerialize(o1, appendable, level + 1, pretty);
                }
                if (pretty) {
                    appendable.append("\n");
                    appendable.append(INDENTS[level]);
                }
                appendable.append(']');
            } else if (o instanceof Collection) {
                if (pretty) {
                    appendable.append("\n");
                    appendable.append(INDENTS[level]);
                }
                appendable.append('[');
                boolean notFirst = false;
                for (Object o1 : (Collection) o) {
                    if (notFirst) {
                        appendable.append(',');
                    } else {
                        notFirst = true;
                    }
                    recursiveSerialize(o1, appendable, level + 1, pretty);
                }
                if (pretty) {
                    appendable.append("\n");
                    appendable.append(INDENTS[level]);
                }
                appendable.append(']');
            } else if (o instanceof Map) {
                if (pretty) {
                    appendable.append("\n");
                    appendable.append(INDENTS[level]);
                }
                appendable.append('{');
                boolean notFirst = false;
                for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) o).entrySet()) {
                    if (entry.getValue() != null) {
                        if (notFirst) {
                            appendable.append(',');
                        } else {
                            notFirst = true;
                        }
                        if (pretty) {
                            appendable.append("\n");
                            appendable.append(INDENTS[level + 1]);
                        }
                        appendable.append('"');
                        Object key = entry.getKey();
                        if (key instanceof String) {
                            appendable.append((String) key);
                        } else {
                            appendable.append(key.toString());
                        }
                        appendable.append('"');
                        appendable.append(':');
                        recursiveSerialize(entry.getValue(), appendable, level + 1, pretty);
                    }
                }
                if (pretty) {
                    appendable.append("\n");
                    appendable.append(INDENTS[level]);
                }
                appendable.append('}');
            } else if (o instanceof Date) {
                appendable.append('"');
                DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DEFAULT);
                appendable.append(dateFormat.format((Date) o));
                appendable.append('"');
            } else {
                // not a System o
                if (objClass.getClassLoader() != null) {
                    if (pretty) {
                        appendable.append("\n");
                        appendable.append(INDENTS[level]);
                    }
                    appendable.append('{');
                    boolean notFirst = false;

                    List<String> methods;
                    methodsLock.lock();
                    try {
                        methods = methodsCache.get(objClass);
                        if (methods == null) {
                            methods = new LinkedList<String>();
                            for (Method method : objClass.getMethods()) {
                                String methodName = method.getName();
                                if (((methodName.startsWith("get") && methodName.length() > 3)
                                        || (methodName.startsWith("is") && methodName.length() > 2))
                                        && !DENIED_METHODS.contains(methodName)) {
                                    Class<?>[] parameters = method.getParameterTypes();
                                    if (parameters == null || parameters.length == 0) {
                                        methods.add(methodName);
                                    }
                                }
                            }
                            methodsCache.put(objClass, methods);
                        }
                    } finally {
                        methodsLock.unlock();
                    }

                    for (String methodName : methods) {
                        Method method = objClass.getMethod(methodName);
                        Object o1 = method.invoke(o);
                        if (o1 != null) {
                            if (notFirst) {
                                appendable.append(',');
                            } else {
                                notFirst = true;
                            }
                            if (pretty) {
                                appendable.append("\n");
                                appendable.append(INDENTS[level + 1]);
                            }
                            appendable.append('"');
                            appendable.append(StringUtils.decapitalize(methodName.substring(3)));
                            appendable.append('"');
                            appendable.append(':');
                            recursiveSerialize(o1, appendable, level + 1, pretty);
                        }
                    }
                    if (pretty) {
                        appendable.append("\n");
                        appendable.append(INDENTS[level]);
                    }
                    appendable.append('}');
                } else {
                    appendable.append('"');
                    appendable.append(o.toString());
                    appendable.append('"');
                }
            }
        } catch (IOException e) {
            throw new JsonException(e);
        } catch (NoSuchMethodException e) {
            throw new JsonException(e);
        } catch (IllegalAccessException e) {
            throw new JsonException(e);
        } catch (InvocationTargetException e) {
            throw new JsonException(e);
        }
    }

    public static void jsonToXml(
            Reader reader, Writer writer, String rootName
    ) throws JsonException {
        try {
            JsonStreamReader jsonReader = new JsonStreamReader(reader);
            StaxerXmlStreamWriter xmlWriter = new StaxerXmlStreamWriter(writer);

            xmlWriter.startDocument();
            xmlWriter.startElement(new XmlName(rootName));

            int event;
            while ((event = jsonReader.next()) != JsonStreamReader.EVENT_END) {
                switch (event) {
                    case JsonStreamReader.EVENT_OBJECT_START:
                        break;
                    case JsonStreamReader.EVENT_OBJECT_FIELD_NAME:
                        String name = jsonReader.getStringValue();
                        xmlWriter.startElement(new XmlName(name));
                        break;
                    case JsonStreamReader.EVENT_OBJECT_FIELD_VALUE:
                        break;
                    case JsonStreamReader.EVENT_OBJECT_FIELD_NEXT:
                        xmlWriter.endElement();
                        break;
                    case JsonStreamReader.EVENT_OBJECT_END:
                        xmlWriter.endElement();
                        break;
                    case JsonStreamReader.EVENT_LIST_START:
                        xmlWriter.startElement(XML_NAME_ITEM);
                        break;
                    case JsonStreamReader.EVENT_LIST_ITEM_NEXT:
                        xmlWriter.endElement();
                        xmlWriter.startElement(XML_NAME_ITEM);
                        break;
                    case JsonStreamReader.EVENT_LIST_END:
                        xmlWriter.endElement();
                        break;
                    case JsonStreamReader.EVENT_VALUE:
                        xmlWriter.text(jsonReader.getStringValue());
                        break;
                }
            }

            // rootName
            xmlWriter.endElement();
            xmlWriter.endDocument();
        } catch (IOException e) {
            throw new JsonException(e);
        } catch (StaxerXmlStreamException e) {
            throw new JsonException(e);
        }
    }

    public static JsonObjectMap asMap(JsonObject jsonObject) throws JsonException {
        if (jsonObject != null && jsonObject.isMap()) {
            return (JsonObjectMap) jsonObject;
        } else {
            return null;
        }
    }

    public static JsonObjectList asList(JsonObject jsonObject) throws JsonException {
        if (jsonObject != null && jsonObject.isList()) {
            return (JsonObjectList) jsonObject;
        } else {
            return null;
        }
    }

    public static JsonObject deserializeJson(String s) throws JsonException {
        StringReader reader = new StringReader(s);
        JsonStreamReader jsonReader = new JsonStreamReader(reader);
        return deserializeJson(jsonReader);
    }

    public static JsonObject deserializeJson(JsonStreamReader jsonReader) throws JsonException {
        try {
            JsonObject[] objectsStack = new JsonObject[100];

            int level = 0;
            String fieldName = null;

            int event;
            while ((event = jsonReader.next()) != JsonStreamReader.EVENT_END) {
                switch (event) {
                    case JsonStreamReader.EVENT_OBJECT_START:
                        JsonObjectMap currJsonObject = new JsonObjectMap();
                        objectsStack[level] = currJsonObject;
                        if (level > 0) {
                            JsonObject upperJsonObject = objectsStack[level - 1];
                            if (upperJsonObject.isList()) {
                                ((JsonObjectList) upperJsonObject).add(currJsonObject);
                            } else if (upperJsonObject.isMap() && !StringUtils.isEmpty(fieldName)) {
                                ((JsonObjectMap) upperJsonObject).put(fieldName, currJsonObject);
                                fieldName = null;
                            }
                        }
                        level += 1;
                        break;
                    case JsonStreamReader.EVENT_OBJECT_FIELD_NAME:
                        fieldName = jsonReader.getStringValue();
                        break;
                    case JsonStreamReader.EVENT_OBJECT_FIELD_VALUE:
                        break;
                    case JsonStreamReader.EVENT_OBJECT_FIELD_NEXT:
                        fieldName = null;
                        break;
                    case JsonStreamReader.EVENT_OBJECT_END:
                        level -= 1;
                        break;
                    case JsonStreamReader.EVENT_LIST_START:
                        JsonObjectList currJsonList = new JsonObjectList();
                        objectsStack[level] = currJsonList;
                        if (level > 0) {
                            JsonObject upperJsonObject = objectsStack[level - 1];
                            if (upperJsonObject.isList()) {
                                ((JsonObjectList) upperJsonObject).add(currJsonList);
                            } else if (upperJsonObject.isMap() && !StringUtils.isEmpty(fieldName)) {
                                ((JsonObjectMap) upperJsonObject).put(fieldName, currJsonList);
                                fieldName = null;
                            }
                        }
                        level += 1;
                        break;
                    case JsonStreamReader.EVENT_LIST_ITEM_NEXT:
                        //                    level -= 1;
                        break;
                    case JsonStreamReader.EVENT_LIST_END:
                        level -= 1;
                        break;
                    case JsonStreamReader.EVENT_VALUE:
                        if (level == 0) {
                            objectsStack[level] = jsonReader.getJsonObjectValue();
                        } else {
                            JsonObject upperJsonObject = objectsStack[level - 1];
                            if (upperJsonObject.isMap() && !StringUtils.isEmpty(fieldName)) {
                                ((JsonObjectMap) upperJsonObject).put(fieldName, jsonReader.getJsonObjectValue());
                                fieldName = null;
                            } else if (upperJsonObject.isList()) {
                                ((JsonObjectList) upperJsonObject).add(jsonReader.getJsonObjectValue());
                            }
                        }
                        break;
                }
            }

            return objectsStack[0];
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static JsonObject getJsonObjectField(JsonObject jsonObject, String fieldName) {
        if (jsonObject != null && jsonObject.isMap()) {
            return ((JsonObjectMap) jsonObject).get(fieldName);
        } else {
            return null;
        }
    }

    public static JsonObjectMap getJsonObjectMapField(JsonObject jsonObject, String fieldName) {
        if (jsonObject != null && jsonObject.isMap()) {
            return (JsonObjectMap) ((JsonObjectMap) jsonObject).get(fieldName);
        } else {
            return null;
        }
    }

    public static JsonObjectList getJsonObjectListField(JsonObject jsonObject, String fieldName) {
        if (jsonObject != null && jsonObject.isMap()) {
            return (JsonObjectList) ((JsonObjectMap) jsonObject).get(fieldName);
        } else {
            return null;
        }
    }

    public static JsonObjectMap getJsonObjectMapItem(JsonObject jsonObject, int idx) {
        if (jsonObject != null && jsonObject.isList()) {
            return (JsonObjectMap) ((JsonObjectList) jsonObject).get(idx);
        } else {
            return null;
        }
    }

    public static JsonObjectList getJsonObjectListItem(JsonObject jsonObject, int idx) {
        if (jsonObject != null && jsonObject.isList()) {
            return (JsonObjectList) ((JsonObjectList) jsonObject).get(idx);
        } else {
            return null;
        }
    }

    public static String getString(JsonObject jsonObject, String fieldName) {
        return getString(getJsonObjectField(jsonObject, fieldName));
    }

    public static String getString(JsonObject jsonObject) {
        if (jsonObject != null && !jsonObject.isNull() && jsonObject.isSimple()) {
            JsonObjectSimple simple = (JsonObjectSimple) jsonObject;
            if (simple.isString()) {
                return ((JsonObjectString) simple).getValue();
            } else if (simple.isNumber()) {
                return StringUtils.toString(((JsonObjectNumber) simple).getValue());
            } else if (simple.isBoolean()) {
                return Boolean.toString(((JsonObjectBoolean) simple).getValue());
            }
        }
        return null;
    }

    public static boolean getBoolean(JsonObject jsonObject, String fieldName) {
        return jsonObject.isMap() && getBoolean(getJsonObjectField(jsonObject, fieldName));
    }

    public static boolean getBoolean(JsonObject jsonObject) {
        if (jsonObject != null && !jsonObject.isNull() && jsonObject.isSimple()) {
            JsonObjectSimple simple = (JsonObjectSimple) jsonObject;
            if (simple.isString()) {
                return Boolean.parseBoolean(((JsonObjectString) simple).getValue());
            } else if (simple.isNumber()) {
                return ((JsonObjectNumber) simple).getValue().intValue() != 0;
            } else if (simple.isBoolean()) {
                return ((JsonObjectBoolean) simple).getValue();
            }
        }
        return false;
    }

    public static Number getNumber(JsonObjectMap jsonObject, String fieldName) {
        return getNumber(getJsonObjectField(jsonObject, fieldName));
    }

    public static Number getNumber(JsonObject jsonObject) {
        if (jsonObject != null && !jsonObject.isNull() && jsonObject.isSimple()) {
            JsonObjectSimple simple = (JsonObjectSimple) jsonObject;
            if (simple.isString()) {
                try {
                    return new BigDecimal(((JsonObjectString) simple).getValue());
                } catch (Exception ignored) {
                }
            } else if (simple.isNumber()) {
                return ((JsonObjectNumber) simple).getValue();
            }
        }
        return null;
    }

    public static Integer getInteger(JsonObject jsonObject, String fieldName) {
        if (jsonObject != null && jsonObject.isMap()) {
            Number number = getNumber((JsonObjectMap) jsonObject, fieldName);
            if (number != null) {
                return number.intValue();
            }
        }
        return null;
    }

    public static Integer getInteger(JsonObject jsonObject) {
        Number number = getNumber(jsonObject);
        if (number != null) {
            return number.intValue();
        } else {
            return null;
        }
    }

    public static <T extends StaxerReadJson> T readJson(
            StaxerJsonStreamReader jsonReader, Class<T> cls
    ) throws StaxerJsonStreamException {
        try {
            T result = cls.newInstance();
            result.readJson(jsonReader);
            return result;
        } catch (InstantiationException e) {
            throw new StaxerJsonStreamException(e);
        } catch (IllegalAccessException e) {
            throw new StaxerJsonStreamException(e);
        }
    }

}
