package staxer.sample.server.ws;

import comtech.staxer.server.WsMessage;
import comtech.staxer.server.WsMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import staxer.sample.ServerStaxerSampleServiceWs;
import staxer.sample.bean.EchoCustomTypesRequest;
import staxer.sample.bean.EchoCustomTypesResponse;
import staxer.sample.bean.EchoXsdTypesRequest;
import staxer.sample.bean.EchoXsdTypesResponse;

import java.util.Map;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-18 15:12 (Europe/Moscow)
 */
public class ServerStaxerSampleServiceWsImpl extends ServerStaxerSampleServiceWs {

    private static Logger logger = LoggerFactory.getLogger(ServerStaxerSampleServiceWsImpl.class);

    @Override
    public EchoXsdTypesResponse echoXsdTypes(WsMessage<EchoXsdTypesRequest> wsMessage) {
        Map<Integer, Object> params = wsMessage.getParams();
        Integer wsMessageId = (Integer) params.get(WsMessageProcessor.PARAM_WS_REQUEST_ID);
        String userLogin = (String) params.get(WsMessageProcessor.PARAM_USER_LOGIN);
        log(wsMessageId, userLogin, "echoXsdTypes");

        EchoXsdTypesRequest request = wsMessage.getBody();
        EchoXsdTypesResponse response = new EchoXsdTypesResponse();
        response.setNillElemBase64Binary(request.getNillElemBase64Binary());
        response.setNillElemBoolean(request.getNillElemBoolean());
        response.setNillElemCharacter(request.getNillElemCharacter());
        response.setNillElemDateTime(request.getNillElemDateTime());
        response.setNillElemDecimal(request.getNillElemDecimal());
        response.setNillElemDouble(request.getNillElemDouble());
        response.setNillElemFloat(request.getNillElemFloat());
        response.setNillElemFloat(request.getNillElemFloat());
        response.setNillElemInteger(request.getNillElemInteger());
        response.setNillElemString(request.getNillElemString());
        response.setAttBase64Binary(request.getAttBase64Binary());
        response.setAttBoolean(request.getAttBoolean());
        response.setAttCharacter(request.getAttCharacter());
        response.setAttDateTime(request.getAttDateTime());
        response.setAttDecimal(request.getAttDecimal());
        response.setAttDouble(request.getAttDouble());
        response.setAttFloat(request.getAttFloat());
        response.setAttInteger(request.getAttInteger());
        response.setAttString(request.getAttString());
        response.setElemBase64Binary(request.getElemBase64Binary());
        response.setElemBoolean(request.getElemBoolean());
        response.setElemCharacter(request.getElemCharacter());
        response.setElemDateTime(request.getElemDateTime());
        response.setElemDecimal(request.getElemDecimal());
        response.setElemDouble(request.getElemDouble());
        response.setElemFloat(request.getElemFloat());
        response.setElemInteger(request.getElemInteger());
        response.setElemString(request.getElemString());
        response.getLstBase64Binary().addAll(request.getLstBase64Binary());
        response.getLstBoolean().addAll(request.getLstBoolean());
        response.getLstCharacter().addAll(request.getLstCharacter());
        response.getLstDateTime().addAll(request.getLstDateTime());
        response.getLstDecimal().addAll(request.getLstDecimal());
        response.getLstDouble().addAll(request.getLstDouble());
        response.getLstFloat().addAll(request.getLstFloat());
        response.getLstInteger().addAll(request.getLstInteger());
        response.getLstString().addAll(request.getLstString());
        return response;
    }

    private void log(Integer wsMessageId, String userLogin, String method) {
        logger.info(wsMessageId + ": " + userLogin + "/" + method + " - access granted");
    }

    @Override
    public EchoCustomTypesResponse echoCustomTypes(WsMessage<EchoCustomTypesRequest> wsMessage) {
        Map<Integer, Object> params = wsMessage.getParams();
        Integer wsMessageId = (Integer) params.get(WsMessageProcessor.PARAM_WS_REQUEST_ID);
        String userLogin = (String) params.get(WsMessageProcessor.PARAM_USER_LOGIN);
        log(wsMessageId, userLogin, "echoCustomTypes");

        EchoCustomTypesRequest request = wsMessage.getBody();
        EchoCustomTypesResponse response = new EchoCustomTypesResponse();
        response.setNillElemEnum(request.getNillElemEnum());
        response.setNillElemOval(request.getNillElemOval());
        response.setNillElemValue(request.getNillElemValue());
        response.setAttEnum(request.getAttEnum());
        response.setElemEnum(request.getNillElemEnum());
        response.setElemOval(request.getNillElemOval());
        response.setElemValue(request.getNillElemValue());
        response.getLstEnum().addAll(request.getLstEnum());
        response.getLstOval().addAll(request.getLstOval());
        response.getLstValue().addAll(request.getLstValue());
        return response;
    }

}
