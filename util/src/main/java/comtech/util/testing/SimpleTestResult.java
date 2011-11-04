package comtech.util.testing;

/**
 * @author Ванин Борис
 *         Date and time: 11.07.2007 12:37:57
 */
public class SimpleTestResult implements TestResult {

    private String testName;
    private State state;
    private long duration;
    private String host;

    public SimpleTestResult(String testName, State state, long duration) {
        this.testName = testName;
        this.state = state;
        this.duration = duration;
    }

    public String getTestName() {
        return this.testName;
    }

    public State getState() {
        return this.state;
    }

    public long getDuration() {
        return this.duration;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
