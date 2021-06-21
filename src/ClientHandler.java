import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread{
    Socket s;
    DataOutputStream dos;
    DataInputStream dis;

    public ClientHandler(Socket ss)
    {
        this.s = ss;
    }


    @Override
    public void run() {
        try {
            String input = dis.readUTF();
            //the rawData[0] should be the command rawData[1] the id and rawData[2] the new value!
            String[] rawData = input.split(" ");





        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
