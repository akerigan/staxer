package comtech.staxer.plugin;

import comtech.staxer.StaxerUtils;
import comtech.staxer.domain.WebService;
import comtech.util.ResourceUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.net.URI;

/**
 * Ws beans generator goal
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
     * Charset of wsdl file
     *
     * @parameter default-value="UTF-8"
     */
    private String wsdlCharset;

    /**
     * Relative path for stub saving
     *
     * @parameter
     * @required
     */
    private String sourceDir;

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
     * Http basic auth password
     *
     * @parameter
     */
    private String httpPassword;

    /**
     * Generate ws client service
     *
     * @parameter
     */
    private boolean createClientService;

    /**
     * Generate server operations
     *
     * @parameter
     */
    private boolean createServerService;

    public void execute() throws MojoExecutionException {
        try {
            URI uri;
            if (wsdlUrl.startsWith("http")) {
                uri = URI.create(wsdlUrl);
            } else {
                File wsdlFile = new File(baseDir, wsdlUrl);
                uri = wsdlFile.toURI();
            }
            String xml = ResourceUtils.getUrlContentAsString(uri, httpUser, httpPassword, wsdlCharset);
            if (xml == null) {
                throw new MojoExecutionException("Url content is empty");
            }
            WebService webService = StaxerUtils.readWebService(uri, httpUser, httpPassword, wsdlCharset);
            if (webService != null) {
                StaxerUtils.createJavaWebService(
                        webService, new File(baseDir, sourceDir), packageName,
                        true, createClientService, createServerService
                );
            } else {
                throw new MojoExecutionException("Web service is empty");
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Cant generate java ws beans", e);
        }
    }
}
