package it.unimib.sd2024.Server;

import it.unimib.sd2024.Controller.Controller;
import it.unimib.sd2024.Logger.Logger;
import jakarta.json.bind.JsonbBuilder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private final int port;
    private final Logger log;
    private final int stopTimeout;
    private final Controller controller;
    private boolean isRunning;

    public Server(int port, Logger log, Controller controller) {
        //0 for stopTimeout means it will wait indefinitely
        this(port, log, 0, controller);
    }

    public Server(int port, Logger log, int stopTimeout, Controller controller) {
        this.port = port;
        this.log = log;
        this.stopTimeout = stopTimeout;
        this.controller = controller;
    }

    public void start() throws IOException {
        this.isRunning = true;
        log.Debug("Server started on port " + port);
        var server = new ServerSocket(port);
        log.Info("Database listening at localhost:" + port);

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
            try {
                var out = new PrintWriter(client.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                JsonReader jsonReader = Json.createReader(in);
                var req = convertToRequest(jsonReader.readObject());

                JsonbBuilder.create().toJson(controller.handleRequest(req), out);
                out.flush();

                in.close();
                out.close();
                client.close();
            } catch (Exception e) {
                log.Error("error handling request: " + e.getMessage());
                e.printStackTrace();
            } finally {
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
                        filters.add(new Filter(filter.getString("key"), filter.getString("comparison"), filter.getString("value")));
                    }
                    req.setFilters(filters);

                } catch (NullPointerException e) {
                    req.setFilters(null);
                }

                try {
                    var jsonUpdate = jobj.getJsonArray("update");
                    var update = new ArrayList<UpdateDefinition>();

                    for (var filter : jsonUpdate.getValuesAs(JsonObject.class)) {
                        update.add(new UpdateDefinition(filter.getString("key"), filter.getString("value")));
                    }
                    req.setUpdates(update);

                } catch (NullPointerException e) {
                    req.setUpdates(null);
                }

                return req;
            } catch (ClassCastException e) {
                throw new InvalidRequest("Unable to parse request");
            }
        }
    }
}
