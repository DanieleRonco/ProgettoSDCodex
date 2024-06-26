package it.unimib.sd2024.Server;

import it.unimib.sd2024.Controller.Controller;
import it.unimib.sd2024.Logger.Logger;
import it.unimib.sd2024.Main;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private final int port;
    private Logger log;
    private boolean isRunning;
    private int stopTimeout;
    private Controller controller;

    public Server(int port, Logger log) {
        //0 for stopTimeout means it will wait indefinitely
        this(port, log, 0);
    }

    public Server(int port, Logger log, int stopTimeout) {
        this.port = port;
        this.log = log;
        this.stopTimeout = stopTimeout;
    }

    public void start() throws IOException {
        this.isRunning=true;
        log.Debug("Server started on port " + port);
        var server = new ServerSocket(port);
        log.Info("Database listening at localhost:" + port);

        try {
            while (isRunning)
                new Main.Handler(server.accept(), log).start();
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

}
