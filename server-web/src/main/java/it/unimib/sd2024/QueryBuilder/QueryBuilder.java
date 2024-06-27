package it.unimib.sd2024.QueryBuilder;

import it.unimib.sd2024.QueryBuilder.V1.V1QueryBuilder;

public class QueryBuilder {

    public QueryBuilder() {
    }

    public static V1QueryBuilder V1() {
        return new V1QueryBuilder();
    }
}
