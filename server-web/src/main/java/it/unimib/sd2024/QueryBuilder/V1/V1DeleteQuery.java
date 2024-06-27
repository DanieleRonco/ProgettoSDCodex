package it.unimib.sd2024.QueryBuilder.V1;

public class V1DeleteQuery extends V1FilterableQuery{
    public V1DeleteQuery(){
        super(V1QueryOperationType.DELETE);
    }

    public V1DeleteQuery(String collection){
        this();
        this.setCollection(collection);
    }

    public V1DeleteQuery setCollection(String collection){
        super.setCollection(collection);
        return this;
    }
    public V1DeleteQuery filter(Filter filter) {
        super.filter(filter);
        return this;
    }
}
