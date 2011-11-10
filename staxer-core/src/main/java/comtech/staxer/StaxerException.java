package comtech.staxer;

import comtech.util.xml.soap.SoapFault;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 21.10.2009
 * Time: 14:01:24
 */
public class StaxerException extends Exception {

    private SoapFault soapFault;

    public StaxerException() {
    }

    public StaxerException(String message) {
        super(message);
    }

    public StaxerException(String message, Throwable cause) {
        super(message, cause);
    }

    public StaxerException(Throwable cause) {
        super(cause);
    }

    public StaxerException(SoapFault soapFault) {
        this.soapFault = soapFault;
    }

    public StaxerException(SoapFault soapFault, Throwable cause) {
        super(cause);
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
