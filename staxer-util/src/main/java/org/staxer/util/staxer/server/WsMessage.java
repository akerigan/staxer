package org.staxer.util.staxer.server;

import java.util.Map;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 21.10.2009
 * Time: 18:09:59
 */
public class WsMessage<T> {

    private Map<Integer, Object> params;
    private T body;

    public Map<Integer, Object> getParams() {
        return params;
    }

    public void setParams(Map<Integer, Object> params) {
        this.params = params;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

}
