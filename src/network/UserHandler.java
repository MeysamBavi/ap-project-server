package network;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static util.JsonUtil.*;
import models.RestaurantPredicate;
import models.Restaurant;
import models.SearchQuery;

public class UserHandler extends ClientHandler {

    private final String[] AnalyzableCommand;

    UserHandler(Socket s , Database d , DataInputStream dis , DataOutputStream dos, String[] AnalyzableCommand)
    {
        super(s,d,dis,dos);
        this.AnalyzableCommand = AnalyzableCommand;
    }

    @Override
    public void run() {
        boolean isDone = false;
        while (!isDone) {
            try {
//                String Command = readString();
//                String[] AnalyzableCommand = Command.split(separator);
                String Response = null;
                switch (AnalyzableCommand[0])
                {
                    case "signup" :
                        Response = signup(AnalyzableCommand);
                        break;
                    case "serialize" :
                        Response = serialize(AnalyzableCommand);
                        break;
                    case "login" :
                        Response = login(AnalyzableCommand);
                        break;
                    case "order" :
                        Response = order(AnalyzableCommand);
                        break;
                    case "credit" :
                        Response = credit(AnalyzableCommand);
                        break;
                    case "address" :
                        Response = address(AnalyzableCommand);
                        break;
                    case "comment" :
                        Response = comment(AnalyzableCommand);
                        break;
                    case "search" :
                        Response = search(AnalyzableCommand);
                        break;
                    case "recommended" :
                        Response = recommended(AnalyzableCommand);
                        break;
                    case  "discount" :
                        Response = discount(AnalyzableCommand);
                        break;
                    case "useDiscount":
                        Response = useDiscount(AnalyzableCommand);
                        break;
                    case "get":
                        Response = get(AnalyzableCommand);
                        break;
                    case "getFood":
                        Response = getFood(AnalyzableCommand);
                        break;
                    case "isPhoneNumberUnique":
                        Response = isPhoneNumberUnique(AnalyzableCommand);
                        break;
                    case "save":
                        Response = save(AnalyzableCommand);
                        break;
                    case "getMenu":
                        Response = getMenu(AnalyzableCommand);
                        break;
                    default:
                        log("Invalid command.");
                        break;
                }
                writeString(Response == null ? "null" : Response);
                endConnection();
                isDone = true;
            } catch (Exception e) {
//                e.printStackTrace();
                isDone = true;
            }
        }
    }
    //signup(*)PhoneNumber(*)Password(*)JSON
    private String signup(String[] ac){
        if (database.isPhoneNumberUnique(ac[1])) {
            log("Signing up for user %s", ac[1]);
            database.createNewObj(ac[1], ac[2], true, ac[3]);
            return String.valueOf(true);
        }
        log("Signup failed with error code 3 for user %s", ac[1]);
        return getError(3);
    }
    //order(*)userID(*)newJson(*)orderID(*)orderJSON(*)restaurantID
    //reorder has no difference with order :)
    private String order(String[] ac)
    {
        log("Requesting for user %s an order with ID %s from restaurant with ID %s", ac[1], ac[3], ac[5]);
        database.saveChangeByID(ac[3], ac[4]);
        database.saveChangeByID(ac[1], true, ac[2]);
        database.addOrderToOwnerFile(ac[5], ac[3]);
        return String.valueOf(true);
    }
    //credit(*)userID(*)newJson
    private String credit(String[] ac)
    {
        log("Saving user account %s (credit)", ac[1]);
        database.saveChangeByID(ac[1],true, ac[2]);
        return String.valueOf(true);
    }
    //address(*)userID(*)newJson
    private String address(String[] ac)
    {
        log("Saving user account %s (address)", ac[1]);
        database.saveChangeByID(ac[1],true, ac[2]);
        return String.valueOf(true);
    }
    //comment(*)UserID(*)UserJSON(*)RestaurantID(*)CommentID(*)CommentJSON
    private String comment(String[] ac)
    {
        log("Adding a new comment with ID %s from user %s for restaurant with ID %s", ac[4], ac[1], ac[3]);
        database.createNewObj(ac[4], ac[5]);
        database.saveChangeByID(ac[1],true, ac[2]);
        database.addCommentToRestaurantFile(ac[3], ac[4]);
        return String.valueOf(true);
    }
    //search(*){restaurant predicate json}
    private String search(String[] ac){
        RestaurantPredicate searchPredicate = jsonToRestaurantPredicate(ac[1]);
        SearchQuery<Restaurant> searchQuery = new SearchQuery<Restaurant>(searchPredicate.generate());
        log("Searching database for restaurant query with hashcode %d", searchQuery.hashCode());
        database.search(searchQuery);
        List<Restaurant> listOfRestaurants = searchQuery.getValue();
        log("Search finished for query with hashcode %d", searchQuery.hashCode());
        return toJson(listOfRestaurants);
    }
    //login(*)PhoneNumber(*)Password
    private String login(String[] ac){
        if (!database.checkPassword(ac[1],ac[2], true)) {
            log("Login failed with error code 2 for user %s", ac[1]);
            return getError(2);
        }
        log("Logging in for user %s", ac[1]);

        var map = jsonToMap(database.getJson(ac[1], true));

        List<String> previousOrdersIDs = (List<String>) map.get("previousOrders");
        final List<Object> previousOrders = new ArrayList<>(previousOrdersIDs.size());
        previousOrdersIDs.forEach((e) -> previousOrders.add(jsonToObject(database.getJson(e))));
        map.put("previousOrders", previousOrders);

        List<String> activeOrdersIDs = (List<String>) map.get("activeOrders");
        final List<Object> activeOrders = new ArrayList<>(activeOrdersIDs.size());
        activeOrdersIDs.forEach((e) -> activeOrders.add(jsonToObject(database.getJson(e))));
        map.put("activeOrders", activeOrders);

        List<Map<String, Object>> cart = (List<Map<String, Object>>) map.get("cart");

        for (var cartItem : cart) {
            cartItem.put("restaurant", jsonToObject(database.getJson((String) cartItem.get("restaurantID"))));
            cartItem.remove("restaurantID");
        }
        return toJson(map);
    }
    //serialize(*)[ObjectType]
    private String serialize(String[] ac) {
        log("Serializing request for object of type %s", ac[1]);
        return database.generateID(ac[1]);
    }

