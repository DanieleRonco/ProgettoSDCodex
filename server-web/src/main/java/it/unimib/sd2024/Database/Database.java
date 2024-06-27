package it.unimib.sd2024.Database;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Database {
    private final static int DEFAULT_PORT = 3030;
    private final static int DEFAULT_TIMEOUT = 5 * 1000;
    private final InetSocketAddress address;
    private final DatabaseConnectionPool connectionPool;
    private int timeout; //connection timeout in milliseconds


    public Database() throws IOException {
        this(DEFAULT_PORT, InetAddress.getLocalHost(), DEFAULT_TIMEOUT);
    }

    public Database(int port, InetAddress hostname) throws IllegalArgumentException, IOException {
        this(port, hostname, DEFAULT_TIMEOUT);
    }

    public Database(int port, InetAddress hostname, int timeout) throws IllegalArgumentException, IOException {
        this(port, hostname, timeout, DatabaseConnectionPool.DEFAULT_POOL_SIZE);
    }

    public Database(int port, InetAddress hostname, int timeout, int poolsize) throws IllegalArgumentException, IOException {
        this.setTimeout(timeout);
        this.address = new InetSocketAddress(hostname, port);
        this.connectionPool = new DatabaseConnectionPool(this.address, poolsize);
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
        var socket = this.connectionPool.getConnection();
        var databaseInputStream = new DataOutputStream(socket.getOutputStream());
        var databaseOutputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        databaseInputStream.writeBytes(query);
        var response = readResponse(databaseOutputStream);
        this.connectionPool.releaseConnection(socket);
        return response;
    }

    private DatabaseResponse readResponse(BufferedReader buffer) throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = buffer.readLine()) != null) {
            responseBuilder.append(line);
        }
        return new DatabaseResponse(responseBuilder.toString());
    }
}



