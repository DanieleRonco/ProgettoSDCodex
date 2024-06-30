package it.unimib.sd2024.Database;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Database {
    private final static int DEFAULT_PORT = 3030;
    private final static int DEFAULT_TIMEOUT = 5 * 1000;
    private final InetSocketAddress address;
    private int timeout; //connection timeout in milliseconds

    public Database() throws IOException {
        this(DEFAULT_PORT, InetAddress.getLocalHost(), DEFAULT_TIMEOUT);
    }

    public Database(int port, InetAddress hostname) throws IllegalArgumentException, IOException {
        this(port, hostname, DEFAULT_TIMEOUT);
    }

    public Database(int port, InetAddress hostname, int timeout) throws IllegalArgumentException, IOException {
        this.setTimeout(timeout);
        this.address = new InetSocketAddress(hostname, port);
    }

    public void setTimeout(int timeout) throws IllegalArgumentException {
        if (timeout < 0) {
            throw new IllegalArgumentException("Timeout must be greater than 0");
        }
        this.timeout = timeout;
    }

    public DatabaseResponse ExecuteQuery(Query query) throws InterruptedException, IOException {
        return ExecuteAleatoryQuery(query.build());
    }

    public DatabaseResponse ExecuteAleatoryQuery(String query) throws InterruptedException, IOException {
        System.out.println("getting connection");
        var connection = new Socket();
        connection.connect(this.address, this.timeout);
        System.out.println("connected");
        var databaseInputStream = new DataOutputStream(connection.getOutputStream());
        var databaseOutputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        System.out.println("sending query " + query);
        databaseInputStream.writeBytes(query);
        System.out.println("query sent");
        return readResponse(databaseOutputStream);
    }

    private DatabaseResponse readResponse(BufferedReader buffer) throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = buffer.readLine()) != null) {
            responseBuilder.append(line);
        }
        System.out.println("database response: " + responseBuilder);
        return new DatabaseResponse(responseBuilder.toString());
    }
}