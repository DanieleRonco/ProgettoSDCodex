package it.unimib.sd2024.Database;

import java.util.Arrays;

public class DatabaseResponseInfo {
    private ErrorKindType errorKind;
    private boolean isError;
    private String message;
    private int affectedDocumentsCount;
    private int detectedDocumentsCount;
    private String[] retrievedDocuments;

    public DatabaseResponseInfo() {
    }

    public String[] getRetrievedDocuments() {
        return retrievedDocuments;
    }

    public void setRetrievedDocuments(String[] retrievedDocuments) {
        this.retrievedDocuments = retrievedDocuments;
    }

    public int getDetectedDocumentsCount() {
        return detectedDocumentsCount;
    }

    public void setDetectedDocumentsCount(int detectedDocumentsCount) {
        this.detectedDocumentsCount = detectedDocumentsCount;
    }

    public int getAffectedDocumentsCount() {
        return affectedDocumentsCount;
    }

    public void setAffectedDocumentsCount(int affectedDocumentsCount) {
        this.affectedDocumentsCount = affectedDocumentsCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    @Override
    public String toString() {
        return "DatabaseResponseInfo{" +
                "errorKind=" + errorKind +
                ", isError=" + isError +
                ", message='" + message + '\'' +
                ", affectedDocumentsCount=" + affectedDocumentsCount +
                ", detectedDocumentsCount=" + detectedDocumentsCount +
                ", retrievedDocuments=" + Arrays.toString(retrievedDocuments) +
                '}';
    }

    public ErrorKindType getErrorKind() {
        return errorKind;
    }

    public void setErrorKind(ErrorKindType errorKind) {
        this.errorKind = errorKind;
    }
}
