package it.unimib.sd2024.StorageManager;

import it.unimib.sd2024.Server.Filter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class StorageManager {
    private static final char[] ILLEGAL_CHARACTERS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
    private final HashMap<String, ReentrantReadWriteLock> locks = new HashMap<>();
    private Path pathToStorageDir = Paths.get("collections").toAbsolutePath();

    public StorageManager() throws IOException {
        this(Paths.get("collections").toAbsolutePath().toString());
    }

    public StorageManager(String pathToStorageDir) throws IOException {
        this.pathToStorageDir = Files.createDirectories(Paths.get(pathToStorageDir));
    }

    private Path getCollectionPath(String collection) {
        return Paths.get(pathToStorageDir.toString(), collection + ".json");
    }

    private boolean isValidCollectionName(String collection) {
        if (collection.isEmpty() || collection.length() > 100) {
            return false;
        }
        for (char c : ILLEGAL_CHARACTERS) {
            if (collection.indexOf(c) != -1) {
                return false;
            }
        }
        return true;
    }

    private int countLinesInCollection(String collection) throws IOException, InvalidCollectionName {
        if (!isValidCollectionName(collection)) {
            throw new InvalidCollectionName();
        }

        locks.putIfAbsent(collection, new ReentrantReadWriteLock());

        try {
            locks.get(collection).readLock().lock();
            var s = new Scanner(getCollectionPath(collection).toFile());
            int count = 0;
            while (s.hasNext()) {
                s.nextLine();
                count++;
            }
            return count--; //last line is empty
        } finally {
            locks.get(collection).readLock().unlock();
        }
    }

    public void createCollectionStorage(String collection) throws IOException, InvalidCollectionName {
        if (!isValidCollectionName(collection)) {
            throw new InvalidCollectionName();
        }

        locks.putIfAbsent(collection, new ReentrantReadWriteLock());

        try {
            locks.get(collection).writeLock().lock();
            Files.createFile(getCollectionPath(collection));
        } finally {
            locks.get(collection).writeLock().unlock();
        }
    }

    public int deleteCollectionStorage(String collection) throws IOException, InvalidCollectionName {
        if (!isValidCollectionName(collection)) {
            throw new InvalidCollectionName();
        }

        locks.putIfAbsent(collection, new ReentrantReadWriteLock());

        try {
            locks.get(collection).writeLock().lock();
            int documentCount = countLinesInCollection(collection);
            Files.delete(getCollectionPath(collection));
            return documentCount;
        } finally {
            locks.get(collection).writeLock().unlock();
        }
    }

    public boolean doesCollectionExist(String collection) throws InvalidCollectionName {
        if (!isValidCollectionName(collection)) {
            throw new InvalidCollectionName();
        }
        return Files.exists(getCollectionPath(collection));
    }

    private boolean doesFilterMatch(JsonObject document, String key, Object value, Type valueType, String comparison) {
        try {
            var documentValueForKey = document.get(key);
            if (documentValueForKey == null || documentValueForKey.getValueType() == JsonValue.ValueType.NULL) {
                if (valueType == null) {
                    return comparison.equals("EQUALS");
                }
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }

        Object documentValueForKey = null;
        Type documentType = null;
        switch (document.get(key).getValueType()) {
            case STRING:
                documentValueForKey = document.getString(key);
                documentType = String.class;
                break;
            case NUMBER:
                documentValueForKey = document.getJsonNumber(key).bigDecimalValue();
                documentType = BigDecimal.class;
                break;
            case TRUE:
                documentValueForKey = true;
                documentType = Boolean.class;
                break;
            case FALSE:
                documentValueForKey = false;
                documentType = Boolean.class;
                break;
            default:
//                todo: check this case better
//                for now we are unable to handle
//                types that are not string, number, boolean or null
                return false;
        }

        if (valueType != documentType) {
            return false;
        }

        if (valueType == String.class) {
            final int compareTo = documentValueForKey.toString().compareTo(value.toString());
            final boolean equals = documentValueForKey.toString().equals(value.toString());
            return switch (comparison) {
                case "GREATER_THAN" -> compareTo > 0;
                case "GREATER_THAN_OR_EQUAL" -> compareTo >= 0;
                case "LESS_THAN" -> compareTo < 0;
                case "LESS_THAN_OR_EQUAL" -> compareTo <= 0;
                case "NOT_EQUALS" -> !equals;
                default -> equals;
            };
        } else if (valueType == BigDecimal.class) {
            final int compareTo = new BigDecimal(documentValueForKey.toString()).compareTo(new BigDecimal(value.toString()));
            return switch (comparison) {
                case "GREATER_THAN" -> compareTo > 0;
                case "GREATER_THAN_OR_EQUAL" -> compareTo >= 0;
                case "LESS_THAN" -> compareTo < 0;
                case "LESS_THAN_OR_EQUAL" -> compareTo <= 0;
                case "NOT_EQUALS" -> compareTo != 0;
                default -> compareTo == 0;
            };
        } else {
            if (comparison.equals("NOT_EQUALS")) {
                return !documentValueForKey.toString().equals(value);
            } else {
                return documentValueForKey.toString().equals(value);
            }
        }
    }

    private boolean traverseObject(JsonObject document, List<String> keys, Object filterValue, Type filterValueType, String comparison) {
        var currentKey = keys.getFirst();
        keys.removeFirst();
        if (document == null) {
            return false;
        }

        if (document.get(currentKey) == null)
            return doesFilterMatch(document, currentKey, filterValue, filterValueType, comparison);
        var valueType = document.get(currentKey).getValueType();

        if (keys.isEmpty()) {
            return doesFilterMatch(document, currentKey, filterValue, filterValueType, comparison);
        } else {
            if (valueType != JsonValue.ValueType.OBJECT)
                return false;
            return traverseObject(document.getJsonObject(currentKey), keys, filterValue, filterValueType, comparison);
        }
    }

    public List<JsonObject> filterDocuments(String collection, java.util.List<Filter> filters) throws InvalidCollectionName, IOException {
        if (!isValidCollectionName(collection)) {
            throw new InvalidCollectionName();
        }

        locks.putIfAbsent(collection, new ReentrantReadWriteLock());

        try {
            locks.get(collection).readLock().lock();
            var result = new ArrayList<JsonObject>();
            var s = new Scanner(getCollectionPath(collection).toFile());

            while (s.hasNext()) {
                var line = s.nextLine();
                line = line.trim();
                if (line.isEmpty() || line.equals("\n")) {
                    continue;
                }
                var document = Json.createReader(new StringReader(line)).readObject();

                boolean matches = false;
                if (filters == null) {
                    result.add(document);
                    continue;
                }

                for (var filter : filters) {
                    var filterKey = new ArrayList<>(Arrays.asList(filter.getKey().split("\\.")));
                    var filterComparison = filter.getComparison();
                    var filterValue = filter.getValue();
                    var filterValueType = filter.getValueType();

                    matches = traverseObject(document, filterKey, filterValue, filterValueType, filterComparison);
                    if (!matches) {
                        break;
                    }
                }

                if (matches) {
                    result.add(document);
                }
            }
            return result;
        } finally {
            locks.get(collection).readLock().unlock();
        }
    }

    public void appendDocumentToCollection(String collection, JsonObject newDocument) throws IOException, InvalidCollectionName {
        if (!isValidCollectionName(collection)) {
            throw new InvalidCollectionName();
        }

        locks.putIfAbsent(collection, new ReentrantReadWriteLock());

        FileWriter fr = null;
        try {
            locks.get(collection).writeLock().lock();
            File f = new File(getCollectionPath(collection).toString());
            fr = new FileWriter(f, true);
            fr.write(newDocument.toString() + "\n");
        } finally {
            if (fr != null)
                fr.close();
            locks.get(collection).writeLock().unlock();
        }
    }

    public void deleteDocuments(String collection, List<String> documentIDs) throws InvalidCollectionName, IOException {
        if (!isValidCollectionName(collection)) {
            throw new InvalidCollectionName();
        }

        locks.putIfAbsent(collection, new ReentrantReadWriteLock());

        try {
            locks.get(collection).writeLock().lock();
            var s = new Scanner(getCollectionPath(collection).toFile());
            var tempFile = File.createTempFile("temp", ".json");
            var fw = new FileWriter(tempFile);
            while (s.hasNext()) {
                var line = s.nextLine();
                var json = Json.createReader(new StringReader(line)).readObject();
                var currentDocumentID = json.getString("_id");
                if (!documentIDs.contains(currentDocumentID)) {
                    fw.write(json + "\n");
                } else {
                    documentIDs.remove(currentDocumentID);
                }
            }
            fw.close();
            s.close();
            Files.delete(getCollectionPath(collection));
            Files.move(tempFile.toPath(), getCollectionPath(collection));
        } finally {
            locks.get(collection).writeLock().unlock();
        }
    }
}

/*
* this was the old filter logic, it was able to not load all the document in memory
* but has too many limitations (no search in nested objects)
*
    public List<JsonObject> filterDocuments(String collection, List<Filter> filters) throws InvalidCollectionName, IOException {
        if (!isValidCollectionName(collection)) {
            throw new InvalidCollectionName();
        }

        locks.putIfAbsent(collection, new ReentrantReadWriteLock());

        try {
            locks.get(collection).readLock().lock();
            var result = new ArrayList<JsonObject>();
            var s = new Scanner(getCollectionPath(collection).toFile());

            while (s.hasNext()) {
                var line = s.nextLine();
                line = line.trim();
                if (line.isEmpty() || line.equals("\n")) {
                    continue;
                }
                var document = Json.createReader(new StringReader(line)).readObject();
                var parser = Json.createParser(new StringReader(line));
                boolean matches = false;
                boolean stopParsing = false;
                if (filters == null) {
                    result.add(document);
                    continue;
                }

                String key = "";
                Object value = null;
                Type valueType = null;
                while (parser.hasNext() && !stopParsing) {
                    final JsonParser.Event event = parser.next();
                    switch (event) {
                        case KEY_NAME:
                            key = parser.getString();
                            break;
                        case VALUE_STRING:
                            value = parser.getString();
                            valueType = String.class;
                            break;
                        case VALUE_NUMBER:
                            value = parser.getBigDecimal();
                            valueType = BigDecimal.class;
                            break;
                        case VALUE_TRUE:
                            value = true;
                            valueType = Boolean.class;
                            break;
                        case VALUE_FALSE:
                            value = false;
                            valueType = Boolean.class;
                            break;
                        case START_OBJECT:

                    }


                    if (value == null) {
//                        todo: check what to do when value is null
                        continue;
                    }

                    for (var filter : filters) {
                        var filterKey = filter.getKey();
                        var filterComparison = filter.getComparison();
                        var filterValue = filter.getValue();
                        var filterValueType = filter.getValueType();

                        System.out.println("document key: " + key + " filter key: " + filterKey);
                        if (key.equals(filterKey)) {
//                            System.out.println("checking filter: " + filterKey + " " + filterComparison + " " + filterValue + "[" + filterValueType + "]");
//                            System.out.println("document " + key + " : " + value);
                            if (valueType != filterValueType) {
//                                System.out.println("type of value: " + valueType + " type of filter value: " + filterValueType);
//                                System.out.println("value type does not match");
                                matches = false;
                                stopParsing = true;
                                break;
                            }
                            if (filterValueType == String.class) {
                                matches = switch (filterComparison) {
                                    case "GREATER_THAN" -> value.toString().compareTo(filterValue.toString()) > 0;
                                    case "GREATER_THAN_OR_EQUAL" ->
                                            value.toString().compareTo(filterValue.toString()) >= 0;
                                    case "LESS_THAN" -> value.toString().compareTo(filterValue.toString()) < 0;
                                    case "LESS_THAN_OR_EQUAL" ->
                                            value.toString().compareTo(filterValue.toString()) <= 0;
                                    case "NOT_EQUALS" -> !value.toString().equals(filterValue.toString());
                                    default -> value.toString().equals(filterValue.toString());
                                };
                            } else if (filterValueType == BigDecimal.class) {
                                matches = switch (filter.getComparison()) {
                                    case "GREATER_THAN" ->
                                            new BigDecimal(value.toString()).compareTo(new BigDecimal(filter.getValue().toString())) > 0;
                                    case "GREATER_THAN_OR_EQUAL" ->
                                            new BigDecimal(value.toString()).compareTo(new BigDecimal(filter.getValue().toString())) >= 0;
                                    case "LESS_THAN" ->
                                            new BigDecimal(value.toString()).compareTo(new BigDecimal(filter.getValue().toString())) < 0;
                                    case "LESS_THAN_OR_EQUAL" ->
                                            new BigDecimal(value.toString()).compareTo(new BigDecimal(filter.getValue().toString())) <= 0;
                                    case "NOT_EQUALS" ->
                                            new BigDecimal(value.toString()).compareTo(new BigDecimal(filter.getValue().toString())) != 0;
                                    default ->
                                            new BigDecimal(value.toString()).compareTo(new BigDecimal(filter.getValue().toString())) == 0;
                                };
                            } else if (filterValueType == Boolean.class) {
                                if (filter.getComparison().equals("NOT_EQUALS")) {
                                    matches = !value.toString().equals(filter.getValue());
                                } else {
                                    matches = value.toString().equals(filter.getValue());
                                }
                            }

//                          in the V1 of the protocol the only supported
//                          filter concatenation means an AND operation
                            if (!matches) {
                                stopParsing = true;
                                break;
                            }
                        }
                    }
                    value = null;
                }

                if (matches) {
//                    System.out.println(document.getString("_id") + " matches");
                    result.add(document);
                }
                parser.close();
            }
            return result;
        } finally {
            locks.get(collection).readLock().unlock();
        }
    }

* */