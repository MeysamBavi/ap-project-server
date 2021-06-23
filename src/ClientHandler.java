import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Map;

public abstract class ClientHandler extends Thread {
    final Database database;
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    public static String separator = "|*|*|";
    private Gson gson;
    private final Type type = new TypeToken<Map<String, Object>>(){}.getType();

    ClientHandler(Socket socket, Database database) throws IOException {
        this(socket, database, new DataInputStream(socket.getInputStream()), new DataOutputStream(socket.getOutputStream()));
    }

    ClientHandler(Socket socket, Database database, DataInputStream dis, DataOutputStream dos) {
        this.socket = socket;
        this.database = database;
        this.dis = dis;
        this.dos = dos;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    void Serialize(String prefix) throws IOException {
        String serializedCode = database.GenerateID(prefix);
        writeString(serializedCode);
    }

    String readString() throws IOException {
        return new String(dis.readNBytes(dis.readInt()));
    }

    void writeString(String data) throws IOException {
        dos.writeBytes(data);
    }

    UserHandler toUserHandler() {
        return new UserHandler(socket, database, dis, dos);
    }

    OwnerHandler toOwnerHandler() {
        return new OwnerHandler(socket, database, dis, dos);
    }

    void endConnection() throws IOException {
        dis.close();
        dos.close();
        socket.close();
    }

    //1: file not found
    //2: wrong password / username
    public String getError(int errorCode) {
        return "Error " + errorCode;
    }

    public Map<String, Object> jsonToMap(String json) {
        return gson.fromJson(json, type);
    }

    public Object jsonToObject(String json) {
        return gson.fromJson(json, Object.class);
    }

    public String toJson(Object o) {
        return gson.toJson(o);
    }

}
