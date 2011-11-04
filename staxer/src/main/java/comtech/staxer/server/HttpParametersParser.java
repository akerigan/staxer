package comtech.staxer.server;

import comtech.util.servlet.helper.HttpHelper;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-09-08 17:13 (Europe/Moscow)
 */
public interface HttpParametersParser {

    public void parseHttpParameters(HttpHelper httpHelper);

}
