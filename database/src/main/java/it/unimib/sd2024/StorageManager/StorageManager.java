package it.unimib.sd2024.StorageManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StorageManager {
    private Path pathToStorageDir;

    public StorageManager() throws IOException {
        this(Paths.get("collections").toAbsolutePath().toString());
    }

    public StorageManager(String pathToStorageDir) throws IOException {
        this.pathToStorageDir = Files.createDirectories(Paths.get(pathToStorageDir));
    }

    public Path getPathToStorageDir() {
        return pathToStorageDir;
    }

    private boolean isValidCollectionName(String collectionName){
        return !collectionName.isEmpty() &&
                collectionName.length() < 100 &&
                !collectionName.contains("\n");
    }

    public void createNewCollectionStorage(String collectionName) throws IOException {
        Files.createFile(Paths.get(pathToStorageDir.toString(), collectionName + ".json"));
    }
}
