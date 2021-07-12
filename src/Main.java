import network.Database;
import network.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Database database = new Database("C:\\src\\database");
        new Server(8081, database).run();
    }
}
