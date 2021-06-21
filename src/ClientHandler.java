import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class ClientHandler extends Thread{
    Socket s;
    DataOutputStream dos;
    DataInputStream dis;

    ClientHandler(Socket s)
    {
        this.s = s;
    }

}
