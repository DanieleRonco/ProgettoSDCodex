package it.unimib.sd2024.QueryBuilder.V1;

public class V1DropQuery extends V1CollectionOperationQuery {
    private String collectionName;

    public V1DropQuery() {
        super(V1QueryOperationType.DROP);
    }

    public V1DropQuery(String collection) {
        this();
        this.setCollection(collection);
    }
}
