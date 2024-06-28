package it.unimib.sd2024.QueryBuilder.V1;

public class V1CreateQuery extends V1CollectionOperationQuery {
    private String collectionName;

    public \V1CreateQuery() {
        super(V1QueryOperationType.CREATE);
    }

    public V1CreateQuery(String collection) {
        this();
        this.setCollection(collection);
    }
}
