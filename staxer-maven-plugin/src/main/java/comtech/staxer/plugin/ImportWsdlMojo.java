package comtech.staxer.plugin;

import comtech.staxer.domain.WebService;
import comtech.util.file.FileUtils;
import comtech.util.xml.XmlConstants;
import comtech.util.xml.XmlUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;

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
            InputStream inputStream = URI.create(wsdlUrl).toURL().openStream();
            WebService webService = XmlUtils.readXml(inputStream, "UTF-8", WebService.class, XmlConstants.XML_NAME_WSDL_DEFINITIONS);
            inputStream.close();
            if (webService != null) {
                File wsdlFile = new File(baseDir, "/src/main/resources/" + generatedWsdl);
                FileUtils.mkdirs(wsdlFile.getParentFile(), null);
                XmlUtils.writeXml(new FileOutputStream(wsdlFile), "UTF-8", 2, webService, XmlConstants.XML_NAME_WSDL_DEFINITIONS);
            } else {
                throw new MojoExecutionException("Web service is empty");
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Cant import wsdl", e);
        }
    }
}
