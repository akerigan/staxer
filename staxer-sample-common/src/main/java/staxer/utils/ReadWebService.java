package staxer.utils;

import comtech.staxer.StaxerUtils;
import comtech.staxer.domain.WebService;
import comtech.util.xml.StaxerXmlStreamException;
import comtech.util.xml.XmlConstants;
import comtech.util.xml.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-09 14:02 (Europe/Moscow)
 */
public class ReadWebService {

    private static Logger log = LoggerFactory.getLogger(ReadWebService.class);

    public static void main(
            String[] args
    ) throws StaxerXmlStreamException, IOException {
        File baseDirectory = new File(".");
        log.info("baseDirectory = " + baseDirectory.getAbsolutePath());
        File wsdlFile = new File(baseDirectory, "staxer-sample-common/src/main/resources/staxerSample.wsdl");
        System.out.println("wsdlFile exists = " + wsdlFile.exists());
        WebService webService = XmlUtils.readXml(
                new FileInputStream(wsdlFile), "UTF-8",
                WebService.class,
                XmlConstants.XML_NAME_WSDL_DEFINITIONS
        );
        if (webService != null) {
            File sourceDir = new File(baseDirectory, "staxer-sample-common/src/main/java");
            StaxerUtils.createJavaWebService(
                    webService, sourceDir, "staxer.sample", true, true, true
            );
        }
    }

}
