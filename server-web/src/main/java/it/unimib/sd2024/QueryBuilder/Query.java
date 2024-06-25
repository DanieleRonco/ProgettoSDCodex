package it.unimib.sd2024.QueryBuilder;

import jakarta.json.bind.JsonbBuilder;

public class Query {
    private String query;
    private String version = "1.0";
    private queryOperation operation;
    private String collectionName;

    public Query() {
    }

    public Query(queryOperation operation) {
        this.setOperation(operation);
    }

    public Query(queryOperation operation, String collectionName, String query) throws InvalidCollectionNameException {
        this.version = version;
        this.setOperation(operation);
        this.setCollectionName(collectionName);
        this.query = query;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public Query setCollectionName(String collectionName) {
        if (!isValidCollectionName(collectionName)) {
            throw new InvalidCollectionNameException("Invalid collection name");
        }
        this.collectionName = collectionName;
        return this;
    }

    public queryOperation getOperation() {
        return operation;
    }

    public Query setOperation(queryOperation operation) throws IllegalArgumentException {
        this.operation = operation;
        return this;
    }

    public String getVersion() {
        return version;
    }

    private boolean isValidCollectionName(String collectionName) {
        // could be a more sophisticated check
        return !collectionName.isEmpty() &&
                collectionName.length() < 100 &&
                !collectionName.contains("\n");
    }

    public String build() {
        return JsonbBuilder.create().toJson(this);
    }
}
