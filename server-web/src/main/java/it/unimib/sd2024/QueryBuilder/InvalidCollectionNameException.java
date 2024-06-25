package it.unimib.sd2024.QueryBuilder;

public class InvalidCollectionNameException extends IllegalQueryParameterException {
    public InvalidCollectionNameException(String message) {
        super(message);
    }
}
