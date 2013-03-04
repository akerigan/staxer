package org.staxer.util.staxer.domain;

import org.staxer.util.xml.XmlName;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-10-17 19:44 (Europe/Moscow)
 */
public class WebServiceOperation {

    private XmlName name;
    private String javaName;
    private XmlName inputName;
    private XmlName inputMessage;
    private XmlName outputName;
    private XmlName outputMessage;
    private String soapAction;
    private String inputSoapBody;
    private String outputSoapBody;

    public XmlName getName() {
        return name;
    }

    public void setName(XmlName name) {
        this.name = name;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public XmlName getInputName() {
        return inputName;
    }

    public void setInputName(XmlName inputName) {
        this.inputName = inputName;
    }

    public XmlName getInputMessage() {
        return inputMessage;
    }

    public void setInputMessage(XmlName inputMessage) {
        this.inputMessage = inputMessage;
    }

    public XmlName getOutputName() {
        return outputName;
    }

    public void setOutputName(XmlName outputName) {
        this.outputName = outputName;
    }

    public XmlName getOutputMessage() {
        return outputMessage;
    }

    public void setOutputMessage(XmlName outputMessage) {
        this.outputMessage = outputMessage;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public String getInputSoapBody() {
        return inputSoapBody;
    }

    public void setInputSoapBody(String inputSoapBody) {
        this.inputSoapBody = inputSoapBody;
    }

    public String getOutputSoapBody() {
        return outputSoapBody;
    }

    public void setOutputSoapBody(String outputSoapBody) {
        this.outputSoapBody = outputSoapBody;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<WebServiceOperation>\n");
        sb.append("<name>");
        sb.append(name);
        sb.append("</name>\n");
        sb.append("<inputName>");
        sb.append(inputName);
        sb.append("</inputName>\n");
        sb.append("<inputMessage>");
        sb.append(inputMessage);
        sb.append("</inputMessage>\n");
        sb.append("<outputName>");
        sb.append(outputName);
        sb.append("</outputName>\n");
        sb.append("<outputMessage>");
        sb.append(outputMessage);
        sb.append("</outputMessage>\n");
        sb.append("<soapAction>");
        sb.append(soapAction);
        sb.append("</soapAction>\n");
        sb.append("<inputSoapBody>");
        sb.append(inputSoapBody);
        sb.append("</inputSoapBody>\n");
        sb.append("<outputSoapBody>");
        sb.append(outputSoapBody);
        sb.append("</outputSoapBody>\n");
        sb.append("</WebServiceOperation>\n");

        return sb.toString();
    }
}
