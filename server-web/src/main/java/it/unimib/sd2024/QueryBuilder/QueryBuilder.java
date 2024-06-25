package it.unimib.sd2024.QueryBuilder;

public class QueryBuilder {

    public QueryBuilder() {
    }

    public static Query PING() {
        return new Query(queryOperation.PING);
    }

    public static Query CREATE(String collectionName) {
        return new Query(queryOperation.CREATE).setCollectionName(collectionName);
    }

    public static Query DROP(String collectionName) {
        return new Query(queryOperation.DROP).setCollectionName(collectionName);
    }
}
