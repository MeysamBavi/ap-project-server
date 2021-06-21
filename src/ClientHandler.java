import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class ClientHandler extends Thread{
    Database database;
    Socket s;
    DataOutputStream dos;
    DataInputStream dis;

    ClientHandler(Socket s , Database database)
    {
        this.s = s;
        this.database = database;
        try {
            this.dos = new DataOutputStream(s.getOutputStream());
            this.dis = new DataInputStream(s.getInputStream());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
