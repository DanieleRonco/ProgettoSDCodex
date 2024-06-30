package it.unimib.sd2024.QueryBuilder.V1;

import jakarta.json.bind.JsonbBuilder;

public class KeyValue {
    private String key;
    private Object value;
    private String valueType;

    public KeyValue() {
    }

    public KeyValue(String key, Object value) {
        this.setKey(key);
        this.setValue(value);
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

        if(value==null){
            this.valueType = "null";
            return;
        }

        this.valueType = value.getClass().getSimpleName();
        if (valueType.equals("Integer") || valueType.equals("Double")) {
            valueType = "Number";
        }
    }

    public String getValueType() {
        return this.valueType;
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

    public String build() {
        return JsonbBuilder.create().toJson(this, KeyValue.class);
    }
}
