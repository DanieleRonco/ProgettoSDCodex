package it.unimib.sd2024.QueryBuilder;

public class V1DropQueryV1 extends V1CollectionOperationQuery {
    private String collectionName;

    public V1DropQueryV1() {
        super(QueryOperationType.DROP);
    }

    public V1DropQueryV1(String collection) {
        this();
        this.setCollection(collection);
    }
}
