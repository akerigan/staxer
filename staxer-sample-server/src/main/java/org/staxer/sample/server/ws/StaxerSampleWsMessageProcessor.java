package org.staxer.sample.server.ws;

import org.staxer.util.staxer.server.WsMessage;
import org.staxer.util.staxer.server.WsMessageProcessor;
import org.staxer.util.NumberUtils;
import org.staxer.util.http.helper.HttpHelper;
import org.staxer.util.xml.XmlName;
import org.staxer.util.xml.soap.SoapFault;
import org.staxer.util.xml.soap.SoapFaultDetail;
import org.staxer.util.xml.soap.SoapFaultDetailEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.staxer.sample.server.service.DbService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-18 11:48 (Europe/Moscow)
 */
public class StaxerSampleWsMessageProcessor extends WsMessageProcessor {

    private static Logger logger = LoggerFactory.getLogger(StaxerSampleWsMessageProcessor.class);

    private DbService dbService;

    public void setDbService(DbService dbService) {
        this.dbService = dbService;
    }

    @Override
    public Logger getLog() {
        return logger;
    }

    @Override
    protected String getUserPassword(String userName) {
        return dbService.getUserPassword(userName);
    }

    @Override
    public void preProcess(WsMessage wsMessage, HttpHelper httpHelper, XmlName operationXmlName) {
    }

    @Override
    public void postProcess(WsMessage wsMessage, XmlName operationXmlName) {
    }

    @Override
    public SoapFault faultProcess(Exception e, WsMessage wsMessage, XmlName operationXmlName) {
        SoapFault result = new SoapFault();

        Map params = wsMessage.getParams();
        Integer wsMessageId = (Integer) params.get(PARAM_WS_REQUEST_ID);
        String userLogin = (String) params.get(PARAM_USER_LOGIN);
        result.setCode("env:Server");
        if (e != null) {
            String message = e.getMessage();
            if (message == null) {
                Set<Throwable> exceptions = new HashSet<Throwable>();
                exceptions.add(e);
                Throwable cause = e.getCause();
                while (cause != null && message == null) {
                    message = cause.getMessage();
                    cause = cause.getCause();
                    if (exceptions.contains(cause)) {
                        cause = null;
                    } else {
                        exceptions.add(cause);
                    }
                }
            }
            result.setString(message);
        } else {
            result.setString("Internal error occured");
        }
        SoapFaultDetail detail = new SoapFaultDetail();
        result.setDetail(detail);
        List entries = detail.getEntries();
        entries.add(new SoapFaultDetailEntry("wsMessageId", NumberUtils.toString(wsMessageId)));
        entries.add(new SoapFaultDetailEntry("userLogin", userLogin));
        if (operationXmlName != null) {
            entries.add(new SoapFaultDetailEntry("operationName", operationXmlName.toString()));
        }
        return result;
    }
}
