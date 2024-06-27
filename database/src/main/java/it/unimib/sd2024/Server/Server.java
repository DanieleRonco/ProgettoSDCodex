package it.unimib.sd2024.Server;

import it.unimib.sd2024.Controller.Controller;
import it.unimib.sd2024.Logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int port;
    private Logger log;
    private boolean isRunning;
    private int stopTimeout;
    private Controller controller;

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
            while (isRunning)
                new Handler(server.accept(), log, controller).start();
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
        private Socket client;
        private Logger log;
        private Controller controller;

        public Handler(Socket client, Logger log,Controller controller) {
            this.log = log;
            this.client = client;
                this.controller = controller;
        }

        public void run() {
            log.Debug("Connected to " + client.getRemoteSocketAddress());
            try {
                var out = new PrintWriter(client.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String inputLine;
                StringBuilder sb = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    sb.append(inputLine);
                }
//                while ((inputLine = in.readLine()) != null) {
//                    System.out.println(inputLine);
//                    if (".".equals(inputLine)) {
//                        out.println("bye");
//                        break;
//                    }
//                    out.println(inputLine);
//                }

                out.print(controller.handleRequest(sb.toString()));

                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}
