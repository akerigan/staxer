package org.staxer.util.words;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com) (akerigan@gmail.com)
 * Date: 25.03.2008
 * Time: 21:50:40
 */
public class NumberInWords {

    /**
     * Энто допустимая степень числа 1000 для __int64:
     * При необходимости его легко увеличить,
     * дополнив массив 'powerNames' и заменив
     * тип __int64 на более серьезный
     */
    private static final int maxPower = 6;

    private static PowerName[] powerNames = {
            new PowerName(Sex.male, "", "", ""),                                         // 1
            new PowerName(Sex.female, "тысяча ", "тысячи ", "тысяч "),                   // 2
            new PowerName(Sex.male, "миллион ", "миллиона ", "миллионов "),              // 3
            new PowerName(Sex.male, "миллиард ", "миллиарда ", "миллиардов "),           // 4
            new PowerName(Sex.male, "триллион ", "триллиона ", "триллионов "),           // 5
            new PowerName(Sex.male, "квадриллион ", "квадриллиона ", "квадриллионов "),  // 6
            new PowerName(Sex.male, "квинтиллион ", "квинтиллиона ", "квинтиллионов ")   // 7
    };

    private static UnitName[] unitNames = {
            new UnitName(new String[]{"", ""}, "десять ", "", ""),
            new UnitName(new String[]{"один ", "одна "}, "одиннадцать ", "десять ", "сто "),
            new UnitName(new String[]{"два ", "две "}, "двенадцать ", "двадцать ", "двести "),
            new UnitName(new String[]{"три ", "три "}, "тринадцать ", "тридцать ", "триста "),
            new UnitName(new String[]{"четыре ", "четыре "}, "четырнадцать ", "сорок ", "четыреста "),
            new UnitName(new String[]{"пять ", "пять "}, "пятнадцать ", "пятьдесят ", "пятьсот "),
            new UnitName(new String[]{"шесть ", "шесть "}, "шестнадцать ", "шестьдесят ", "шестьсот "),
            new UnitName(new String[]{"семь ", "семь "}, "семнадцать ", "семьдесят ", "семьсот "),
            new UnitName(new String[]{"восемь ", "восемь "}, "восемнадцать ", "восемьдесят ", "восемьсот "),
            new UnitName(new String[]{"девять ", "девять "}, "девятнадцать ", "девяносто ", "девятьсот ")
    };

    public static String dig2str(long number, Sex sex, String one, String four, String many) {
        int i;
        long mny;
        String str, result = "";

        /**
         * делитель
         */
        long divisor;

        powerNames[0].sex = sex;
        powerNames[0].one = one;
        powerNames[0].four = four;
        powerNames[0].many = many;

        if (number == 0) return "ноль " + many;
        if (number < 0) {
            result = "минус ";
            number = -number;
        }

        for (i = 0, divisor = 1; i < maxPower; i++) {
            divisor *= 1000;
        }
        for (i = maxPower - 1; i >= 0; i--) {
            divisor /= 1000;
            mny = number / divisor;
            number %= divisor;
            str = "";
            if (mny == 0) {
                if (i > 0) {
                    continue;
                }
                str += powerNames[i].one;
            } else {
                if (mny >= 100) {
                    str += unitNames[(int) (mny / 100)].hun;
                    mny %= 100;
                }
                if (mny >= 20) {
                    str += unitNames[(int) (mny / 10)].dec;
                    mny %= 10;
                }
                if (mny >= 10) {
                    str += unitNames[(int) (mny % 10)].two;
                } else if (mny >= 1) {
                    str += unitNames[(int) mny].one[powerNames[i].sex.id];
                }
                if (mny == 1) {
                    str += powerNames[i].one;
                } else if (mny > 1 && mny < 5) {
                    str += powerNames[i].four;
                } else {
                    str += powerNames[i].many;
                }
            }
            result += str;
        }
        return result;
    }

    public static String dig2str2(long number, String one, String four, String many) {
        long tmpNumber = Math.abs(number);
        String result = (number < 0 ? "-" : "")
                + (tmpNumber < 10 ? "0" : "")
                + String.valueOf(tmpNumber) + " ";
        if (tmpNumber == 1) {
            return result + one;
        } else if (tmpNumber > 1 && tmpNumber < 5) {
            return result + four;
        } else {
            return result + many;
        }
    }
}
