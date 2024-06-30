package it.unimib.sd2024.QueryBuilder.V1;

public class V1FilterableQuery extends V1CollectionOperationQuery {
    private Filter filter;

    public V1FilterableQuery(V1QueryOperationType operation) {
        super(operation);
    }

    public V1FilterableQuery(V1QueryOperationType operation, String collection) {
        this(operation);
        this.setCollection(collection);
    }

    public V1FilterableQuery filter(Filter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("Filter must not be null");
        }
        this.filter = filter;
        return this;
    }

    public String build() {
        if (this.filter == null) {
            // match all if no filter
            this.filter = new Filter();
        }

        var query = super.build();
        return query.substring(0, query.length() - 2) +
                ",\"filter\":" +
                this.filter.build() +
                "}";
    }
}
