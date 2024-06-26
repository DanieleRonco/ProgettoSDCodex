package it.unimib.sd2024.QueryBuilder;

public enum QueryOperationType {
    PING,
    CREATE,
    DROP,
    FIND,
    INSERT,
    UPDATE,
    DELETE;

    public String getOperation() {
        return this.toString().toUpperCase();
    }
}

