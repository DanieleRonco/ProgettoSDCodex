package it.unimib.sd2024.Server;

import java.lang.reflect.Type;

public class Filter {
    private String comparison;
    private String key;
    private Object value;
    private Type valueType;

    public Filter() {
    }

    public Filter(String comparison, String key, Object value, Type valueType) {
        this.setComparison(comparison);
        this.setKey(key);
        this.setValue(value);
        this.setValueType(valueType);
    }

    public String getComparison() {
        return comparison;
    }

    public void setComparison(String comparison) {
        this.comparison = comparison;
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

    public void setValue(Object value) {
        this.value = value;
    }

    public Type getValueType() {
        return valueType;
    }

    private void setValueType(Type valueType) {
        this.valueType = valueType;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "key='" + key + '\'' +
                ", comparison='" + comparison + '\'' +
                ", value='" + value + '\'' +
                ", valueType=" + valueType +
                '}';
    }

    public String toSaveString() {
        return key + "=[REDACTED]";
    }
}
