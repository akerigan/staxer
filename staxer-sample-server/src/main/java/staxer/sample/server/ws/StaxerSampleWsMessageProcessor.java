package staxer.sample.server.ws;

import comtech.staxer.server.WsMessage;
import comtech.staxer.server.WsMessageProcessor;
import comtech.util.NumberUtils;
import comtech.util.http.helper.HttpHelper;
import comtech.util.xml.XmlName;
import comtech.util.xml.soap.SoapFault;
import comtech.util.xml.soap.SoapFaultDetail;
import comtech.util.xml.soap.SoapFaultDetailEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import staxer.sample.server.service.DbService;

import java.util.List;
import java.util.Map;

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
            result.setString(e.getMessage());
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
