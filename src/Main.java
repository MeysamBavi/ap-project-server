import network.Database;
import network.Server;

import java.io.IOException;

public class Main {

    // args: [port, database directory, number of threads (optional)]
    public static void main(String[] args) throws IOException {

        int port = Integer.parseInt(args[0]);
        int threadCount = Server.DEFAULT_NUMBER_OF_THREADS;

        try {
            threadCount = Integer.parseInt(args[2]);
        } catch (Exception ignored) {}

        Database database = new Database(args[1]);

        new Server(port, database, threadCount).run();
    }
}
