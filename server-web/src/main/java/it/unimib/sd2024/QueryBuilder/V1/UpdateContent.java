package it.unimib.sd2024.QueryBuilder.V1;

import jakarta.json.bind.JsonbBuilder;

public class UpdateContent {
    private final String key;
    private final Object value;

    public UpdateContent(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.value;
    }

    public String build() {
        return JsonbBuilder.create().toJson(this, UpdateContent.class);
    }
}
