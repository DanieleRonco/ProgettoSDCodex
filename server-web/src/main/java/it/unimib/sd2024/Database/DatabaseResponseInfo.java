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

    public int getDetectedDocumentsCount() {
        return detectedDocumentsCount;
    }

    public int getAffectedDocumentsCount() {
        return affectedDocumentsCount;
    }

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return isError;
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
}
