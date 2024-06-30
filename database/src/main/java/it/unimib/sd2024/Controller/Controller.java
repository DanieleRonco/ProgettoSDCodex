package it.unimib.sd2024.Controller;

import it.unimib.sd2024.Logger.Logger;
import it.unimib.sd2024.Server.Filter;
import it.unimib.sd2024.Server.Request;
import it.unimib.sd2024.Server.UpdateDefinition;
import it.unimib.sd2024.StorageManager.InvalidCollectionName;
import it.unimib.sd2024.StorageManager.StorageManager;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Controller {
    private final Logger log;
    private StorageManager storageManager;

    public Controller(Logger log, StorageManager s) throws IOException {
        this.log = log;
        this.storageManager = s;
    }

    private String generateNewId() {
        return UUID.randomUUID().toString();
    }

    private Response checkIfCollectionExists(String collection) {
        try {
            if (!storageManager.doesCollectionExist(collection)) {
                log.Info("collection [" + collection + "] does not exists");
                return new Response()
                        .setError(true)
                        .setErrorKind(ErrorKindType.COLLECTION_DOES_NOT_EXIST)
                        .setMessage("collection [" + collection + "] does not exist");
            }
        } catch (InvalidCollectionName e) {
            log.Info("invalid collection name: " + e);
            return new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.INVALID_COLLECTION_NAME)
                    .setMessage("collection [" + collection + "] is unprocessable");
        }
        return null;
    }

    private String isRequestProcessable(Request request) {
        if (request == null) {
            return "request can not be null";
        }
        if (!request.getVersion().equals("V1")) {
            //at the moment only v1 is supported
            return "version not supported, currently only protocol version V1 is supported";
        }

        if (request.getOperation().isEmpty()) {
            return "operation can not be empty";
        }

        //check if operation is a query operation type
        for (var c : QueryOperationType.values()) {
            if (c.name().equals(request.getOperation().toUpperCase())) {
                return null;
            }
        }

        return "operation not supported, check documentation for supported operations";
    }

    public Response handleRequest(Request request) {
        log.Debug("received request: " + request);
        var reasonForUnprocessableRequest = isRequestProcessable(request);
        if (reasonForUnprocessableRequest != null) {
            log.Info("received unprocessable request: " + request.toSaveString() + " reason: " + reasonForUnprocessableRequest);
            return new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.INVALID_REQUEST)
                    .setMessage("request in not processable: " + reasonForUnprocessableRequest);
        }

        return handleQuery(request);
    }

    public Response handleQuery(Request request) {
        return switch (QueryOperationType.valueOf(request.getOperation())) {
            case QueryOperationType.PING -> handlePingQuery();
            case QueryOperationType.CREATE -> handleCreateQuery(request);
            case QueryOperationType.DROP -> handleDropQuery(request.getCollectionName());
            case QueryOperationType.INSERT -> handleInsertQuery(request.getCollectionName(), request.getDocument());
            case QueryOperationType.FIND -> handleFindQuery(request.getCollectionName(), request.getFilters());
            case QueryOperationType.UPDATE ->
                    handleUpdateQuery(request.getCollectionName(), request.getFilters(), request.getUpdates());
            case QueryOperationType.DELETE -> handleDeleteQuery(request.getCollectionName(), request.getFilters());
        };
    }

    public Response handlePingQuery() {
        log.Info("handle ping query");
        var response = new Response().setMessage("pong");
        log.Debug("ping query handled successfully");
        return response;
    }

    public Response handleCreateQuery(Request req) {
        log.Info("handle create query");
        try {
            storageManager.createCollectionStorage(req.getCollectionName());
        } catch (FileAlreadyExistsException e) {
            log.Info("collection already exists: " + e);
            return new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.COLLECTION_ALREADY_EXISTS)
                    .setMessage("collection [" + req.getCollectionName() + "] already exists");
        } catch (NoSuchFileException | InvalidCollectionName e) {
            log.Info("error creating collection storage: " + e);
            return new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.INVALID_COLLECTION_NAME)
                    .setMessage("collection [" + req.getCollectionName() + "] is unprocessable");
        } catch (IOException e) {
            log.Error("error creating collection storage: " + e);
            return new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.INTERNAL_ERROR)
                    .setMessage("error creating collection storage: " + e);
        }
        var response = new Response().setMessage("collection [" + req.getCollectionName() + "] created");
        log.Debug("create query handled successfully");
        return response;
    }

    public Response handleDropQuery(String collectionName) {
        log.Info("handle drop query");
        int deletedDocuments = 0;
        try {
            deletedDocuments = storageManager.deleteCollectionStorage(collectionName);
            log.Debug("done dropping collection storage, deleted " + deletedDocuments + " documents");
        } catch (InvalidCollectionName e) {
            log.Info("invalid collection name: " + e);
            return new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.INVALID_COLLECTION_NAME)
                    .setMessage("collection [" + collectionName + "] is unprocessable");
        } catch (NoSuchFileException e) {
            log.Info("collection does not exist: " + e);
            return new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.COLLECTION_DOES_NOT_EXIST)
                    .setMessage("collection [" + collectionName + "] does not exist");
        } catch (IOException e) {
            log.Error("error dropping collection storage: " + e);
            return new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.INTERNAL_ERROR)
                    .setMessage("error dropping collection storage: " + e);
        }

        var response = new Response()
                .setMessage("collection [" + collectionName + "] dropped")
                .setDetectedDocumentsCount(deletedDocuments)
                .setAffectedDocumentsCount(deletedDocuments);
        log.Debug("drop query handled successfully");
        return response;
    }

    public Response handleInsertQuery(String collectionName, JsonObject document) {
        log.Info("handle insert query");
        var collectionExists = checkIfCollectionExists(collectionName);
        if (collectionExists != null) {
            return collectionExists;
        }

        var documentId = generateNewId();
        var isDocumentIdGenerated = true;
        var builder = Json.createObjectBuilder(document);
        try {
            var id = document.get("_id").toString();
            if (id.isEmpty()) {
                log.Debug("no id provided, generating new id");
                builder.add("_id", documentId);
            } else {
                isDocumentIdGenerated = false;
                documentId = id;
            }
        } catch (Exception e) {
            log.Debug("no id provided, generating new id");
            builder.add("_id", documentId);
        }

        try {
//            we do this check only if the document id is not generated
//            because we cannot trust the id provided by the user
//            the generated id is a UUID, so we can trust that it will be unique
//            in the system as we are not going to generate UUID fast enough to have a collision
//            https://en.wikipedia.org/w/index.php?title=Universally_unique_identifier&oldid=755882275#Random_UUID_probability_of_duplicates
            if (!isDocumentIdGenerated) {
                log.Debug("checking if document with id [" + documentId + "] already exists in collection [" + collectionName + "]");
                var checkByIdFilter = new ArrayList<Filter>();
                checkByIdFilter.add(new Filter("EQUAL", "_id", documentId, String.class));
                var documents = storageManager.filterDocuments(collectionName, checkByIdFilter);
                if (!documents.isEmpty()) {
                    log.Info("document with id [" + documentId + "] already exists in collection [" + collectionName + "]");
                    return new Response()
                            .setError(true)
                            .setErrorKind(ErrorKindType.DOCUMENT_ID_ALREADY_EXISTS)
                            .setMessage("document with id [" + documentId + "] already exists in collection [" + collectionName + "]");

                }
            }
            log.Debug("attempt to append document to collection");
            storageManager.appendDocumentToCollection(collectionName, builder.build());
            log.Debug("done writing to collection");
        } catch (IOException | InvalidCollectionName e) {
            log.Error("error inserting document into collection: " + e);
            return new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.INTERNAL_ERROR)
                    .setMessage("error inserting document into collection: " + e);
        }

        var response = new Response()
                .setMessage("inserted document into collection [" + collectionName + "]")
                .setAffectedDocumentsCount(1);
        log.Debug("insert query handled successfully");
        return response;
    }

    private Response handleFindQuery(String collectionName, List<Filter> filters) {
        log.Info("handle find query");
        var collectionExists = checkIfCollectionExists(collectionName);
        if (collectionExists != null) {
            return collectionExists;
        }

        try {
            var documents = storageManager.filterDocuments(collectionName, filters);
            String[] docs = new String[documents.size()];
            for (int i = 0; i < documents.size(); i++) {
                docs[i] = documents.get(i).toString();
            }

            var response = new Response()
                    .setDetectedDocumentsCount(documents.size())
                    .setRetrievedDocuments(docs);
            log.Debug("find query handled successfully");
            return response;
        } catch (IOException | InvalidCollectionName e) {
            log.Error("error finding documents in collection: " + e);
            return new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.INTERNAL_ERROR)
                    .setMessage("error finding documents in collection: " + e);
        }
    }

    private Response handleUpdateQuery(String collectionName, List<Filter> filters, List<UpdateDefinition> updates) {
        log.Info("handle update query");
        var collectionExists = checkIfCollectionExists(collectionName);
        if (collectionExists != null) {
            return collectionExists;
        }

        try {
            var response = new Response();
            var documents = storageManager.filterDocuments(collectionName, filters);
            log.Debug("found " + documents.size() + " documents");
            response.setDetectedDocumentsCount(documents.size());
            for (var doc : documents) {
                log.Debug("updating document: " + doc.getString("_id"));
                JsonObjectBuilder newDoc = Json.createObjectBuilder(doc);
                for (var update : updates) {
                    log.Debug("updating field: " + update.getKey());
                    if (update.getValueType() == String.class) {
                        newDoc.add(update.getKey(), Json.createValue(update.getValue().toString()));
                    } else if (update.getValueType() == BigDecimal.class) {
                        newDoc.add(update.getKey(), Json.createValue(new BigDecimal(update.getValue().toString())));
                    } else if (update.getValueType() == Boolean.class) {
                        var value = update.getValue().toString();
                        newDoc.add(update.getKey(), value.equals("true") ? JsonValue.TRUE : JsonValue.FALSE);
                    } else if (update.getValueType() == null) {
                        newDoc.add(update.getKey(), JsonValue.NULL);
                    }
                }
                documents.set(documents.indexOf(doc), newDoc.build());
            }
            //todo: actually count which documents have been updated
            response.setAffectedDocumentsCount(documents.size());
            List<String> idsToDelete = new ArrayList<>();
            for (JsonObject document : documents) {
                idsToDelete.add(document.getString("_id"));
            }
            storageManager.deleteDocuments(collectionName, idsToDelete);
            for (JsonObject document : documents) {
                storageManager.appendDocumentToCollection(collectionName, document);
            }
            log.Debug("update query handled successfully");
            return response;
        } catch (IOException | InvalidCollectionName e) {
            log.Error("error updating documents in collection: " + e);
            return new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.INTERNAL_ERROR)
                    .setMessage("error finding documents in collection: " + e);
        }
    }

    private Response handleDeleteQuery(String collectionName, List<Filter> filters) {
        log.Info("handle delete query");
        var collectionExists = checkIfCollectionExists(collectionName);
        if (collectionExists != null) {
            return collectionExists;
        }

        try {
            var response = new Response();
            var documents = storageManager.filterDocuments(collectionName, filters);
            log.Debug("found " + documents.size() + " documents");
            response.setDetectedDocumentsCount(documents.size());

            response.setAffectedDocumentsCount(documents.size());
            List<String> idsToDelete = new ArrayList<>();
            for (JsonObject document : documents) {
                idsToDelete.add(document.getString("_id"));
            }
            storageManager.deleteDocuments(collectionName, idsToDelete);
            log.Debug("delete query handled successfully");
            return response;
        } catch (IOException | InvalidCollectionName e) {
            log.Error("error deleting documents in collection: " + e);
            return new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.INTERNAL_ERROR)
                    .setMessage("error finding documents in collection: " + e);
        }
    }
}

