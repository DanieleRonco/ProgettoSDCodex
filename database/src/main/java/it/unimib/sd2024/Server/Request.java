package it.unimib.sd2024.Server;

import javax.json.JsonObject;
import java.util.List;

public class Request {
    private String version;
    private String collectionName;
    private String operation;
    private List<Filter> filters;
    private List<UpdateDefinition> updates;
    private JsonObject document;

    public Request() {
    }

    public Request(String version, String collectionName, String operation, List<Filter> filters, List<UpdateDefinition> updates, JsonObject document) {
        this.version = version;
        this.collectionName = collectionName;
        this.operation = operation;
        this.filters = filters;
        this.updates = updates;
        this.document = document;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public List<UpdateDefinition> getUpdates() {
        return updates;
    }

    public void setUpdates(List<UpdateDefinition> updates) {
        this.updates = updates;
    }

    public JsonObject getDocument() {
        return document;
    }

    public void setDocument(JsonObject document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return "Request{" +
                "version='" + version + '\'' +
                ", operation='" + operation + '\'' +
                ", collectionName='" + collectionName + '\'' +
                ", filters=" + filters +
                ", updates=" + updates +
                ", document=" + document +
                '}';
    }

    public String toSaveString() {
        return "Request{" +
                "version='" + version + '\'' +
                ", operation='" + operation + '\'' +
                ", collectionName='" + collectionName + '\'' +
                ", filters=" + (filters == null ? "null" : "[REDACTED]len(" + filters.size() + ")") +
                ", updates=" + (updates == null ? "null" : "[REDACTED]len(" + updates.size() + ")") +
                ", document=" + (document == null ? "null" : "[REDACTED]") +
                '}';
    }
}

