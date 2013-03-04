package org.staxer.util.range;

/**
 * @author Vlad Vinichenko
 * @since 2012-08-29 16:18
 */
public class SimpleRange implements Comparable<SimpleRange> {

    private int start = -1;
    private int end = -1;

    public SimpleRange() {
    }

    public SimpleRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean add(int elementIndex) {
        if (start == -1) {
            end = start = elementIndex;
            return true;
        }
        if ((elementIndex - end) == 1) {
            end = elementIndex;
            return true;
        }
        return false;
    }

    public int compareTo(int i) {
        if (i < start) {
            return 1;
        } else if (end < i) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * -11 if start2 < start1 && end2 < end1
     * -10 if start2 < start1 && end2 = end1
     * -9 if start2 < start1 && end2 > end1
     * -1 if start2 = start1 && end2 < end1
     * 0 if start2 = start1 && end2 = end1
     * 1 if start2 = start1 && end2 > end1
     * 9 if start2 > start1 && end2 < end1
     * 10 if start2 > start1 && end2 = end1
     * 11 if start2 > start1 && end2 > end1
     *
     * @param otherRange other range
     * @return compare result
     */
    public int compareTo(SimpleRange otherRange) {
        return compareTo(otherRange.start) * 10 + compareTo(otherRange.end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SimpleRange that = (SimpleRange) o;

        if (end != that.end) {
            return false;
        }
        if (start != that.start) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + end;
        return result;
    }
}
