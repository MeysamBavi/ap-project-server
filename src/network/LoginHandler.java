package network;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class LoginHandler extends ClientHandler {

    LoginHandler(Socket s , Database d) throws IOException {
        super(s, d);
    }

    @Override
    public void run() {
        try {
            //[user or owner](*){rest of the commands}
            String message = readString();
            String[] analyzableCommand = message.split(separator);
            if (analyzableCommand[0].equals("user")) {
                Thread userHandler = toUserHandler(Arrays.copyOfRange(analyzableCommand, 1, analyzableCommand.length));
                userHandler.start();
            } else if (analyzableCommand[0].equals("owner")) {
                Thread ownerHandler = toOwnerHandler(Arrays.copyOfRange(analyzableCommand, 1, analyzableCommand.length));
                ownerHandler.start();
            } else if (analyzableCommand[0].equals("ping")) {
                writeString(".");
                log("Ping message received.");
                endConnection();
            } else {
                //close streams and terminate connection
                endConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
