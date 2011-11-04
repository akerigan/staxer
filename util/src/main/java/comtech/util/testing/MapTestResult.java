package comtech.util.testing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Результат теста, содержащий параметры результата в карте
 *
 * @author Ванин Борис
 *         Date and time: 09.07.2007 16:39:10
 */
public class MapTestResult extends SimpleTestResult {

    private Map<String, String> parameters = new HashMap<String, String>();

    public MapTestResult(String testName, State state, long duration) {
        super(testName, state, duration);
    }

    /**
     * Добавить параметр результата
     *
     * @param name  имя параметра
     * @param value значение параметра
     */
    public void addParameter(String name, String value) {
        this.parameters.put(name, value);
    }

    /**
     * @return соответсвие имен параметров их значениям
     */
    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }
}
