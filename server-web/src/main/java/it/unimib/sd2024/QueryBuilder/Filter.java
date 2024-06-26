package it.unimib.sd2024.QueryBuilder;

import jakarta.json.bind.JsonbBuilder;

public class Filter {
    private KeyValue kv;
    private Filter nextFilter;
    private Filter beforeFilter;

    public Filter() {
        this.kv = new KeyValue();
    }

    private KeyValue getKv() {
        return this.kv;
    }

    public Filter add(String key, String value) {
        this.setKey(key);
        this.setValue(value);
        this.nextFilter = new Filter();
        this.nextFilter.beforeFilter = this;
        return this.nextFilter;
    }

    public String getKey() {
        return this.kv.getKey();
    }

    private void setKey(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty");
        }
        this.kv.setKey(key);
    }

    public String getValue() {
        return this.kv.getValue();
    }

    private void setValue(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Value must not be null or empty");
        }
        this.kv.setValue(value);
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        Filter current = this;
        if (this.kv.isEmpty()) {
            if (this.beforeFilter == null) {
                return "{}";
            } else {
                current = this.beforeFilter;
            }
        }

        while (current.beforeFilter != null) {
//            return JsonbBuilder.create().toJson(current.getKV());
            sb.append("\"").
                    append(current.
                            getKey().
                            replace("\"", "\\\"")).
                    append("\":\"").
                    append(current.
                            getValue().
                            replace("\"", "\\\""))
                    .append("\"");
            current = current.beforeFilter;
            if (current.beforeFilter != null) {
                sb.append(",");
            }
        }
        return "{" + sb.toString() + "}";
    }
}


