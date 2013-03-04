package org.staxer.sample.server.ignite;

import org.staxer.util.staxer.server.WsMessageProcessor;
import org.staxer.util.staxer.server.WsMessageProcessorsContainer;
import org.staxer.sample.server.service.DbService;
import org.staxer.sample.server.ws.ServerStaxerSampleServiceWsImpl;
import org.staxer.sample.server.ws.StaxerSampleWsMessageProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-18 12:16 (Europe/Moscow)
 */
public class StaxerSampleServerServices implements WsMessageProcessorsContainer {

    private Map<String, WsMessageProcessor> wsMessageProcessorMap = new HashMap<String, WsMessageProcessor>();
    private DbService dbService;

    public StaxerSampleServerServices() {

        dbService = new DbService();

        AtomicInteger wsRequestIdGenerator = new AtomicInteger();
        StaxerSampleWsMessageProcessor wsMessageProcessor = new StaxerSampleWsMessageProcessor();
        wsMessageProcessor.setDbService(dbService);
        wsMessageProcessor.setServiceWs(new ServerStaxerSampleServiceWsImpl());
        wsMessageProcessor.setServletPath("sample");
        wsMessageProcessor.setWsdlPath("/staxerSample.wsdl");
        wsMessageProcessor.setWsRequestIdGenerator(wsRequestIdGenerator);
        wsMessageProcessorMap.put(wsMessageProcessor.getServletPath(), wsMessageProcessor);
    }

    public DbService getDbService() {
        return dbService;
    }

    public WsMessageProcessor getWsMessageProcessor(String servletPath) {
        return wsMessageProcessorMap.get(servletPath);
    }

    public void shutDown() {
    }

}
