package it.unimib.sd2024.QueryBuilder;

public class V1CollectionOperationQuery extends V1Query {
    private String collection;

    public V1CollectionOperationQuery(QueryOperationType operation) {
        super(operation);
    }

    private boolean isValidCollectionName(String collectionName) {
        // could be a more sophisticated check
        return !collectionName.isEmpty() &&
                collectionName.length() < 100 &&
                !collectionName.contains("\n");
    }

    public String getCollection() {
        return this.collection;
    }

    public V1CollectionOperationQuery setCollection(String collectionName) {
        if (!isValidCollectionName(collectionName)) {
            throw new InvalidCollectionNameException("Invalid collection name");
        }
        this.collection = collectionName;
        return this;
    }

    public String Build() {
        if (this.collection == null) {
            throw new InvalidCollectionNameException("Collection name not set");
        }
        if (this.getOperation() == null) {
            throw new InvalidCollectionNameException("Operation not set");
        }
        if (!this.isValidCollectionName(this.collection)) {
            throw new InvalidCollectionNameException("Invalid collection name");
        }
        return super.build();
    }
}
