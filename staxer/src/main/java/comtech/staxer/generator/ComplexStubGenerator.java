package comtech.staxer.generator;

import comtech.staxer.domain.type.WsDefinition;

import java.io.File;
import java.io.FileReader;

/**
 * User: Vlad Vinichenko (akerigan@gmail.com)
 * Date: 15.09.2009
 * Time: 18:07:33
 */
public class ComplexStubGenerator {

    public static void main(String[] args) throws Exception {

        File curDirFile = new File(".");

        // import ego wsdl
/*
        File descriptionFile = File.createTempFile("description", ".xml");
        File sourceDir = new File(curDirFile, "payment/src/main/java");
        String packageName = "comtech.payment.client.ego.stub";

        StubGenerator.importWsdl("https://212.176.5.151/ws/gdspay/gdspay.wsdl", "test", "test", descriptionFile);
        StubGenerator.generateClientStub(descriptionFile, sourceDir, packageName);
*/

        // import swc schedule wsdl
/*
        File descriptionFile = File.createTempFile("swc-schedule", ".xml");
        File sourceDir = new File(curDirFile, "ws-common/src/main/java");
        String packageName = "comtech.ws.swc.schedule";

        StubGenerator.importWsdl("http://localhost:8090/swc-test/schedule?wsdl", descriptionFile);
        StubGenerator.generateClientStub(descriptionFile, sourceDir, packageName);
*/

        File descriptionFile = File.createTempFile("cmplxdefntn", ".xml");
        File sourceDir = new File(curDirFile, "ws-common/src/main/java");
        String packageName = "comtech.ws.swc.air";

        FileReader inputStream = new FileReader(
//                "/home/user/work/projects/trunk/ws-common/src/main/resources/premiere.wsdl"
                "/home/user/Documents/WSDL Schema/air_v16_0/Air.wsdl"
        );
        WsDefinition wsDefinition = StubGenerator.importWsdl2("/home/user/Documents/WSDL Schema/air_v16_0/Air.wsdl", descriptionFile);
        inputStream.close();
//        StubGenerator.generateStub(descriptionFile, sourceDir, packageName, true, true);
        StubGenerator.generateStubByStringBuilder(wsDefinition, sourceDir, packageName, true, true);

    }

}
