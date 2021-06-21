import java.io.IOException;
import java.net.Socket;

public class LoginHandler extends ClientHandler{
    LoginHandler(Socket s , Database d)
    {
        super(s,d);
    }

    @Override
    public void run() {
        try {
            String loginData = dis.readUTF();





        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
