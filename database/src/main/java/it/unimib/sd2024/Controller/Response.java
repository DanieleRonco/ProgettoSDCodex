package it.unimib.sd2024.Controller;

public class Response {
    private boolean isError;
    private ErrorKindType errorKind;
    private String message;
    private int affectedDocumentsCount;
    private int detectedDocumentsCount;
    private String[] retrievedDocuments;

    public Response() {
    }

    public String[] getRetrievedDocuments() {
        return retrievedDocuments;
    }

    public Response setRetrievedDocuments(String[] retrievedDocuments) {
        this.retrievedDocuments = retrievedDocuments;
        return this;
    }

    public int getDetectedDocumentsCount() {
        return detectedDocumentsCount;
    }

    public Response setDetectedDocumentsCount(int detectedDocumentsCount) {
        this.detectedDocumentsCount = detectedDocumentsCount;
        return this;
    }

    public int getAffectedDocumentsCount() {
        return affectedDocumentsCount;
    }

    public Response setAffectedDocumentsCount(int affectedDocumentsCount) {
        this.affectedDocumentsCount = affectedDocumentsCount;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isError() {
        return isError;
    }

    public Response setError(boolean error) {
        isError = error;
        return this;
    }

    public ErrorKindType getErrorKind() {
        return errorKind;
    }

    public Response setErrorKind(ErrorKindType errorKind) {
        this.errorKind = errorKind;
        return this;
    }
}
