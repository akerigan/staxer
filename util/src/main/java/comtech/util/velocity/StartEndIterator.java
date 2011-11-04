package comtech.util.velocity;

import java.util.Iterator;

/**
 * DateTime: 2010-08-05-15-40 (Europe/Moscow)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class StartEndIterator implements Iterator<Integer> {

    private int end;
    private int current;

    public StartEndIterator(int start, int end) {
        current = start;
        this.end = end;
    }

    public boolean hasNext() {
        return current < end;
    }

    public Integer next() {
        return current++;
    }

    public void remove() {
    }
}
