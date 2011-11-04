package comtech.staxer.plugin;

import comtech.staxer.generator.StubGenerator;
import comtech.util.file.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;

/**
 * Ws-client stub generator goal
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @goal import-wsdl
 * @phase generate-sources
 */
public class ImportWsdlMojo extends AbstractMojo {

    /**
     * Location base dir
     *
     * @parameter expression="${basedir}"
     * @required
     */
    private File baseDir;

    /**
     * URL of wsdl file
     *
     * @parameter
     * @required
     */
    private String wsdlUrl;

    /**
     * Http basic auth login
     *
     * @parameter
     */
    private String httpUser;

    /**
     * Http basic auth login
     *
     * @parameter
     */
    private String httpPassword;

    /**
     * Path and name of generated wsdl file
     *
     * @parameter
     * @required
     */
    private String generatedWsdl;

    public void execute() throws MojoExecutionException {
        try {
            File definitionFile = FileUtils.createTempFile(null, "definition", "xml");
            StubGenerator.importWsdl(wsdlUrl, httpUser, httpPassword, definitionFile);
            File wsdlFile = new File(baseDir, "/src/main/resources/" + generatedWsdl);
            FileUtils.mkdirs(wsdlFile.getParentFile(), null);
            Writer wsdlWriter = new FileWriter(wsdlFile);
            Reader definitionReader = new FileReader(definitionFile);
            StubGenerator.serializeAsWsdl(
                    definitionReader, wsdlWriter,
                    "http://localhost:8080/webapp/servlet"
            );
            definitionReader.close();
            wsdlWriter.close();
        } catch (Exception e) {
            throw new MojoExecutionException("Cant generate stub", e);
        }
    }
}
