import java.io.*;
import java.net.Socket;

public class LoginHandler extends ClientHandler{
    LoginHandler(Socket s , Database d)
    {
        super(s,d);
    }

    @Override
    public void run() {
        try {
            String ClientData = new String(dis.readNBytes(dis.readInt()));
            String[] AnalyzableData = ClientData.split("-");
            //U-phonenumber-JSON String == User signup format
            //W-phobebynumber-JSON String = Restaurant signup format
            if (AnalyzableData[0].equals("U") || AnalyzableData[0].equals("W"))
            {
                if (database.getJson(AnalyzableData[0]+"-"+AnalyzableData[1]) == null) {
                    database.createNewObj(AnalyzableData[0] + "-" + AnalyzableData[1], AnalyzableData[2]);
                    //we can send a boolean here for success indication
                    dos.writeUTF("Sign up Success");
                }else {
                    dos.writeUTF("the phone number already exists in database!");
                }
            }else if (AnalyzableData[0].equals("Login"))
            {
                String JsonUser = database.getJson("U-"+AnalyzableData[1]);
                String JsonOwner = database.getJson("W-"+AnalyzableData[1]);
                //Login-phonenumber-Password
                if (JsonUser != null)
                {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
