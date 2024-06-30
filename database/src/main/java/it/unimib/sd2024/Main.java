package it.unimib.sd2024;

import it.unimib.sd2024.Controller.Controller;
import it.unimib.sd2024.Logger.LogLevelType;
import it.unimib.sd2024.Logger.Logger;
import it.unimib.sd2024.Server.Server;
import it.unimib.sd2024.StorageManager.StorageManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Main {
    public static final int DEFAULT_PORT = 3030;
    public static final LogLevelType DEFAULT_LOG_LEVEL = LogLevelType.INFO;

    /**
     * Metodo principale di avvio del database.
     *
     * @param args argomenti passati a riga di comando.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        LogLevelType logLevel = DEFAULT_LOG_LEVEL;
        int port = DEFAULT_PORT;
        String hostname = "0.0.0.0";
        for (String arg : args) {
            switch (arg) {
                case "--help":
                    System.out.println("Usage: java -jar database.jar [--port=PORT] [--log-level=LEVEL]");
                    System.out.println("PORT: port number to listen on, default is 3030");
                    System.out.println("LEVEL: log level, default is INFO");
                    System.exit(0);
                    break;
                case "--port", "-p":
                    System.out.println("Port must be specified");
                    System.exit(1);
                    break;
                case "--log-level", "-ll":
                    System.out.println("Log level must be specified");
                    System.exit(1);
                    break;
                case "--hostname", "-h":
                    System.out.println("Hostname must be specified");
                    System.exit(1);
                    break;
            }
            if (arg.startsWith("--port=") || arg.startsWith("-p=")) {
                try {
                    port = Integer.parseInt(arg.split("=")[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Port must be a number");
                    System.exit(1);
                }
            } else if (arg.startsWith("--log-level=") || arg.startsWith("-ll=")) {
                switch (arg.split("=")[1].toUpperCase()) {
                    case "DEBUG":
                        System.out.println("Log level set to DEBUG");
                        logLevel = LogLevelType.DEBUG;
                        break;
                    case "INFO":
                        System.out.println("Log level set to INFO");
                        logLevel = LogLevelType.INFO;
                        break;
                    case "WARNING":
                        System.out.println("Log level set to WARNING");
                        logLevel = LogLevelType.WARNING;
                        break;
                    case "ERROR":
                        System.out.println("Log level set to ERROR");
                        logLevel = LogLevelType.ERROR;
                        break;
                    default:
                        System.out.println("Log level not recognized, only supported levels are DEBUG, INFO, WARNING, ERROR");
                        System.exit(1);
                }
            } else if (arg.startsWith("--hostname=") || arg.startsWith("-h=")) {
                hostname = arg.split("=")[1];
            }
        }

        SocketAddress address = null;
        try {
            address = new InetSocketAddress(hostname, port);
        } catch (Exception e) {
            System.out.println("Hostname not valid");
            System.exit(1);
        }

        Logger log = new Logger(logLevel);
        StorageManager s = new StorageManager();
        Controller controller = new Controller(log, s);
        Server server = new Server(port, log, address, controller);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.Info("Server is shutting down");
                server.stop();
            }
        });

        try {
            server.start();
        } catch (Exception e) {
            log.Error("Error starting db: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
