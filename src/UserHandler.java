import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Map;

public class UserHandler extends ClientHandler{

    UserHandler(Socket s , Database d)
    {
        super(s,d);
    }

    @Override
    public void run() {
        try {
            String Command = new String(dis.readNBytes(dis.readInt()));
            String[] AnalyzableCommand = Command.split("-");
            Gson gson = new Gson();
            //userID = "U-phonenumber"

            //order-userID-RestaurantID-OrderJson

            //credit-userID-amount
            if (AnalyzableCommand[0].equals("credit"))
            {
                String json = database.getJson(AnalyzableCommand[1]+"-"+AnalyzableCommand[2]);
                Type type = new TypeToken<Map<String,String>>(){}.getType();
                Map<String , String> jsonMap = gson.fromJson(json , type);
                int currentCredit = Integer.parseInt(jsonMap.get("credit")) ;
                currentCredit += Integer.parseInt(AnalyzableCommand[3]);
                jsonMap.put("credit" , String.valueOf(currentCredit));

                String newJson = gson.toJson(jsonMap);
                dos.writeUTF(newJson);
                database.saveChangeByID(AnalyzableCommand[1]+"-"+AnalyzableCommand[2] , newJson);

            }




        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
