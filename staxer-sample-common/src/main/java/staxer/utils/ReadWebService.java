package staxer.utils;

import comtech.staxer.StaxerUtils;
import comtech.staxer.domain.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-09 14:02 (Europe/Moscow)
 */
public class ReadWebService {

    private static Logger log = LoggerFactory.getLogger(ReadWebService.class);

    public static void main(
            String[] args
    ) throws Exception {
        File baseDirectory = new File(".");
        log.info("baseDirectory = " + baseDirectory.getAbsolutePath());
        File wsdlFile = new File(baseDirectory, "staxer-sample-common/src/main/resources/staxerSample.wsdl");
        System.out.println("wsdlFile exists = " + wsdlFile.exists());
        WebService webService = StaxerUtils.readWebService(wsdlFile.toURI(), null, null, "UTF-8");
        if (webService != null) {
            File sourceDir = new File(baseDirectory, "staxer-sample-common/src/main/java");
            StaxerUtils.createJavaWebService(
                    webService, sourceDir, "staxer.sample", true, true, true
            );
        }
    }

}
