package it.unimib.sd2024.QueryBuilder.V1;

public class KeyValue {
    private String key;
    private Object value;

    public KeyValue() {
    }

    public KeyValue(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return (this.key == null || this.key.isEmpty()) && (this.value == null);
    }

    @Override
    public String toString() {
        return "KeyValue{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
