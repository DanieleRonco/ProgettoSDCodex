package it.unimib.sd2024.QueryBuilder.V1;

public class V1QueryBuilder {

    public V1QueryBuilder() {
    }

    public V1PingQuery PING() {
        return new V1PingQuery();
    }

    public V1CreateQuery CREATE() {
        return new V1CreateQuery();
    }

    public V1CreateQuery CREATE(String collection) {
        return new V1CreateQuery(collection);
    }

    public V1DropQuery DROP() {
        return new V1DropQuery();
    }

    public V1DropQuery DROP(String collection) {
        return new V1DropQuery(collection);
    }

    public V1FindQuery FIND() {
        return new V1FindQuery();
    }

    public V1FindQuery FIND(String collection) {
        return new V1FindQuery(collection);
    }

    public V1DeleteQuery DELETE() {
        return new V1DeleteQuery();
    }

    public V1DeleteQuery DELETE(String collection) {
        return new V1DeleteQuery(collection);
    }

    public V1InsertQuery INSERT() {
        return new V1InsertQuery();
    }

    public V1InsertQuery INSERT(String collection) {
        return new V1InsertQuery(collection);
    }

    public V1UpdateQuery UPDATE() {
        return new V1UpdateQuery();
    }

    public V1UpdateQuery UPDATE(String collection) {
        return new V1UpdateQuery(collection);
    }
}


