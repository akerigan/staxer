package comtech.staxer.generator;

import java.io.File;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 15.09.2009
 * Time: 18:07:33
 */
public class ClientStubGenerator {

    public static void main(String[] args) throws Exception {
        generateStub(
                new File("."), "comtech.ws.swc.aviaSearch",
                "ws-common/src/main/resources/aviaSearch.wsdl"
        );
    }

    private static void generateStub(File curDirFile, String packageName, String wsdlPathname) throws Exception {
        File descriptionFile = File.createTempFile("description", ".xml");
        File sourceDir = new File(curDirFile, "ws-common/src/main/java");
        StubGenerator.importWsdl2(curDirFile.getAbsolutePath() + "/" + wsdlPathname, descriptionFile);
        StubGenerator.generateStub(descriptionFile, sourceDir, packageName, true, true);
    }

}
