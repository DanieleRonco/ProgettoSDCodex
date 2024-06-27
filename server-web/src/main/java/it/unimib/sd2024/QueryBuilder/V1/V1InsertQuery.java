package it.unimib.sd2024.QueryBuilder.V1;

public class V1InsertQuery extends V1CollectionOperationQuery {
    private Object document;

    public V1InsertQuery() {
        super(V1QueryOperationType.INSERT);
    }

    public V1InsertQuery(String collection) {
        this();
        this.setCollection(collection);
    }

    public V1InsertQuery setCollection(String collection) {
        super.setCollection(collection);
        return this;
    }

    public V1InsertQuery insert(Object document) {
        if (document == null) {
            throw new IllegalArgumentException("Document must not be null");
        }
        this.document = document;
        return this;
    }

    public Object getDocument() {
        return this.document;
    }

    public String build() {
        if (this.document == null) {
            throw new IllegalArgumentException("Document not set");
        }
        return super.build();
    }
}
