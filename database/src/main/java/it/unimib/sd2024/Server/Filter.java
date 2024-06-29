package it.unimib.sd2024.Server;

import java.lang.reflect.Type;

public class Filter {
    private String comparison;
    private String key;
    private Object value;

    public Filter() {
    }

    public Filter(String comparison, String key, String value) {
        this.comparison = comparison;
        this.key = key;
        this.value = value;
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

    public void setValue(String value) {
        this.value = value;
    }

    public Type getValueType() {
        return value.getClass();
    }

    @Override
    public String toString() {
        return "Filter{" +
                "key='" + key + '\'' +
                ", comparison='" + comparison + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public String toSaveString() {
        return key + "=[REDACTED]";
    }
}
