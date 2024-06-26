package it.unimib.sd2024.QueryBuilder;

public class V1CreateQueryV1 extends V1CollectionOperationQuery {
    private String collectionName;

    public V1CreateQueryV1() {
        super(QueryOperationType.CREATE);
    }

    public V1CreateQueryV1(String collection) {
        this();
        this.setCollection(collection);
    }
}
