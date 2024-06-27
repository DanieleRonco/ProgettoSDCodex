package it.unimib.sd2024.QueryBuilder.V1;


public class V1Query extends it.unimib.sd2024.Database.Query {
    public V1Query(V1QueryOperationType operation){
        super(operation);
        super.setVersion("V1");
    }
}
