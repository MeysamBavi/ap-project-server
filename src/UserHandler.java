import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UserHandler extends ClientHandler{

    UserHandler(Socket s , Database d , DataInputStream dis , DataOutputStream dos)
    {
        super(s,d,dis,dos);
    }

    @Override
    public void run() {
        boolean isDone = false;
        while (!isDone) {
            try {

                String Command = readString();
                String[] AnalyzableCommand = Command.split(separator);
                String Response = null;
                Gson gson = new Gson();
                switch (AnalyzableCommand[0])
                {
                    case "Signup" :
                        Response = signup(AnalyzableCommand);
                        break;
                    case "Serialize" :
                        Response = serialize(AnalyzableCommand);
                        break;
                    case "Login" :
                        Response = login(AnalyzableCommand);
                        break;
                    case "Order" :
                        Response = order(AnalyzableCommand);
                        break;
                    case "Credit" :
                        Response = credit(AnalyzableCommand);
                        break;
                    case "Address" :
                        Response = address(AnalyzableCommand);
                        break;
                    case "Comment" :
                        Response = comment(AnalyzableCommand);
                        break;
                    case "Search" :
                        Response = search(AnalyzableCommand);
                        break;
                    case "Recommended" :
                        Response = recommended(AnalyzableCommand);
                        break;
                    case  "Discount" :
                        Response = discount(AnalyzableCommand);
                        break;
                }
                writeString(Response == null ? "null" : Response);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    endConnection();
                }catch (Exception x)
                {
                    x.printStackTrace();
                }
                isDone = true;
            }
        }
    }
    //Signup(*)Phonenumber(*)Password(*)JSON
    private String signup(String[] ac){
        if (database.isPhoneNumberUnique(ac[1])) {
            database.createNewObj(ac[1], ac[2], true, ac[3]);
            return String.valueOf(true);
        }

        return getError(3);
    }
    //order(*)userID(*)newJson(*)orderID(*)orderJSON(*)restaurantID
    //reorder has no difference with order :)
    private String order(String[] ac)
    {
        database.saveChangeByID(ac[3], ac[4]);
        database.saveChangeByID(ac[1], true, ac[2]);
        database.addOrderToOwnerFile(ac[5], ac[3]);
        return String.valueOf(true);
    }
    //credit(*)userID(*)newJson
    private String credit(String[] ac)
    {
        database.saveChangeByID(ac[1],true, ac[2]);
        return String.valueOf(true);
    }
    //AddAddress(*)userID(*)newJson
    private String address(String[] ac)
    {
        database.saveChangeByID(ac[1],true, ac[2]);
        return String.valueOf(true);
    }
    //Comment(*)UserID(*)UserJSON(*)RestaurantID(*)CommentID(*)CommentJSON
    private String comment(String[] ac)
    {
        database.createNewObj(ac[4], ac[5]);
        database.saveChangeByID(ac[1],true, ac[2]);
        database.addCommentToRestaurantFile(ac[3], ac[4]);
        return String.valueOf(true);
    }
    //Search(*)name
    private String search(String[] ac){
        RestaurantPredicate searchPredicate = new RestaurantPredicate(ac[1]);
        SearchQuery<Restaurant> searchQuery = new SearchQuery<Restaurant>(searchPredicate.generate());
        database.search(searchQuery);
        Restaurant[] listOfRestaurants = searchQuery.getValue();
        return toJson(listOfRestaurants);
    }
    //Login(*)Phonenumber(*)Password
    private String login(String[] ac){
        if (!database.checkPassword(ac[1],ac[2]))
        {
            return getError(2);
        }
        var map = jsonToMap(database.getJson(ac[1], true));

        List<String> previousOrdersIDs = (List<String>) map.get("previousOrders");
        final List<Object> previousOrders = new ArrayList<>(previousOrdersIDs.size());
        previousOrdersIDs.forEach((e) -> previousOrders.add(jsonToObject(database.getJson(e))));
        map.put("previousOrders", previousOrders);

        List<String> activeOrdersIDs = (List<String>) jsonToMap(database.getActiveOrdersJson(ac[1])).get("activeOrders");
        final List<Object> activeOrders = new ArrayList<>(activeOrdersIDs.size());
        activeOrdersIDs.forEach((e) -> activeOrders.add(jsonToObject(database.getJson(e))));
        map.put("activeOrders", activeOrders);

        return toJson(map);
    }
    //ObjectType
    private String serialize(String[] ac){
        return database.generateID(ac[1]);
    }
    private String recommended(String[] ac)
    {
        // TODO
        return "salam";
    }
    //Discount(*)Discountcode(*)OrderID(*)OrderJSON
    private String discount(String[] ac)
    {
        if (database.getDiscountJson(ac[1]) != null) {
            database.saveChangeByID(ac[2], ac[3]);
            return String.valueOf(true);
        }
        return getError(1);
    }
}
