package comtech.util.number.in.words;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com) (akerigan@gmail.com)
 *         Date: 25.03.2008
 *         Time: 22:31:57
 */
public class NumberingTest {

    private static final Log log = LogFactory.getLog(NumberingTest.class);

    public static void main(String[] args) {
//        long number = 10231545;
        long number = 13113113103L;
        log.debug(number + " : " + NumberInWords.dig2str(number, Sex.male, "рубль", "рубля", "рублей"));
    }
}
