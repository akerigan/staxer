package org.staxer.util.json.bean;

import org.staxer.util.json.*;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2013-03-10 12:19
 */
public class JsonBean3 implements StaxerReadJson, StaxerWriteJson {

    private String str3;

    public String getStr3() {
        return str3;
    }

    public void setStr3(String str3) {
        this.str3 = str3;
    }

    public void readJson(StaxerJsonStreamReader jsonReader) throws StaxerJsonStreamException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void writeJson(StaxerJsonStreamWriter jsonWriter) throws StaxerJsonStreamException {
        jsonWriter.startObject();
        jsonWriter.objectEntry("str3", str3);
        jsonWriter.endObject();
    }

}
