package it.unimib.sd2024.StorageManager;

public class InvalidCollectionName extends Throwable {
    public InvalidCollectionName() {
        super("Invalid collection name");
    }
}
