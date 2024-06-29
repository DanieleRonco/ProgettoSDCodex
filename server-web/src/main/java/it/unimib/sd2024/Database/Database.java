package it.unimib.sd2024.Database;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Database {
    private final static int DEFAULT_PORT = 3030;
    private final static int DEFAULT_TIMEOUT = 5 * 1000;
    private final InetSocketAddress address;
    private int timeout; //connection timeout in milliseconds


    public Database() throws IllegalArgumentException, UnknownHostException {
        this(DEFAULT_PORT, InetAddress.getLocalHost(), DEFAULT_TIMEOUT);
    }

    public Database(int port, InetAddress hostname) throws IllegalArgumentException {
        this(port, hostname, DEFAULT_TIMEOUT);
    }

    public Database(int port, InetAddress hostname, int timeout) throws IllegalArgumentException {
        this.setTimeout(timeout);
        this.address = new InetSocketAddress(hostname, port);
    }

    public void setTimeout(int timeout) throws IllegalArgumentException {
        if (timeout < 0) {
            throw new IllegalArgumentException("Timeout must be greater than 0");
        }
        this.timeout = timeout;
    }

    public DatabaseResponse ExecuteQuery(Query query) throws IOException {
        return ExecuteAleatoryQuery(query.build());
    }

    public DatabaseResponse ExecuteAleatoryQuery(String query) throws IOException {
        var socket = new Socket();
        socket.connect(this.address, this.timeout);
        var databaseInputStream = new DataOutputStream(socket.getOutputStream());
        var databaseOutputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("sending query: " + query);
        databaseInputStream.writeBytes(query);
        var response = readResponse(databaseOutputStream);
        socket.close();
        return response;
    }

    private DatabaseResponse readResponse(BufferedReader buffer) throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = buffer.readLine()) != null) {
            responseBuilder.append(line);
        }
        System.out.println("response: " + responseBuilder.toString());
        return new DatabaseResponse(responseBuilder.toString());
    }
}



