package staxer.utils;

import comtech.util.staxer.StaxerUtils;
import comtech.util.staxer.domain.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-11-09 14:02 (Europe/Moscow)
 */
public class ReadGrntWebService {

    private static Logger log = LoggerFactory.getLogger(ReadGrntWebService.class);

    public static void main(
            String[] args
    ) throws Exception {
        File baseDirectory = new File(".");
        URI uri = new URI("http://83.102.140.138:8090/GnrtBT6NetWcfServiceLibrary.GnrtBT6NetWcfService/?wsdl");
        WebService webService = StaxerUtils.readWebService(uri, null, null, "UTF-8");
        if (webService != null) {
            File sourceDir = new File(baseDirectory, "staxer-sample-common/src/main/java");
            StaxerUtils.createJavaWebService(
                    webService, sourceDir, "comtech.ws.swc.grnt", true, true, true
            );
        }
    }

}
