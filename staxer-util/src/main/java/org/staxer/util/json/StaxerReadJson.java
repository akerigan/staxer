package org.staxer.util.json;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-12-30 13:21 (Europe/Moscow)
 */
public interface StaxerReadJson {

    public void readJson(StaxerJsonStreamReader jsonReader) throws StaxerJsonStreamException;

}
