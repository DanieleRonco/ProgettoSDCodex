package it.unimib.sd2024.StorageManager;

import it.unimib.sd2024.Server.Filter;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Semaphore;


public class StorageManager {
    private static final char[] ILLEGAL_CHARACTERS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
    private final Path pathToStorageDir;
    private final HashMap<String, Semaphore> readLocks;
    private final HashMap<String, Semaphore> writeLocks;

    public StorageManager() throws IOException {
        this(Paths.get("collections").toAbsolutePath().toString());
    }

    public StorageManager(String pathToStorageDir) throws IOException {
        this.pathToStorageDir = Files.createDirectories(Paths.get(pathToStorageDir));
        this.readLocks = new HashMap<>();
        this.writeLocks = new HashMap<>();
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

    private void lockRead(String collection) throws InterruptedException, BrokenBarrierException {
//        if there is a write operation wait for it
        if (writeLocks.containsKey(collection)) {
            writeLocks.get(collection).acquire();
        }

        if (!readLocks.containsKey(collection)) {
            var s = new Semaphore(1);
            s.acquire();
            readLocks.put(collection, s);
        }else{
            readLocks.get(collection).acquire();
        }
    }

    private void unlockRead(String collection) {
        readLocks.get(collection).release();
        if (writeLocks.containsKey(collection))
            writeLocks.get(collection).release();
    }

    private void lockWrite(String collection) throws InterruptedException, BrokenBarrierException {
//        if there is a write operation wait for it
        if (writeLocks.containsKey(collection)) {
            System.out.println("there is a write lock, waiting...");
            writeLocks.get(collection).acquire();
            System.out.println("acquired write lock");
        }
        if (readLocks.containsKey(collection)) {
            System.out.println("there is a write lock, waiting...");
            readLocks.get(collection).acquire();
            System.out.println("acquired read lock");
        }

        if (!writeLocks.containsKey(collection)) {
            var s = new Semaphore(1);
            s.acquire();
            writeLocks.put(collection, s);
        }
    }

    private void unlockWrite(String collection) {
        writeLocks.get(collection).release();
        System.out.println("released write lock");
        if (readLocks.containsKey(collection)) {
            readLocks.get(collection).release();
            System.out.println("released read lock");
        }
    }

    public void createNewCollectionStorage(String collectionName) throws IOException, InvalidCollectionName {
        if (!isValidCollectionName(collectionName)) {
            throw new InvalidCollectionName();
        }

        try {
            lockWrite(collectionName);
            Files.createFile(getCollectionPath(collectionName));
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        } finally {
            unlockWrite(collectionName);
        }
    }

    public void deleteCollectionStorage(String collectionName) throws IOException, InvalidCollectionName {
        if (!isValidCollectionName(collectionName)) {
            throw new InvalidCollectionName();
        }

        try {
            lockWrite(collectionName);
            System.out.println("acquired write lock, deleting collection");
            Files.delete(getCollectionPath(collectionName));
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        } finally {
            unlockWrite(collectionName);
            System.out.println("released write lock");
        }
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
        return Files.exists(getCollectionPath(collectionName));
    }

    public Scanner OpenReaderCollectionStorage(String collectionName) throws IOException, InvalidCollectionName {
        if (!isValidCollectionName(collectionName)) {
            throw new InvalidCollectionName();
        }
        return new Scanner(getCollectionPath(collectionName).toFile());
    }

    public List<JsonObject> filterCollection(String collection, List<Filter> filters) throws InvalidCollectionName, IOException {
        if (!isValidCollectionName(collection)) {
            throw new InvalidCollectionName();
        }

        try {
            lockRead(collection);
            System.out.println("acquired read lock, filtering now");
            var result = new ArrayList<JsonObject>();
            var s = new Scanner(getCollectionPath(collection).toFile());

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

            System.out.println("sleeping 2 sec to simulate READ");
            Thread.sleep(2000);
            return result;

        } catch (BrokenBarrierException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            unlockRead(collection);
            System.out.println("released read lock");
        }
    }

    public Writer OpenWriterCollectionStorage(String collectionName) throws IOException, InvalidCollectionName {
        if (!isValidCollectionName(collectionName)) {
            throw new InvalidCollectionName();
        }
        return Files.newBufferedWriter(Paths.get(pathToStorageDir.toString(), collectionName + ".json"));
    }

    private Path getCollectionPath(String collection) {
        return Paths.get(pathToStorageDir.toString(), collection + ".json");
    }

    public synchronized void appendToCollection(String collection, JsonObject newDocument) throws IOException, InvalidCollectionName {
        if (!isValidCollectionName(collection)) {
            throw new InvalidCollectionName();
        }

        FileWriter fr = null;
        try {
            System.out.println("waiting write lock....");
            lockWrite(collection);
            System.out.println("acquired write lock");
            File f = new File(getCollectionPath(collection).toString());
            fr = new FileWriter(f, true);
            fr.write(newDocument.toString() + "\n");
            System.out.println("sleeping 2 sec to simulate write");
            Thread.sleep(2000);
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        } finally {
            if (fr != null)
                fr.close();
            unlockWrite(collection);
        }
    }
}
