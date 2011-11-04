package comtech.util.testing;

/**
 * @author Ванин Борис
 *         Date and time: 17.07.2007 12:01:05
 */
public abstract class AbstractTest implements Test {

    private String name;
    private long startTime = 0;

    public AbstractTest(String name) {
        this.name = name;
    }

    public final TestResult test() {
        startTime = System.currentTimeMillis();
        return doTest();
    }

    protected abstract TestResult doTest();

    protected long getDuration() {
        return System.currentTimeMillis() - startTime;
    }

    public String getName() {
        return this.name;
    }

    protected TestResult createTestResult(TestResult.State state) {
        return new SimpleTestResult(getName(), state, getDuration());
    }

    protected TestResult createCantTestResult(String cause) {
        return createTestResultWithMessage(TestResult.State.cantTest, "cause", cause);
    }

    protected TestResult createErrorTestResult(String error) {
        return createTestResultWithMessage(TestResult.State.error, "error", error);
    }

    protected TestResult createTestResultWithMessage(TestResult.State state, String name, String message) {
        MapTestResult result = new MapTestResult(getName(), state, getDuration());
        result.addParameter(name, message);
        return result;
    }

    protected TestResult createErrorTestResult() {
        return createTestResult(TestResult.State.error);
    }

    protected TestResult createPassedTestResult() {
        return createTestResult(TestResult.State.passed);
    }
}
