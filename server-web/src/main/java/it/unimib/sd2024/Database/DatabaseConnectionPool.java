package it.unimib.sd2024.Database;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DatabaseConnectionPool {
    public static final int DEFAULT_POOL_SIZE = 10;
    private final BlockingQueue<Socket> availableConnections;

    public DatabaseConnectionPool(InetSocketAddress address, int poolSize) throws IOException, IllegalArgumentException {
        if (poolSize <= 0) {
            throw new IllegalArgumentException("Pool size must be greater than 0");
        }
        availableConnections = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            Socket socket = new Socket(address.getHostName(), address.getPort());
            availableConnections.add(socket);
        }
    }

    public DatabaseConnectionPool(InetSocketAddress address) throws IOException {
        this(address, DEFAULT_POOL_SIZE);
    }

    public Socket getConnection() throws InterruptedException {
        return availableConnections.take();
    }

    public void releaseConnection(Socket socket) {
        availableConnections.offer(socket);
    }
}
