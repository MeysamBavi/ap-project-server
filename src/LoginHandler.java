import java.io.*;
import java.net.Socket;
import java.util.Locale;

public class LoginHandler extends ClientHandler {

    LoginHandler(Socket s , Database d) throws IOException {
        super(s, d);
    }

    @Override
    public void run() {
        try {
            String message = readString();
            if (message.equals("user")) {
                Thread userHandler = toUserHandler();
                userHandler.start();
            } else if (message.equals("owner")) {
                Thread ownerHandler = toUserHandler();
                ownerHandler.start();
            } else {
                //close streams and terminate connection
                endConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
