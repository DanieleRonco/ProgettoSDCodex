package it.unimib.sd2024.QueryBuilder.V1;

public class V1UpdateQuery extends V1FilterableQuery {
    private UpdateDefinition updates;

    public V1UpdateQuery() {
        super(V1QueryOperationType.UPDATE);
    }

    public V1UpdateQuery(String collection) {
        this();
        this.setCollection(collection);
    }

    public V1UpdateQuery setCollection(String collection) {
        super.setCollection(collection);
        return this;
    }

    public V1UpdateQuery filter(Filter filter) {
        super.filter(filter);
        return this;
    }

    public V1UpdateQuery updateOn(UpdateDefinition definition) {
        if (this.updates == null) {
            this.updates = definition;
        } else {
            this.updates.append(definition);
        }
        return this;
    }

    public String build() throws IllegalArgumentException {
        if (this.updates == null) {
            throw new IllegalArgumentException("Updates must not be null");
        }

        var query = super.build();
        return query.substring(0, query.length() - 1) +
                ",\"update\":" +
                this.updates.build() +
                "}";
    }
}
