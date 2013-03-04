package org.staxer.sample.bean;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.HashMap;
import java.util.Map;

@XmlEnum
public enum EnumType {

    @XmlEnumValue("one")
    ONE("one"),
    @XmlEnumValue("two")
    TWO("two"),
    @XmlEnumValue("three")
    THREE("three");

    private static Map<String, EnumType> map;
    private String code;

    static {
        map = new HashMap<String, EnumType>();
        for (EnumType value : EnumType.values()) {
            map.put(value.code, value);
        }
    }

    EnumType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static EnumType getByCode(String code) {
        return map.get(code);
    }

}
