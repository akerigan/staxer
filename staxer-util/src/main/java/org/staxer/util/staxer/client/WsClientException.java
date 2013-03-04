package org.staxer.util.staxer.client;

import org.staxer.util.xml.soap.SoapFault;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 14.09.2009
 * Time: 17:56:52
 */
public class WsClientException extends Exception {

    SoapFault soapFault;

    public WsClientException(String message) {
        super(message);
    }

    public WsClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public WsClientException(Throwable cause) {
        super(cause);
    }

    public WsClientException(SoapFault soapFault) {
        this.soapFault = soapFault;
    }

    public SoapFault getSoapFault() {
        return soapFault;
    }

    @Override
    public String getMessage() {
        if (soapFault != null) {
            return soapFault.toString();
        } else {
            return super.getMessage();
        }
    }
}
