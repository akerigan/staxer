package comtech.util.cache;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 01.09.2008
 * Time: 17:27:38
 */
public class CacheItem {

    private Object object;
    private long expires;

    public CacheItem(Object object, long expires) {
        this.expires = expires;
        this.object = object;
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
