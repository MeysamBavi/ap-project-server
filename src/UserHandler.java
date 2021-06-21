import java.net.Socket;

public class UserHandler extends ClientHandler{

    UserHandler(Socket s , Database d)
    {
        super(s,d);
    }

    @Override
    public void run() {

    }
}
