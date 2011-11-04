package comtech.util.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2010-11-11 12:13:56 (Europe/Moscow)
 */
public class SingleCache {

    public static final long STORE_TIME_10_SECONDS = 10 * 1000;
    public static final long STORE_TIME_1_MINUTE = 60 * 1000;
    public static final long STORE_TIME_10_MINUTES = 10 * 60 * 1000;
    public static final long STORE_TIME_1_HOUR = 60 * 60 * 1000;

    private static final Logger LOG = LoggerFactory.getLogger(SingleCache.class);

    private HashMap<Object, CacheItem> cacheItemsMap = new HashMap<Object, CacheItem>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private ValidatingThread validatingThread;

    private String name;

    public SingleCache(String name) {
        this.name = name;
        validatingThread = new ValidatingThread(this);
        LOG.info(name + ": single cache validating thread created...");
        validatingThread.start();
        LOG.info(name + ": single cache validating thread started...");
    }

    public void setValidateInterval(long validateInterval) {
        validatingThread.setValidateInterval(validateInterval);
    }

    public <T> T get(Object key) {
        CacheItem cacheItem = null;
        lock.readLock().lock();
        try {
            cacheItem = cacheItemsMap.get(key);
        } finally {
            lock.readLock().unlock();
        }
        if (cacheItem != null) {
            if (System.currentTimeMillis() <= cacheItem.getExpires()) {
                return (T) cacheItem.getObject();
            }
            removeEntry(key);
        }
        return null;
    }

    public void put(Object key, Object obj, long validityTime) {
        if (obj != null) {
            lock.writeLock().lock();
            try {
                cacheItemsMap.put(key, new CacheItem(obj, System.currentTimeMillis() + validityTime));
            } finally {
                lock.writeLock().unlock();
            }
            LOG.info(key + " object stored in cache " + name + " for " + validityTime + " ms");
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            cacheItemsMap.clear();
        } finally {
            lock.writeLock().unlock();
        }
        LOG.info(name + ": cache cleared");
    }

    public void removeEntry(Object key) {
        boolean removed = false;
        lock.writeLock().lock();
        try {
            if (cacheItemsMap.containsKey(key)) {
                cacheItemsMap.remove(key);
                removed = true;
            }
        } finally {
            lock.writeLock().unlock();
        }
        if (removed) {
            LOG.info(name + ": cache item removed: " + key);
        }
    }

    public Collection<Object> getKeys() {
        Set<Object> result = new HashSet<Object>();
        lock.readLock().lock();
        try {
            result.addAll(cacheItemsMap.keySet());
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

}
