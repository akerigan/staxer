package comtech.util.version;

/**
 * DateTime: 2010-08-11-12-35 (Europe/Moscow)
 *
 * @author Vlad Vinichenko (akerigan@gmail.com)
 */
public class VersionBean {

    public static final String DEFAULT_BUILD_NUMBER_NAME = "babbler-build";
    public static final String DEFAULT_BRANCH_NAME = "unknown";

    private String versionNumber;
    private String buildNumberName = DEFAULT_BUILD_NUMBER_NAME;
    private String buildNumber;
    private String branchName = DEFAULT_BRANCH_NAME;

    public VersionBean() {
    }

    public VersionBean(String versionNumber, String buildNumberName, String buildNumber) {
        this.versionNumber = versionNumber;
        this.buildNumberName = buildNumberName;
        this.buildNumber = buildNumber;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getBuildNumberName() {
        return buildNumberName;
    }

    public void setBuildNumberName(String buildNumberName) {
        this.buildNumberName = buildNumberName;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
