package comtech.staxer.server;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-18 14:53 (Europe/Moscow)
 */
public interface WsMessageProcessorsContainer {

    public WsMessageProcessor getWsMessageProcessor(String sevletPath);

}
