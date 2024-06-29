package it.unimib.sd2024.Server;

public class InvalidRequest extends Exception {
    public InvalidRequest(String message) {
        super(message);
    }
}
