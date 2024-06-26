package it.unimib.sd2024.QueryBuilder;

public class V1QueryBuilder {

    public V1QueryBuilder(){
    }

    public  V1PingQuery PING() {
        return new V1PingQuery();
    }

    public V1CreateQueryV1 CREATE() {
        return new V1CreateQueryV1();
    }

    public V1CreateQueryV1 CREATE(String collection){
        return new V1CreateQueryV1(collection);
    }

    public V1DropQueryV1 DROP() {
        return new V1DropQueryV1();
    }

    public V1DropQueryV1 DROP(String collection){
        return new V1DropQueryV1(collection);
    }

    public V1FindQuery FIND() {
        return new V1FindQuery();
    }
}


