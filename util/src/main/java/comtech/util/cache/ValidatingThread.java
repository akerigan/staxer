package comtech.util.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2010-11-11 10:37:02 (Europe/Moscow)
 */
public class ValidatingThread extends Thread {
    // 35 secs
    public static final int DEFAULT_VALIDATE_INTERVAL = 35 * 1000;
    private static final Log log = LogFactory.getLog(ValidatingThread.class);
    private long validateInterval = DEFAULT_VALIDATE_INTERVAL;
    private boolean continueWork = true;
    private SingleCache cache;

    public ValidatingThread(SingleCache cache) {
        super("SingleCacheValidateThread");
        this.cache = cache;
    }

    public long getValidateInterval() {
        return validateInterval;
    }

    public void setValidateInterval(long validateInterval) {
        this.validateInterval = validateInterval;
    }

    public void run() {
        while (continueWork) {
            Collection<Object> keys = cache.getKeys();
            log.info(getName() + ": loop iteration started, cache size is " + keys.size());
            int removed = 0;
            for (Object key : keys) {
                if (cache.get(key) == null) {
                    removed += 1;
                }
            }
            log.info(getName() + ": loop iteration finished, " + removed + " elements removed");
            try {
                if (continueWork) {
                    log.info(getName() + ": sleeping for " + validateInterval + " msec");
                    Thread.sleep(validateInterval);
                }
            } catch (Exception e) {
                log.warn("Thread was interrupted", e);
            }
        }
        log.warn(getName() + ": thread have stopped it's checking");
    }

    public void stopWork() {
        log.warn(getName() + ": thread have stopped it's checking");
        continueWork = false;
    }

    @Override
    public void start() {
        log.info(getName() + ": thread is starting...");
        continueWork = true;
        super.start();
    }

    @Override
    public void interrupt() {
        log.info("Thread is interrupting...");
        super.interrupt();
    }

}
