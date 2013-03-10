package org.staxer.util.json.bean;

import org.apache.commons.codec.binary.Base64;
import org.staxer.util.StringUtils;
import org.staxer.util.date.DateTimeUtils;
import org.staxer.util.json.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2013-03-10 11:38
 */
public class JsonBean1 implements StaxerReadJson, StaxerWriteJson {

    private String str1;
    private Number number1;
    private Number number2;
    private Date date1;
    private byte[] bytes1;
    private boolean boolean1;
    private JsonBean2 jsonBean2;
    private List<JsonBean3> jsonBean3List;

    public String getStr1() {
        return str1;
    }

    public void setStr1(String str1) {
        this.str1 = str1;
    }

    public Number getNumber1() {
        return number1;
    }

    public void setNumber1(Number number1) {
        this.number1 = number1;
    }

    public Number getNumber2() {
        return number2;
    }

    public void setNumber2(Number number2) {
        this.number2 = number2;
    }

    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public byte[] getBytes1() {
        return bytes1;
    }

    public void setBytes1(byte[] bytes1) {
        this.bytes1 = bytes1;
    }

    public boolean isBoolean1() {
        return boolean1;
    }

    public void setBoolean1(boolean boolean1) {
        this.boolean1 = boolean1;
    }

    public JsonBean2 getJsonBean2() {
        return jsonBean2;
    }

    public void setJsonBean2(JsonBean2 jsonBean2) {
        this.jsonBean2 = jsonBean2;
    }

    public List<JsonBean3> getJsonBean3List() {
        if (jsonBean3List == null) {
            jsonBean3List = new ArrayList<JsonBean3>();
        }
        return jsonBean3List;
    }

    public void setJsonBean3List(List<JsonBean3> jsonBean3List) {
        this.jsonBean3List = jsonBean3List;
    }

    public void readJson(StaxerJsonStreamReader jsonReader) throws StaxerJsonStreamException {
        if (jsonReader.readObjectStart()) {
            int rootLevel = jsonReader.getLevel() + 1;
            while (jsonReader.readNext() && !jsonReader.objectEnded(rootLevel)) {
                String fieldName = jsonReader.readFieldName(rootLevel);
                if ("str1".equals(fieldName)) {
                    if (jsonReader.readFieldValue(rootLevel)) {
                        str1 = jsonReader.getStringValue();
                    }
                } else if ("number1".equals(fieldName)) {
                    if (jsonReader.readFieldValue(rootLevel)) {
                        number1 = jsonReader.getNumberValue();
                    }
                } else if ("number2".equals(fieldName)) {
                    if (jsonReader.readFieldValue(rootLevel)) {
                        number2 = jsonReader.getNumberValue();
                    }
                } else if ("date1".equals(fieldName)) {
                    if (jsonReader.readFieldValue(rootLevel)) {
                        date1 = DateTimeUtils.parseXmlDate(jsonReader.getStringValue());
                    }
                } else if ("bytes1".equals(fieldName)) {
                    if (jsonReader.readFieldValue(rootLevel)) {
                        bytes1 = Base64.decodeBase64(jsonReader.getStringValue());
                    }
                } else if ("bytes1".equals(fieldName)) {
                    if (jsonReader.readFieldValue(rootLevel)) {
                        boolean1 = StringUtils.parseBooleanInstance(jsonReader.getStringValue());
                    }
                } else if ("jsonBean2".equals(fieldName)) {
                    if (jsonReader.readObjectFieldValue(rootLevel)) {
                        jsonBean2 = JsonUtils.readJson(jsonReader, JsonBean2.class);
                    }
                }
            }
        }
    }

    public void writeJson(StaxerJsonStreamWriter jsonWriter) throws StaxerJsonStreamException {
        jsonWriter.startObject();
        jsonWriter.objectEntry("str1", str1);
        jsonWriter.objectEntry("number1", number1);
        jsonWriter.objectEntry("number2", number2);
        jsonWriter.objectEntry("date1", DateTimeUtils.formatXmlDate(date1, DateTimeUtils.TIME_ZONE_UTC));
        jsonWriter.objectEntry("bytes1", Base64.encodeBase64String(bytes1));
        jsonWriter.objectEntry("boolean1", boolean1);
        if (jsonBean2 != null) {
            jsonWriter.objectEntry("jsonBean2", jsonBean2);
        }
        if (jsonBean3List != null) {
            jsonWriter.objectEntryStartList("jsonBean3");
            for (JsonBean3 jsonBean3 : jsonBean3List) {
                jsonWriter.listItem(jsonBean3);
            }
            jsonWriter.objectEntryEndList();
        }
        jsonWriter.endObject();
    }

}
