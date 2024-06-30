package it.unimib.sd2024.Server;

import it.unimib.sd2024.Controller.Controller;
import it.unimib.sd2024.Controller.ErrorKindType;
import it.unimib.sd2024.Controller.Response;
import it.unimib.sd2024.Logger.Logger;
import jakarta.json.bind.JsonbBuilder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class Server {
    private final int port;
    private final Logger log;
    private final int stopTimeout;
    private final Controller controller;
    private SocketAddress address;
    private boolean isRunning;

    public Server(int port, Logger log, SocketAddress address, Controller controller) {
        //0 for stopTimeout means it will wait indefinitely
        this(port, log, 0, address, controller);
    }

    public Server(int port, Logger log, int stopTimeout, SocketAddress address, Controller controller) {
        this.port = port;
        this.log = log;
        this.stopTimeout = stopTimeout;
        this.controller = controller;
        this.address = address;
    }

    public void start() throws IOException {
        this.isRunning = true;
        log.Debug("Server started on port " + port);
        var server = new ServerSocket();
        server.bind(address);
        log.Info("Database listening on " + server.getInetAddress() + ":"+server.getLocalPort());

        try {
            while (isRunning) {
                Socket client = server.accept();
                log.Debug("connection accepted");
                new Handler(client, log, controller).start();
            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            server.close();
        }
    }

    public void stop() {
        log.Info("Server stopped on port " + port);
        isRunning = false;
    }

    private static class Handler extends Thread {
        private final Socket client;
        private final Logger log;
        private final Controller controller;

        public Handler(Socket client, Logger log, Controller controller) {
            this.log = log;
            this.client = client;
            this.controller = controller;
        }

        @Override
        public void run() {
            log.Debug("Connected to " + client.getRemoteSocketAddress());
            PrintWriter out = null;
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                JsonReader jsonReader = Json.createReader(in);
                var req = convertToRequest(jsonReader.readObject());

                JsonbBuilder.create().toJson(controller.handleRequest(req), out);
                out.flush();
                out.close();
                in.close();

                client.close();
            } catch (InvalidRequest e) {
                log.Error("invalid request: " + e.getMessage());
                JsonbBuilder.create().toJson(
                        new Response()
                                .setError(true)
                                .setErrorKind(ErrorKindType.INVALID_REQUEST)
                                .setMessage("error handling request"),
                        out);
            } catch (Exception e) {
                log.Error("error handling request: " + e.getMessage());
                if (out == null) {
                    log.Error("error writing response, output stream is null");
                    e.printStackTrace();
                } else {
                    JsonbBuilder.create().toJson(
                            new Response()
                                    .setError(true)
                                    .setErrorKind(ErrorKindType.INTERNAL_ERROR)
                                    .setMessage("error handling request"),
                            out);
                }
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                try {
                    client.close();
                } catch (IOException e) {
                    log.Error("error closing client socket: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        private Request convertToRequest(JsonObject jobj) throws InvalidRequest {
            try {
                var req = new Request();

                try {
                    var operation = jobj.getString("operation");
                    req.setOperation(operation);
                } catch (NullPointerException e) {
                    throw new InvalidRequest("Operation not found, required field");
                }

                try {
                    var version = jobj.getString("version");
                    req.setVersion(version);
                } catch (NullPointerException e) {
                    throw new InvalidRequest("Version not found, required field");
                }

                try {
                    var collection = jobj.getString("collection");
                    req.setCollectionName(collection);
                } catch (NullPointerException e) {
                    req.setCollectionName(null);
                }

                try {
                    var document = jobj.getJsonObject("document");
                    req.setDocument(document);
                } catch (NullPointerException e) {
                    req.setDocument(null);
                }

                try {
                    var JsonFilters = jobj.getJsonArray("filter");
                    var filters = new ArrayList<Filter>();

                    for (var filter : JsonFilters.getValuesAs(JsonObject.class)) {
                        var comparison = filter.getString("comparison");
                        var key = filter.getString("key");
                        var valueType = filter.getString("valueType");
                        Filter f = switch (valueType) {
                            case "String" -> new Filter(comparison, key, filter.getString("value"), String.class);
                            case "Number" ->
                                    new Filter(comparison, key, filter.getJsonNumber("value").bigDecimalValue(), BigDecimal.class);
                            case "Boolean" -> new Filter(comparison, key, filter.getBoolean("value"), Boolean.class);
                            case "Null" -> new Filter(comparison, key, null, null);
                            default -> throw new InvalidRequest("Invalid filter value type: " + valueType);
                        };

                        filters.add(f);
                    }
                    req.setFilters(filters);

                } catch (NullPointerException e) {
                    req.setFilters(null);
                }

                try {
                    var jsonUpdate = jobj.getJsonArray("update");
                    var updates = new ArrayList<UpdateDefinition>();

                    for (var update : jsonUpdate.getValuesAs(JsonObject.class)) {
                        var key = update.getString("key");
                        var valueType = update.getString("valueType");

                        UpdateDefinition u = switch (valueType) {
                            case "String" -> new UpdateDefinition(key, update.getString("value"), String.class);
                            case "Number" ->
                                    new UpdateDefinition(key, update.getJsonNumber("value").bigDecimalValue(), BigDecimal.class);
                            case "Boolean" -> new UpdateDefinition(key, update.getBoolean("value"), Boolean.class);
                            case "Null" -> new UpdateDefinition(key, null, null);
                            default -> throw new InvalidRequest("Invalid update definition value type: " + valueType);
                        };
                        updates.add(u);
                    }
                    req.setUpdates(updates);

                } catch (NullPointerException e) {
                    req.setUpdates(null);
                }

                return req;
            } catch (ClassCastException e) {
                throw new InvalidRequest("at least a field has the wrong type");
            }
        }
    }
}
