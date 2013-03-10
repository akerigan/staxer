package org.staxer.util.json.bean;

import org.staxer.util.json.*;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2013-03-10 12:19
 */
public class JsonBean2 implements StaxerReadJson, StaxerWriteJson {

    private String str2;

    public String getStr2() {
        return str2;
    }

    public void setStr2(String str2) {
        this.str2 = str2;
    }

    public void readJson(StaxerJsonStreamReader jsonReader) throws StaxerJsonStreamException {
        if (jsonReader.readObjectStart()) {
            int rootLevel = jsonReader.getLevel() + 1;
            while (jsonReader.readNext() && !jsonReader.objectEnded(rootLevel)) {
                String fieldName = jsonReader.readFieldName(rootLevel);
                if ("str2".equals(fieldName)) {
                    if (jsonReader.readFieldValue(rootLevel)) {
                        str2 = jsonReader.getStringValue();
                    }
                }
            }
        }
    }

    public void writeJson(StaxerJsonStreamWriter jsonWriter) throws StaxerJsonStreamException {
        jsonWriter.startObject();
        jsonWriter.objectEntry("str2", str2);
        jsonWriter.endObject();
    }

}
