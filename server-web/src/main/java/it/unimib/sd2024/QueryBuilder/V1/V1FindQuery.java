package it.unimib.sd2024.QueryBuilder.V1;

public class V1FindQuery extends V1FilterableQuery {
    public V1FindQuery(){
        super(V1QueryOperationType.FIND);
    }

    public V1FindQuery(String collection){
        this();
        this.setCollection(collection);
    }

    public V1FindQuery setCollection(String collection){
        super.setCollection(collection);
        return this;
    }

    public V1FindQuery filter(Filter filter) {
        super.filter(filter);
        return this;
    }
}
