package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(8081);
        Database database = new Database("C:\\src\\database");
        System.out.println("Server is running");
        while (true)
        {
            Socket cs = ss.accept();
            LoginHandler lh = new LoginHandler(cs , database);
            lh.start();

        }
    }
}
