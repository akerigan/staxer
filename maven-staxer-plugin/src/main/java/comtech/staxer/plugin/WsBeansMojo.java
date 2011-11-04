package comtech.staxer.plugin;

import comtech.staxer.generator.StubGenerator;
import comtech.util.file.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileReader;

/**
 * Ws-client stub generator goal
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @goal ws-beans
 * @phase generate-sources
 */
public class WsBeansMojo extends AbstractMojo {

    /**
     * Location base dir
     *
     * @parameter expression="${basedir}"
     * @required
     */
    private File baseDir;

    /**
     * URL of wsdl file or relative (based on module path) path to wsdl file
     *
     * @parameter
     * @required
     */
    private String wsdlUrl;

    /**
     * Relative path for stub saving
     *
     * @parameter
     * @required
     */
    private String sourceDir;

    /**
     * Relative path for description saving
     *
     * @parameter
     */
    private String definitionPath;

    /**
     * Name of generated package
     *
     * @parameter
     * @required
     */
    private String packageName;

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
     * Generate ws client service
     *
     * @parameter
     */
    private boolean clientService;

    /**
     * Generate server operations
     *
     * @parameter
     */
    private boolean serverService;

    public void execute() throws MojoExecutionException {
        try {
            File definitionFile;
            if (definitionPath != null) {
                definitionFile = new File(baseDir, definitionPath);
                File parentFile = definitionFile.getParentFile();
                if (!parentFile.exists()) {
                    if (!parentFile.mkdirs()) {
                        throw new MojoExecutionException("Cant create dir: " + parentFile.toString());
                    }
                }
            } else {
                definitionFile = FileUtils.createTempFile(baseDir, "description", "xml");
            }
            if (wsdlUrl.startsWith("http")) {
                StubGenerator.importWsdl(wsdlUrl, httpUser, httpPassword, definitionFile);
            } else {
                File wsdlFile = new File(baseDir, wsdlUrl);
                FileReader wsdlReader = new FileReader(wsdlFile);
                StubGenerator.importWsdl(wsdlReader, definitionFile);
                wsdlReader.close();
            }
            StubGenerator.generateStub(
                    definitionFile, new File(baseDir, sourceDir),
                    packageName, clientService, serverService
            );
        } catch (Exception e) {
            throw new MojoExecutionException("Cant generate stub", e);
        }
    }
}
