package org.staxer.util.json;

import junit.framework.TestCase;
import org.staxer.util.json.bean.JsonBean1;
import org.staxer.util.json.bean.JsonBean2;
import org.staxer.util.json.bean.JsonBean3;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2013-03-10 11:29
 */
public class StaxerJsonTest extends TestCase {

    public static final String JSON_1 =
            "{\n" +
                    "\"str1\"" +
                    "}";
    public static final String JSON_2 =
            "";
    public static final String JSON_3 =
            "";

    public void testJsonWriteRead() throws Exception {
        JsonBean1 jsonBean1 = new JsonBean1();
        String s = "asdf \"ghjkl\"";
        jsonBean1.setStr1(s);
        jsonBean1.setNumber1(123);
        jsonBean1.setNumber2(234l);
        jsonBean1.setDate1(new Date());
        jsonBean1.setBytes1(s.getBytes("UTF-8"));
        JsonBean2 jsonBean2 = new JsonBean2();
        jsonBean2.setStr2("wqer \"uiop\"");
        jsonBean1.setJsonBean2(jsonBean2);
        List<JsonBean3> jsonBean3List = jsonBean1.getJsonBean3List();
        JsonBean3 jsonBean3 = new JsonBean3();
        jsonBean3.setStr3("qwe");
        jsonBean3List.add(jsonBean3);jsonBean3 = new JsonBean3();
        jsonBean3.setStr3("asd");
        jsonBean3List.add(jsonBean3);jsonBean3 = new JsonBean3();
        jsonBean3.setStr3("zxc");
        jsonBean3List.add(jsonBean3);
        String json1 = getJsonString(jsonBean1);
        StringReader sr = new StringReader(json1);
        StaxerJsonStreamReader jsonReader = new StaxerJsonStreamReader(sr);
        JsonBean1 readJsonBean1 = new JsonBean1();
        readJsonBean1.readJson(jsonReader);
        String json2 = getJsonString(readJsonBean1);
        assertEquals(json1, json2);
    }

    private String getJsonString(JsonBean1 jsonBean1) throws StaxerJsonStreamException {
        StringWriter jsonStringWriter = new StringWriter();
        StaxerJsonStreamWriter jsonStreamWriter = new StaxerJsonStreamWriter(jsonStringWriter, 2);
        jsonBean1.writeJson(jsonStreamWriter);
        return jsonStringWriter.toString();
    }

    public void testJsonRead() throws StaxerJsonStreamException {
/*
        StringReader sr = new StringReader(JSON_1);
        StaxerJsonStreamReader jsonReader = new StaxerJsonStreamReader(sr);
        int level = jsonReader.getLevel();
        while (jsonReader.)
        jsonReader.readNext();
*/
    }

}
