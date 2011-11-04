package comtech.util.velocity;

import java.util.Collection;
import java.util.Iterator;

/**
 * DateTime: 2010-08-05-15-40 (Europe/Moscow)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class IteratorCollection implements Collection<Integer> {

    private int start;
    private int end;

    public IteratorCollection(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int size() {
        return start - end;
    }

    public boolean isEmpty() {
        return end > start;
    }

    public boolean contains(Object o) {
        return false;
    }

    public Iterator<Integer> iterator() {
        return new StartEndIterator(start, end);
    }

    public Object[] toArray() {
        return null;
    }

    public <T> T[] toArray(T[] a) {
        return null;
    }

    public boolean add(Integer integer) {
        return false;
    }

    public boolean remove(Object o) {
        return false;
    }

    public boolean containsAll(Collection<?> c) {
        return false;
    }

    public boolean addAll(Collection<? extends Integer> c) {
        return false;
    }

    public boolean removeAll(Collection<?> c) {
        return false;
    }

    public boolean retainAll(Collection<?> c) {
        return false;
    }

    public void clear() {
    }
}

