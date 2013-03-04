package comtech.util.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 27.04.2010
 * Time: 12:09:33
 */
public class SuffixFilenameFilter implements FilenameFilter {

    private String[] patterns;

    public SuffixFilenameFilter(String... patterns) {
        this.patterns = patterns;
        for (int i = 0; i < patterns.length; i++) {
            patterns[i] = patterns[i].toUpperCase();
        }
    }

    public boolean accept(File dir, String name) {
        name = name.toUpperCase();
        for (String pattern : patterns) {
            if (name.endsWith(pattern)) {
                return true;
            }
        }
        return false;
    }
}
