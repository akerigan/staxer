package comtech.staxer.plugin;

import comtech.staxer.domain.WebService;
import comtech.util.ResourceUtils;
import comtech.util.file.FileUtils;
import comtech.util.xml.XmlConstants;
import comtech.util.xml.XmlUtils;
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
     * Charset of wsdl file
     *
     * @parameter default-value="UTF-8"
     */
    private String wsdlCharset;

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
            WebService webService;
            if (wsdlUrl.startsWith("http")) {
                String xml = ResourceUtils.getUrlContentAsString(wsdlUrl, httpUser, httpPassword);
                if (xml != null) {
                    webService = XmlUtils.readXml(
                            new StringReader(xml), WebService.class,
                            XmlConstants.XML_NAME_WSDL_DEFINITIONS
                    );
                } else {
                    throw new MojoExecutionException("Url content is empty");
                }
            } else {
                File wsdlFile = new File(baseDir, wsdlUrl);
                InputStream inputStream = new FileInputStream(wsdlFile);
                webService = XmlUtils.readXml(
                        inputStream, wsdlCharset, WebService.class,
                        XmlConstants.XML_NAME_WSDL_DEFINITIONS
                );
                inputStream.close();
            }
            if (webService != null) {
                File wsdlFile = new File(baseDir, "/src/main/resources/" + generatedWsdl);
                FileUtils.mkdirs(wsdlFile.getParentFile(), null);
                FileOutputStream outputStream = new FileOutputStream(wsdlFile);
                XmlUtils.writeXml(outputStream, "UTF-8", 4, webService, XmlConstants.XML_NAME_WSDL_DEFINITIONS);
                outputStream.flush();
                outputStream.close();
            } else {
                throw new MojoExecutionException("Web service is empty");
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Cant import wsdl", e);
        }
    }
}
