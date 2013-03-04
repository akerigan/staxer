package org.staxer.util.cache;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 01.09.2008
 * Time: 17:27:38
 */
public class CacheItem {

    private String groupName;
    private Object object;
    private long expires;

    public CacheItem(
            String groupName,
            Object object,
            long expires
    ) {
        this.groupName = groupName;
        this.object = object;
        this.expires = expires;
    }

    public CacheItem(
            Object object,
            long expires
    ) {
        this.expires = expires;
        this.object = object;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
