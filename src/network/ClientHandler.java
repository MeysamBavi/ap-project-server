package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class ClientHandler extends Thread {
    final Database database;
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    public static String separator = "\\|\\*\\|\\*\\|";

    ClientHandler(Socket socket, Database database) throws IOException {
        this(socket, database, new DataInputStream(socket.getInputStream()), new DataOutputStream(socket.getOutputStream()));
    }

    ClientHandler(Socket socket, Database database, DataInputStream dis, DataOutputStream dos) {
        this.socket = socket;
        this.database = database;
        this.dis = dis;
        this.dos = dos;
    }

    String readString() throws IOException {
        return new String(dis.readNBytes(dis.readInt()));
    }

    void writeString(String data) throws IOException {
        dos.writeBytes(data);
    }

    UserHandler toUserHandler(String[] ac) {
        return new UserHandler(socket, database, dis, dos, ac);
    }

    OwnerHandler toOwnerHandler(String[] ac) {
        return new OwnerHandler(socket, database, dis, dos, ac);
    }

    void endConnection() throws IOException {
        dos.close();
        dis.close();
        socket.close();
    }

    //1: file not found
    //2: wrong password / username
    //3: duplicate phonenumber
    public String getError(int errorCode) {
        return "Error " + errorCode;
    }

}
