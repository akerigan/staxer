package org.staxer.util.xml;

import org.staxer.util.StringUtils;

/**
 * @author Vlad Vinichenko
 * @since 2012-11-28 10:03
 */
public class StaxerXpathValued extends StaxerXpath {

    protected static final int MODE_VALUE = 3;

    private String value;

    public StaxerXpathValued() {
    }

    public StaxerXpathValued(String valuedExpression) {
        super(valuedExpression);
        valuedExpression = StringUtils.notEmptyTrimmedElseNull(valuedExpression);
        if (valuedExpression != null) {
            StringBuilder sb = new StringBuilder();
            int mode = MODE_XPATH;
            for (int i = 0, length = valuedExpression.length(); i <= length; i += 1) {
                char ch = 0;
                if (i < length) {
                    ch = valuedExpression.charAt(i);
                }
                if (mode == MODE_VALUE) {
                    if (ch == ';' || ch == 0) {
                        if (sb.length() > 0) {
                            value = sb.toString();
                        }
                    } else {
                        sb.append(ch);
                    }
                }
                if (ch == ';') {
                    mode += 1;
                }
            }
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value != null) {
            return super.toString() + ";" + value;
        } else {
            return super.toString();
        }
    }
}
