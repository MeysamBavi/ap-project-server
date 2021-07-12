package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    public static final int DEFAULT_NUMBER_OF_THREADS = 3;
    private ServerSocket ss;
    private Database database;
    private ExecutorService threadPool;
    private boolean shouldEnd;

    public Server(int port, Database database, int numberOfThreads) throws IOException {
        this.database = database;
        ss = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(numberOfThreads);
        shouldEnd = false;
    }

    public Server(int port, Database database) throws IOException {
        this(port, database, DEFAULT_NUMBER_OF_THREADS);
    }

    public void run() {
        System.out.println("Server is running");
        while (!shouldEnd)
        {
            try {
                Socket cs = ss.accept();
                LoginHandler lh = new LoginHandler(cs , database);
                threadPool.execute(lh);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
