package ru.atc.esnsi.util.words;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com) (akerigan@gmail.com)
 * Date: 25.03.2008
 * Time: 22:31:57
 */
public class NumberingTest {

    private static final Logger log = LoggerFactory.getLogger(NumberingTest.class);

    public static void main(String[] args) {
//        long number = 10231545;
        long number = 13113113103L;
        log.debug(number + " : " + NumberInWords.dig2str(number, Sex.male, "рубль", "рубля", "рублей"));
    }
}
