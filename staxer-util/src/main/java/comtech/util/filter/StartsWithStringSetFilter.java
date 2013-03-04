package ru.atc.esnsi.util.filter;

import java.util.Set;
import java.util.TreeSet;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 19.05.2009
 * Time: 17:26:30
 */
public class StartsWithStringSetFilter implements StringSetFilter {

    String[] patterns;

    public StartsWithStringSetFilter(String pattern) {
        patterns = new String[]{pattern};
    }

    public StartsWithStringSetFilter(String[] patterns) {
        this.patterns = patterns;
    }

    public Set<String> filter(Set<String> stringsSet) {
        TreeSet<String> source = new TreeSet<String>(stringsSet);
        Set<String> result = new TreeSet<String>();
        for (String pattern : patterns) {
            for (String s : source.tailSet(pattern)) {
                if (s.startsWith(pattern)) {
                    result.add(s);
                } else {
                    break;
                }
            }
        }
        return result;
    }
}
