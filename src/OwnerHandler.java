import java.net.Socket;

public class OwnerHandler extends ClientHandler{

    OwnerHandler(Socket s , Database d)
    {
        super(s,d);
    }

    @Override
    public void run() {

    }
}
