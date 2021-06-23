import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class UserHandler extends ClientHandler{

    UserHandler(Socket s , Database d , DataInputStream dis , DataOutputStream dos)
    {
        super(s,d,dis,dos);
    }

    @Override
    public void run() {
        try {
            String Command = readString();
            String[] AnalyzableCommand = Command.split(separator);
            Gson gson = new Gson();
            //Signup(*)Phonenumber(*)Password(*)JSON
            if (AnalyzableCommand[0].equals("Signup"))
            {
                database.createNewObj(AnalyzableCommand[1],AnalyzableCommand[2],true,AnalyzableCommand[3]);
                writeString("User added to the server");
            }
            //Login(*)Phonenumber(*)Password
            else if (AnalyzableCommand[0].equals("Login"))
            {
                if (database.checkPassword(AnalyzableCommand[1] , AnalyzableCommand[2]))
                {
                    writeString(database.getJson(AnalyzableCommand[1],true));
                }else
                {
                    writeString("please create your account first !");
                }
            }
            //order(*)userID(*)newJson

            //credit(*)userID(*)newJson
            else if (AnalyzableCommand[0].equals("Credit"))
            {
                database.saveChangeByID(AnalyzableCommand[1],AnalyzableCommand[2]);
                writeString("Credit added successfully");
            }
            //AddAddress(*)userID(*)newJson
            else if (AnalyzableCommand[0].equals("Address"))
            {
                database.saveChangeByID(AnalyzableCommand[1] , AnalyzableCommand[2]);
                writeString("Address added/edited successfully");
            }
            //Comment(*)UserID(*)UserJSON(*)RestaurantID(*)RestaurantsJSON(*)CommentID(*)CommentJSON
            else if (AnalyzableCommand[0].equals("Comment"))
            {
                database.saveChangeByID(AnalyzableCommand[1] , AnalyzableCommand[2]);
                writeString("Comment added to User Comments");
                database.saveChangeByID(AnalyzableCommand[3] , AnalyzableCommand[4]);
                writeString("Comment added to Restaurant Comments");
                database.saveChangeByID(AnalyzableCommand[5] , AnalyzableCommand[6]);
                writeString("Comment added to Database");
            }
            //Discount(*)Discountcode(*)OrderID(*)OrderJSON
            else if (AnalyzableCommand[0].equals("Discount"))
            {
                if (database.getDiscountJson(AnalyzableCommand[1]) != null)
                {
                    writeString("Discount");
                    database.saveChangeByID(AnalyzableCommand[2],AnalyzableCommand[3]);
                    writeString("discount completed!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
