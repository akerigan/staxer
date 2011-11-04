package comtech.util.filter;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 19.05.2009
 * Time: 17:22:05
 */
public class ExactStringSetFilter implements StringSetFilter {

    Set<String> matches = new TreeSet<String>();

    public ExactStringSetFilter(String[] stringsArray) {
        matches.addAll(Arrays.asList(stringsArray));
    }

    public Set<String> filter(Set<String> stringsSet) {
        Set<String> result = new TreeSet<String>(matches);
        result.retainAll(stringsSet);
        return result;
    }

}
