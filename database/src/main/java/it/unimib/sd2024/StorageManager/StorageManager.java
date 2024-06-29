package it.unimib.sd2024.StorageManager;

import it.unimib.sd2024.Server.Filter;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class StorageManager {
    private static final char[] ILLEGAL_CHARACTERS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
    private final Path pathToStorageDir;
    private final HashMap<String, ReentrantReadWriteLock> locks;

    public StorageManager() throws IOException {
        this(Paths.get("collections").toAbsolutePath().toString());
    }

    public StorageManager(String pathToStorageDir) throws IOException {
        this.pathToStorageDir = Files.createDirectories(Paths.get(pathToStorageDir));
        this.locks = new HashMap<>();
    }

    public Path getPathToStorageDir() {
        return pathToStorageDir;
    }

    private boolean isValidCollectionName(String collectionName) {
        if (collectionName.isEmpty() || collectionName.length() > 100) {
            return false;
        }
        for (char c : ILLEGAL_CHARACTERS) {
            if (collectionName.indexOf(c) != -1) {
                return false;
            }
        }
        return true;
    }

    public void createNewCollectionStorage(String collectionName) throws IOException, InvalidCollectionName {
        if (!isValidCollectionName(collectionName)) {
            throw new InvalidCollectionName();
        }
        Files.createFile(Paths.get(pathToStorageDir.toString(), collectionName + ".json"));
//        System.out.println("Created new collection storage at: " + path);
//        Files.writeString(path, "[]");
    }

    public void deleteCollectionStorage(String collectionName) throws IOException, InvalidCollectionName {
        if (!isValidCollectionName(collectionName)) {
            throw new InvalidCollectionName();
        }
        var path = Paths.get(pathToStorageDir.toString(), collectionName + ".json");
        Files.delete(path);
    }

    /*
    * TODO:
    *  CHECK https://stackoverflow.com/questions/5999100/is-there-a-block-until-condition-becomes-true-function-in-java
    * serve per gestire i lock, usa la wait perché sti lock non mi tornano
    * creare una collection non ha locks
    * cancellare una collezione deve guardare se in quel momento é in fase di lettura o scrittura
    *
    * filtra deve aspettare che non ci siano scritture in corso ma ci possono essere lettura
    * la scrittura deve aspettare che non ci siano scritture o letture in corso perché potrebbe creare problemi con i buffers
    *
    * l'update prima legge le collezioni, il controller fa le modifiche, cancella le risorse definite (per id) e poi le
    * riscrive, altrimenti ci sono problemi di concorrenza
    * */

    public boolean doesCollectionExist(String collectionName) throws InvalidCollectionName {
        if (!isValidCollectionName(collectionName)) {
            throw new InvalidCollectionName();
        }
        var path = Paths.get(pathToStorageDir.toString(), collectionName + ".json");
        return Files.exists(path);
    }

    public Scanner OpenReaderCollectionStorage(String collectionName) throws IOException, InvalidCollectionName {
        if (!isValidCollectionName(collectionName)) {
            throw new InvalidCollectionName();
        }
        return new Scanner(Paths.get(pathToStorageDir.toString(), collectionName + ".json").toFile());
    }

    public List<JsonObject> filterCollection(String collection, List<Filter> filters) throws InvalidCollectionName, IOException {
        if (!isValidCollectionName(collection)) {
            throw new InvalidCollectionName();
        }

        if (!locks.containsKey(collection)) {
            locks.put(collection, new ReentrantReadWriteLock());
        }
        try {
            locks.get(collection).readLock().lock();

            var result = new ArrayList<JsonObject>();
            var s = new Scanner(Paths.get(pathToStorageDir.toString(), collection + ".json").toFile());

            while (s.hasNext()) {
                var line = s.nextLine();
                var json = Json.createReader(new StringReader(line)).readObject();
                boolean matches = false;
                if (filters == null) {
                    result.add(json);
                    continue;
                }

                for (var filter : filters) {
                    try {
                        var val = json.get(filter.getKey());
                        if (val == null) {
                            //TODO: should check what to do with a null value
                            break;
                        }

                        var filterValueType = filter.getValueType();
                        if (filterValueType == String.class) {
                            matches = switch (filter.getComparison()) {
                                case "GREATER_THAN" -> val.toString().compareTo(filter.getValue().toString()) > 0;
                                case "GREATER_THAN_OR_EQUAL" ->
                                        val.toString().compareTo(filter.getValue().toString()) >= 0;
                                case "LESS_THAN" -> val.toString().compareTo(filter.getValue().toString()) < 0;
                                case "LESS_THAN_OR_EQUAL" ->
                                        val.toString().compareTo(filter.getValue().toString()) <= 0;
                                case "NOT_EQUALS" -> !val.toString().equals(filter.getValue());
                                default -> val.toString().equals(filter.getValue());
                            };
                        } else if (filterValueType == Integer.class) {
                            matches = switch (filter.getComparison()) {
                                case "GREATER_THAN" ->
                                        Integer.parseInt(val.toString()) > Integer.parseInt(filter.getValue().toString());
                                case "GREATER_THAN_OR_EQUAL" ->
                                        Integer.parseInt(val.toString()) >= Integer.parseInt(filter.getValue().toString());
                                case "LESS_THAN" ->
                                        Integer.parseInt(val.toString()) < Integer.parseInt(filter.getValue().toString());
                                case "LESS_THAN_OR_EQUAL" ->
                                        Integer.parseInt(val.toString()) <= Integer.parseInt(filter.getValue().toString());
                                case "NOT_EQUALS" ->
                                        Integer.parseInt(val.toString()) != Integer.parseInt(filter.getValue().toString());
                                default -> val.toString().equals(filter.getValue());
                            };
                        } else if (filterValueType == Boolean.class) {
                            if (filter.getComparison().equals("NOT_EQUALS")) {
                                matches = !val.toString().equals(filter.getValue());
                            } else {
                                matches = val.toString().equals(filter.getValue());
                            }
                        }
                    } catch (Exception e) {
                        break;
                    }
                }

                if (matches) {
                    result.add(json);
                }
            }

            return result;
        } finally {
            locks.get(collection).readLock().lock();
        }
    }

    public Writer OpenWriterCollectionStorage(String collectionName) throws IOException, InvalidCollectionName {
        if (!isValidCollectionName(collectionName)) {
            throw new InvalidCollectionName();
        }
        return Files.newBufferedWriter(Paths.get(pathToStorageDir.toString(), collectionName + ".json"));
    }
//
//    public void WriteLockCollectionStorage(String collection) {
//        if (!writeLocks.containsKey(collection)) {
//            writeLocks.put(collection, new ReentrantLock());
//        }
//        writeLocks.get(collection).lock();
//    }
//
//    public void WriteUnlockCollectionStorage(String collection) {
//        writeLocks.get(collection).writeLock().unlock();
//    }
//
//    public boolean isWriteLocked(String collection) {
//        return writeLocks.get(collection).isWriteLocked();
//    }
//
//    public void ReadLockCollectionStorage(String collection) {
//        if (!writeLocks.containsKey(collection)) {
//            writeLocks.put(collection, new ReentrantLock());
//        }
//        writeLocks.get(collection).readLock().lock();
//    }
//
//    public void ReadUnlockCollectionStorage(String collection) {
//        writeLocks.get(collection).readLock().unlock();
//    }
//
//    public boolean isReadLocked(String collection) {
//        return writeLocks.get(collection).getReadHoldCount() > 0;
//    }

    public synchronized void appendToCollection(String collection, JsonObject newDocument) throws IOException, InvalidCollectionName {
        if (!isValidCollectionName(collection)) {
            throw new InvalidCollectionName();
        }

        if (!locks.containsKey(collection)) {
            locks.put(collection, new ReentrantReadWriteLock());
        }
        locks.get(collection).writeLock().lock();
        System.out.println("got lock");
        try {
            var buf = Files.newBufferedWriter(Paths.get(pathToStorageDir.toString(), collection + ".json"));
            buf.append(newDocument.toString()).append("\n");
            System.out.println("eepy");
            Thread.sleep(1000);
            buf.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            locks.get(collection).writeLock().unlock();
        }
    }
}
