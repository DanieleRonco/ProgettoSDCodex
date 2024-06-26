package it.unimib.sd2024.QueryBuilder;

public class V1FilterableQuery extends V1CollectionOperationQuery {
    private Filter filter;

    public V1FilterableQuery(QueryOperationType operation) {
        super(operation);
    }

    public V1FilterableQuery(QueryOperationType operation, String collection) {
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
}
