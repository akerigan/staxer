package staxer.sample.server.service;

import comtech.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-18 11:45 (Europe/Moscow)
 */
public class DbService {

    private static Logger logger = LoggerFactory.getLogger(DbService.class);

    public String getUserPassword(String login) {
        if ("user".equals(login)) {
            try {
                return SecurityUtils.getMD5_EncodedBase64("user");
            } catch (NoSuchAlgorithmException e) {
                logger.error("", e);
            }
        }
        return null;
    }

}
