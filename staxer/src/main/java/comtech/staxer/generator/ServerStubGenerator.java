package comtech.staxer.generator;

import comtech.util.file.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 15.09.2009
 * Time: 18:07:33
 */
public class ServerStubGenerator {

    private static Log log = LogFactory.getLog(ServerStubGenerator.class);

    public static void main(String[] args) throws Exception {
        File baseDir = new File("/home/akerigan/work/projects/comtech/trunk/ws-common");
        String wsdlUrl = "http://localhost:8080/swc-test/site?wsdl";

        File definitionFile = FileUtils.createTempFile(baseDir, "description", "xml");
        StubGenerator.importWsdl(wsdlUrl, definitionFile);
        StubGenerator.generateStub(
                definitionFile, new File(baseDir, "/src/main/java"),
                "comtech.ws.swc.site", true, true
        );
    }

}
