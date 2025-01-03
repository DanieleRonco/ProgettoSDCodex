package it.unimib.sd2024.Database;

import jakarta.json.bind.JsonbBuilder;

public class DatabaseResponse {
    private final String rawResponse;
    private DatabaseResponseInfo responseInfo;

    public DatabaseResponse(String rawResponse) {
        this.rawResponse = rawResponse;
        parseResponse();
    }

    private void parseResponse() {
        try {
            this.responseInfo = JsonbBuilder.create().fromJson(rawResponse, DatabaseResponseInfo.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid response format");
        }
    }

    public String[] getRetrievedDocuments() {
        return responseInfo.getRetrievedDocuments();
    }

    public int getDetectedDocumentsCount() {
        return responseInfo.getDetectedDocumentsCount();
    }

    public int getAffectedDocumentsCount() {
        return responseInfo.getAffectedDocumentsCount();
    }

    public String getErrorMessage() {
        return responseInfo.getMessage();
    }

    public boolean isErrorResponse() {
        return responseInfo.isError();
    }
}