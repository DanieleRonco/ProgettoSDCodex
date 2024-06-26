package it.unimib.sd2024.QueryBuilder;

public class V1FindQuery extends V1FilterableQuery {
    public V1FindQuery(){
        super(QueryOperationType.FIND);
    }

    public V1FindQuery(String collection){
        this();
        this.setCollection(collection);
    }

    public V1FindQuery setCollection(String collection){
        super.setCollection(collection);
        return this;
    }


}
