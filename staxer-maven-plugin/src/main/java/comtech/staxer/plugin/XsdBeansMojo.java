package comtech.staxer.plugin;

import comtech.staxer.StaxerUtils;
import comtech.staxer.domain.XmlSchema;
import comtech.util.ResourceUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.net.URI;

/**
 * Xsd beans generator goal
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @goal xsd-beans
 * @phase generate-sources
 */
public class XsdBeansMojo extends AbstractMojo {

    /**
     * Location base dir
     *
     * @parameter expression="${basedir}"
     * @required
     */
    private File baseDir;

    /**
     * URL of xsd file or relative (based on module path) path to xsd file
     *
     * @parameter
     * @required
     */
    private String xsdUrl;

    /**
     * Charset of xsd file
     *
     * @parameter default-value="UTF-8"
     */
    private String xsdCharset;

    /**
     * Relative path for beans saving
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

    public void execute() throws MojoExecutionException {
        try {
            URI uri;
            if (xsdUrl.startsWith("http")) {
                uri = URI.create(xsdUrl);
            } else {
                File wsdlFile = new File(baseDir, xsdUrl);
                uri = wsdlFile.toURI();
            }
            String xml = ResourceUtils.getUrlContentAsString(uri, httpUser, httpPassword, xsdCharset);
            if (xml == null) {
                throw new MojoExecutionException("Url content is empty");
            }
            XmlSchema xmlSchema = StaxerUtils.readXmlSchema("", uri, httpUser, httpPassword, xsdCharset);
            if (xmlSchema != null) {
                StaxerUtils.createXsdBeans(
                        xmlSchema, new File(baseDir, sourceDir), packageName, true
                );
            } else {
                throw new MojoExecutionException("Xml schema is empty");
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Cant generate java xsd beans", e);
        }
    }
}
