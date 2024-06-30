package it.unimib.sd2024.Server;

import java.lang.reflect.Type;

public class UpdateDefinition {
    private String key;
    private Object value;
    private Type valueType;

    public UpdateDefinition() {
    }

    public UpdateDefinition(String key, Object value, Type valueType) {
        this.key = key;
        this.value = value;
        this.valueType = valueType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public Type getValueType() {
        return valueType;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "UpdateDefinition{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public String toSaveString() {
        return key + "=[REDACTED]";
    }
}
