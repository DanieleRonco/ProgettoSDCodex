package it.unimib.sd2024.Controller;

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

