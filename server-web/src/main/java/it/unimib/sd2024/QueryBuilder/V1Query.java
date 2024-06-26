package it.unimib.sd2024.QueryBuilder;

public class V1Query extends Query{
    public V1Query(QueryOperationType operation){
        super(operation);
        super.setVersion("V1");
    }
}
