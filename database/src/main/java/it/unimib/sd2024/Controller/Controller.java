package it.unimib.sd2024.Controller;

import it.unimib.sd2024.Logger.Logger;
import it.unimib.sd2024.Server.Filter;
import it.unimib.sd2024.Server.Request;
import it.unimib.sd2024.StorageManager.InvalidCollectionName;
import it.unimib.sd2024.StorageManager.StorageManager;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Controller {
    private final Logger log;
    private final StorageManager storageManager;

    public Controller(Logger log) throws IOException {
        this.log = log;
        this.storageManager = new StorageManager();
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
            case QueryOperationType.DROP -> dropCollection(request.getCollectionName());
            case QueryOperationType.INSERT -> insertDocument(request.getCollectionName(), request.getDocument());
            case QueryOperationType.FIND -> findDocument(request.getCollectionName(), request.getFilters());
//                return updateDocument(request.getCollectionName(), request.getFilters(), request.getUpdates());
//                return deleteDocument(request.getCollectionName(), request.getFilters());
            default -> new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.INVALID_REQUEST)
                    .setMessage("operation not supported, check documentation for supported operations");
        };
    }

    public Response handlePingQuery() {
        log.Info("handle ping query");
        return new Response().setMessage("pong");
    }

    public Response handleCreateQuery(Request req) {
        log.Info("handle create query");
        try {
            storageManager.createNewCollectionStorage(req.getCollectionName());
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
        return new Response().setMessage("collection [" + req.getCollectionName() + "] created");
    }

    public Response dropCollection(String collectionName) {
        log.Info("handle drop query");
        try {
            storageManager.deleteCollectionStorage(collectionName);
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
        return new Response().setMessage("collection [" + collectionName + "] dropped");
    }

    public Response insertDocument(String collectionName, JsonObject document) {
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

//        Scanner collection = null;
//        Writer w = null;
        try {
//            log.Debug("locking write lock for collection [" + collectionName + "]");

//            we do this check only if the document id is not generated
//            because we cannot trust the id provided by the user
//            the generated id is a UUID, so we can trust that it will be unique
//            in the system as we are not going to generate UUID fast enough to have a collision
//            https://en.wikipedia.org/w/index.php?title=Universally_unique_identifier&oldid=755882275#Random_UUID_probability_of_duplicates
            if (!isDocumentIdGenerated) {
                log.Debug("checking if document with id [" + documentId + "] already exists in collection [" + collectionName + "]");
                var checkByIdFilter = new ArrayList<Filter>();
                checkByIdFilter.add(new Filter("EQUAL", "_id", documentId));
                var documents = storageManager.filterCollection(collectionName, checkByIdFilter);
                if (!documents.isEmpty()) {
                    log.Info("document with id [" + documentId + "] already exists in collection [" + collectionName + "]");
                    return new Response()
                            .setError(true)
                            .setErrorKind(ErrorKindType.DOCUMENT_ID_ALREADY_EXISTS)
                            .setMessage("document with id [" + documentId + "] already exists in collection [" + collectionName + "]");

                }
//                log.Debug("read lock");
//                storageManager.ReadLock
//                CollectionStorage(collectionName);
//                collection = storageManager.OpenReaderCollectionStorage(collectionName);
//                while (collection.hasNext()) {
//                    var line = collection.nextLine();
//                    var json = Json.createReader(new StringReader(line)).readObject();
//                    if (json.get("_id").toString().equals(documentId)) {
//                        log.Info("document with id [" + documentId + "] already exists in collection [" + collectionName + "]");
////                        storageManager.ReadUnlockCollectionStorage(collectionName);
//                        return new Response()
//                                .setError(true)
//                                .setErrorKind(ErrorKindType.DOCUMENT_ID_ALREADY_EXISTS)
//                                .setMessage("document with id [" + documentId + "] already exists in collection [" + collectionName + "]");
//                    }
//                }
//                log.Debug("unlocking read");
//                storageManager.ReadUnlockCollectionStorage(collectionName);
            }
            log.Debug("sleeping to check how things work");
            Thread.sleep(5000);
            log.Debug("done sleeping");
            //            log.Debug("write lock");
//            storageManager.WriteLockCollectionStorage(collectionName);
//            log.Debug("opening writer");
//            w = storageManager.OpenWriterCollectionStorage(collectionName);
//            log.Debug("writing to collection");
            storageManager.appendToCollection(collectionName, builder.build());

        } catch (IOException | InvalidCollectionName e) {
            log.Error("error inserting document into collection: " + e);
            return new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.INTERNAL_ERROR)
                    .setMessage("error inserting document into collection: " + e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            log.Debug("releasing resources for collection [" + collectionName + "]");
//            if (storageManager.isReadLocked(collectionName))
//                storageManager.ReadUnlockCollectionStorage(collectionName);
//            if (storageManager.isWriteLocked(collectionName))
//                storageManager.WriteUnlockCollectionStorage(collectionName);
        }

        return new Response().setMessage("inserted document into collection [" + collectionName + "]");
    }

    private Response findDocument(String collectionName, List<Filter> filters) {
        log.Info("handle find query");
        var collectionExists = checkIfCollectionExists(collectionName);
        if (collectionExists != null) {
            return collectionExists;
        }

        try {
            var documents = storageManager.filterCollection(collectionName, filters);
            String[] docs = new String[documents.size()];
            for (int i = 0; i < documents.size(); i++) {
                docs[i] = documents.get(i).toString();
            }
            return new Response().setRetrievedDocuments(docs);
        } catch (IOException | InvalidCollectionName e) {
            log.Error("error finding documents in collection: " + e);
            return new Response()
                    .setError(true)
                    .setErrorKind(ErrorKindType.INTERNAL_ERROR)
                    .setMessage("error finding documents in collection: " + e);
        }
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
}

