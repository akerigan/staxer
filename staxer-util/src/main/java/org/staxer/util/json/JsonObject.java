package org.staxer.util.json;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-05-13 11:00 (Europe/Moscow)
 */
public interface JsonObject {

    public boolean isNull();

    public boolean isList();

    public boolean isMap();

    public boolean isSimple();


}
