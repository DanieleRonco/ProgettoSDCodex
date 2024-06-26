package it.unimib.sd2024.QueryBuilder;

import jakarta.json.bind.JsonbBuilder;

public class Query {
    private String version = "1.0";
    private String collectionName;
    private QueryOperationType operation;

    public Query(QueryOperationType operation) {
        this.setOperation(operation);
    }

    public Query(QueryOperationType operation, String collectionName, String query) throws InvalidCollectionNameException {
//        this.version = version;
//        this.setOperation(operation);
//        this.setCollection(collectionName);
//        this.query = query;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public QueryOperationType getOperation() {
        return operation;
    }

    public Query setOperation(QueryOperationType operation) throws IllegalArgumentException {
        this.operation = operation;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String Version){
        this.version = Version;
    }

    public String build() {
        return JsonbBuilder.create().toJson(this);
    }
}