    //recommended(*)count
    private String recommended(String[] ac) {
        int count = Integer.parseInt(ac[1]);
        log("Getting %d recommended restaurants from database", count);
        return database.getAllRestaurants(count);
    }

    //Discount(*)code
    private String discount(String[] ac)
    {
        log("Getting discount with code %s", ac[1]);
        String json = database.getDiscountJson(ac[1]);
        return json == null ? getError(1) : json;
    }

    //useDiscount(*)code
    private String useDiscount(String[] ac) {
        log("Removing discount with code %s", ac[1]);
        database.removeDiscount(ac[1]);
        return String.valueOf(true);
    }

    //get(*)objectID
    private String get(String[] ac) {
        log("Getting object with ID %s", ac[1]);
        String json =  database.getJson(ac[1]);
        return json == null ? getError(1) : json;
    }

    //getFood(*)menuID(*)foodID
    private String getFood(String[] ac) {
        log("Getting food with ID %s and menu ID", ac[2], ac[1]);
        String json =  database.getJson(ac[1], ac[2]);
        return json == null ? getError(1) : json;
    }

    //isPhoneNumberUnique(*)[phoneNumber]
    private String isPhoneNumberUnique(String[] ac) {
        String result = String.valueOf(database.isPhoneNumberUnique(ac[1]));
        log("Checked the uniqueness of %s. The result is %s", ac[1], result);
        return result;
    }

    //save(*)[object id](*){json}
    private String save(String[] ac) {
        log("Saving object with ID %s", ac[1]);
        database.saveChangeByID(ac[1], ac[2]);
        return String.valueOf(true);
    }

    //getMenu(*)[menu ID]
    private String getMenu(String[] ac) {
        log("Getting menu with ID %s", ac[1]);
        String json = database.getJson(ac[1]);
        if (json == null) {
            return getError(1);
        }
        var menuMap = jsonToMap(json);
        for (String category : menuMap.keySet()) {
            if (category.equals("ID")) continue;
            List<String> foodIDs = (List<String>) menuMap.get(category);
            List<Object> foods = new ArrayList<>(foodIDs.size());
            for (String id : foodIDs) {
                String foodJson = database.getJson(ac[1], id);
                if (foodJson == null) continue;
                foods.add(jsonToObject(foodJson));
            }
            menuMap.put(category, foods);
        }
        return toJson(menuMap);
    }
}
