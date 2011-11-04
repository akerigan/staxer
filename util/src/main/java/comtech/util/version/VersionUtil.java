package comtech.util.version;

import comtech.util.ResourceUtils;
import comtech.util.StringUtils;
import comtech.util.servlet.helper.HttpHelper;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * DateTime: 2010-08-11-12-37 (Europe/Moscow)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class VersionUtil {

    public static final String VERSION = "version";
    public static final String MANIFEST_PATH = "/META-INF/MANIFEST.MF";
    public static final String ATTRIBUTE_VERSION = "Build-Version";
    public static final String ATTRIBUTE_BUILD = "Build-SVN-Revision";
    public static final String ATTRIBUTE_BRANCH = "Branch";

    public static void readVersionFromManifest(
            VersionBean versionBean, HttpHelper httpHelper, Log log
    ) {
        if (!httpHelper.isApplicationAttributeExists(VERSION)) {
            try {
                Manifest mf = new Manifest();
                InputStream webResourceInputStream = ResourceUtils.getWebResourceInputStream(
                        httpHelper, MANIFEST_PATH
                );
                mf.read(webResourceInputStream);
                webResourceInputStream.close();

                Attributes atts = mf.getMainAttributes();

                String versionNumber = atts.getValue(ATTRIBUTE_VERSION);
                if (!StringUtils.isEmpty(versionNumber)) {
                    versionBean.setVersionNumber(versionNumber);
                }
                String buildNumber = atts.getValue(ATTRIBUTE_BUILD);
                if (!StringUtils.isEmpty(buildNumber)) {
                    versionBean.setBuildNumber(buildNumber);
                }
                String branchName = atts.getValue(ATTRIBUTE_BRANCH);
                if (!StringUtils.isEmpty(branchName)) {
                    versionBean.setBranchName(branchName);
                }
            } catch (IOException e) {
                log.warn(MANIFEST_PATH + " reading problem: " + e.getMessage());
            }
            httpHelper.setApplicationAttribute(VERSION, versionBean);
        }
    }

}
