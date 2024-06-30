package it.unimib.sd2024.QueryBuilder.V1;

import jakarta.json.bind.JsonbBuilder;

public class Filter {
    private FilterContent content;
    private Filter nextFilter;
    private Filter beforeFilter;

    public Filter() {
    }

    public Filter add(String key, Object value) {
        return this.add(key, value, ComparisonType.EQUAL);
    }

    public Filter add(String key, Object value, ComparisonType comparison) {
        this.content = new FilterContent(key, value, comparison);
        this.nextFilter = new Filter();
        this.nextFilter.beforeFilter = this;
        return this.nextFilter;
    }

    private FilterContent getFilterContent() {
        return this.content;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        Filter current = this;
        if (this.content == null) {
            if (this.beforeFilter == null) {
                return "[{}]";
            } else {
                current = this.beforeFilter;
            }
        }

        do {
            sb.append(JsonbBuilder.create().toJson(current.getFilterContent(), FilterContent.class));
            current = current.beforeFilter;
            if (current != null && current.nextFilter != null) {
                sb.append(",");
            }
        } while (current != null);
        return "[" + sb.toString() + "]";
    }
}
