package it.unimib.sd2024.QueryBuilder.V1;

public enum V1QueryOperationType {
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

