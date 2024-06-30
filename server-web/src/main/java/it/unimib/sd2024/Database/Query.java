package it.unimib.sd2024.Database;

import it.unimib.sd2024.QueryBuilder.V1.V1QueryOperationType;
import jakarta.json.bind.JsonbBuilder;

public class Query {
    private String version = "1.0";
    private String collectionName;
    private V1QueryOperationType operation;
    public Query(V1QueryOperationType operation) {
        this.setOperation(operation);
    }

    public String getVersion() {
        return version;
    }

    protected void setVersion(String Version) {
        this.version = Version;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public V1QueryOperationType getOperation() {
        return operation;
    }

    private Query setOperation(V1QueryOperationType operation) throws IllegalArgumentException {
        this.operation = operation;
        return this;
    }

    public String build() {
        return JsonbBuilder.create().toJson(this)+"\n";
    }
}
