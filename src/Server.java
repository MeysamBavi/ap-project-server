import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException{
        ServerSocket ss = new ServerSocket(8080);
        while (true)
        {
            Socket cs = ss.accept();
            System.out.println("Client connected");


        }
    }
}
