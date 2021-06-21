import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException{
        ServerSocket ss = new ServerSocket(8080);
        Database database = new Database("C:\\Users\\sinatb\\Desktop\\ap_final_project\\database");
        while (true)
        {
            Socket cs = ss.accept();
            System.out.println("Client connected");


        }
    }
}
