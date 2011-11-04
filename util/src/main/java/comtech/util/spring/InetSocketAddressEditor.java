package comtech.util.spring;

import java.beans.PropertyEditorSupport;
import java.net.InetSocketAddress;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 25.06.2009
 * Time: 15:13:46
 */
public class InetSocketAddressEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        InetSocketAddress adr = parseAddress(text);
        setValue(adr);
    }

    private static InetSocketAddress parseAddress(String addressStr) {
        if (!addressStr.matches("(.+?):(\\d+)")) {
            throw new IllegalArgumentException("Incorrect server address");
        }

        String[] tokens = addressStr.split("\\:");

        String host = tokens[0];
        int port = Integer.parseInt(tokens[1]);

        return new InetSocketAddress(host, port);
    }

}
