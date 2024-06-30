package it.unimib.sd2024.QueryBuilder.V1;

public class FilterContent {
    private KeyValue kv;
    private ComparisonType comp;

    public FilterContent() {
        this.kv = new KeyValue();
    }

    public FilterContent(String key, Object value, ComparisonType comp) {
        this();
        this.kv = new KeyValue(key, value);
        this.setComparison(comp);
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

    public Object getValue() {
        return this.kv.getValue();
    }

    private void setValue(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Value must not be null or empty");
        }
        this.kv.setValue(value);
    }

    public String getValueType() {
        return this.kv.getValueType();
    }

    public ComparisonType getComparison() {
        return this.comp;
    }

    private void setComparison(ComparisonType comparison) {
        if (comparison == null) {
            throw new IllegalArgumentException("Comparison must not be null");
        }
        this.comp = comparison;
    }
}
