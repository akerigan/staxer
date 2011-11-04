package comtech.util.testing;

/**
 * Тест
 *
 * @author Ванин Борис
 *         Date and time: 09.07.2007 13:19:17
 */
public interface Test {

    /**
     * Метод выполнения теста
     *
     * @return результат выполнения теста
     */
    public TestResult test();

    /**
     * @return уникальное название теста
     */
    public String getName();
}
