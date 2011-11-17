package comtech.initer.log.beans;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.HashMap;
import java.util.Map;

@XmlEnum
public enum LogLevelXml {

    @XmlEnumValue("FATAL")
    FATAL("FATAL"),
    @XmlEnumValue("ERROR")
    ERROR("ERROR"),
    @XmlEnumValue("WARN")
    WARN("WARN"),
    @XmlEnumValue("INFO")
    INFO("INFO"),
    @XmlEnumValue("DEBUG")
    DEBUG("DEBUG");

    private static Map<String, LogLevelXml> map;
    private String code;

    static {
        map = new HashMap<String, LogLevelXml>();
        for (LogLevelXml value : LogLevelXml.values()) {
            map.put(value.code, value);
        }
    }

    LogLevelXml(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static LogLevelXml getByCode(String code) {
        return map.get(code);
    }

}
