package comtech.util.testing;

/**
 * Результат выполнения теста
 *
 * @author Ванин Борис
 *         Date and time: 09.07.2007 13:20:23
 */
public interface TestResult {

    /**
     * Статус результата
     */
    public enum State {
        passed(0, "passed"), cantTest(9, "can_t_test"), error(10, "error");

        /**
         * Уровень. 0 = самый лучший из возможных, 10 = худший
         */
        private int level;
        private String name;

        State(int level, String name) {
            this.level = level;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }
    }

    /**
     * @return уникальное имя теста, результат которого содержит сущность
     */
    public String getTestName();

    /**
     * @return статус
     */
    public State getState();

    /**
     * @return длительность выполнения теста в миллисекундах
     */
    public long getDuration();

    public String getHost();

    public void setHost(String host);

}
