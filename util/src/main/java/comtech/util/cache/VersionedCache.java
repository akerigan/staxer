package comtech.util.cache;

import comtech.util.StringUtils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-05-11 10:32 (Europe/Moscow)
 */
public abstract class VersionedCache<T> {

    public static final int DEFAULT_UPDATE_INTERVAL_MINUTES = 10;

    private int updateIntervalMinutes = DEFAULT_UPDATE_INTERVAL_MINUTES;
    private long validTo;
    private Lock lock = new ReentrantLock();

    private String version;
    protected T value;

    public int getUpdateIntervalMinutes() {
        return updateIntervalMinutes;
    }

    public void setUpdateIntervalMinutes(int updateIntervalMinutes) {
        this.updateIntervalMinutes = updateIntervalMinutes;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        if (!StringUtils.isEmpty(version)) {
            this.version = version;
        }
    }

    public T getValue() {
        lock.lock();
        try {
            if (System.currentTimeMillis() > validTo) {
                updateValue();
                validTo = System.currentTimeMillis() + updateIntervalMinutes * 60 * 1000;
            }
            return value;
        } finally {
            lock.unlock();
        }
    }

    public void update() {
        lock.lock();
        try {
            updateValue();
        } finally {
            lock.unlock();
        }
    }

    protected abstract void updateValue();

}
