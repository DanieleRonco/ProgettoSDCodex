package it.unimib.sd2024.QueryBuilder.V1;

public enum ComparisonType {
    EQUAL,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN,
    LESS_THAN_OR_EQUAL,
    NOT_EQUALS;

    public String getComparisonSymbol() {
        return switch (this) {
            case GREATER_THAN -> ">";
            case GREATER_THAN_OR_EQUAL -> ">=";
            case LESS_THAN -> "<";
            case LESS_THAN_OR_EQUAL -> "<=";
            case NOT_EQUALS -> "!=";
            default -> "=";
        };
    }

}
