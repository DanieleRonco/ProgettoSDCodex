package it.unimib.sd2024.QueryBuilder.V1;

public class InvalidCollectionNameException extends IllegalQueryParameterException {
    public InvalidCollectionNameException(String message) {
        super(message);
    }
}
