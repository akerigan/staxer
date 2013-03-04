package org.staxer.util.json;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-12-29 12:12 (Europe/Moscow)
 */
public interface StaxerWriteJson {

    public void writeJson(StaxerJsonStreamWriter jsonWriter) throws StaxerJsonStreamException;

}
