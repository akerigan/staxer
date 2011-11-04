package comtech.util.testing;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Комплексный результат. Имеет худший статус всех своих подрезультатов.
 *
 * @author Ванин Борис
 *         Date and time: 09.07.2007 14:55:35
 */
public class ComplexTestResult implements TestResult {

    /**
     * статус результата, для комплексного ресультата статус с самым высоким уровнем
     */
    private State state = State.passed;

    /**
     * подрезультаты
     */
    private Collection<TestResult> results = new LinkedList<TestResult>();
    private String testName;

    public ComplexTestResult(String testName) {
        this.testName = testName;
    }

    public String getTestName() {
        return this.testName;
    }

    public State getState() {
        for (TestResult result : results) {
            if (state.getLevel() < result.getState().getLevel()) {
                state = result.getState();
            }
        }

        return state;
    }

    /**
     * @return суммарное значение всех длительных вложенных тестов
     */
    public long getDuration() {
        long totalDuration = 0;
        for (TestResult result : results) {
            totalDuration += result.getDuration();
        }

        return totalDuration;
    }

    /**
     * Добавляет результат в набор
     *
     * @param result результат
     */
    public void addResult(TestResult result) {
        results.add(result);
    }

    /**
     * @return список результатов
     */
    public Collection<TestResult> getResults() {
        return results;
    }

    public String getHost() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TestResult result : results) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(result.getHost());
        }
        return stringBuilder.toString();
    }

    public void setHost(String host) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
