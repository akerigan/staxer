package comtech.staxer.plugin;

import comtech.staxer.StaxerUtils;
import comtech.staxer.domain.WebService;
import comtech.util.xml.XmlConstants;
import comtech.util.xml.XmlUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

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
    private boolean createClientService;

    /**
     * Generate server operations
     *
     * @parameter
     */
    private boolean createServerService;

    public void execute() throws MojoExecutionException {
        try {
            InputStream inputStream = URI.create(wsdlUrl).toURL().openStream();
            WebService webService = XmlUtils.readXml(inputStream, "UTF-8", WebService.class, XmlConstants.XML_NAME_WSDL_DEFINITIONS);
            inputStream.close();
            if (webService != null) {
                StaxerUtils.createJavaWebService(
                        webService, new File(sourceDir), packageName, true, createServerService, createClientService
                );
            } else {
                throw new MojoExecutionException("Web service is empty");
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Cant generate java ws beans", e);
        }
    }
}
