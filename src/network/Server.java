package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(8081);
        Database database = new Database("C:\\src\\database");
        while (true)
        {
            Socket cs = ss.accept();
            System.out.println("Client connected");
            LoginHandler lh = new LoginHandler(cs , database);
            lh.start();

        }
    }
}
