package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import static util.JsonUtil.*;

public class OwnerHandler extends ClientHandler {

    private final String[] analyzableCommand;

    OwnerHandler(Socket socket, Database database, DataInputStream dis, DataOutputStream dos, String[] analyzableCommand) {
        super(socket, database, dis, dos);
        this.analyzableCommand = analyzableCommand;
    }

    @Override
    public void run() {
        boolean shouldEnd = false;
        while (!shouldEnd) {
            try {
//                String command = readString();
//                String[] analyzableCommand = command.split(separator);
                String response = null;
                switch (analyzableCommand[0]) {
                    case "login":
                        response = login(analyzableCommand);
                        break;
                    case "signup":
                        response = signUp(analyzableCommand);
                        break;
                    case "serialize":
                        response = serialize(analyzableCommand);
                        break;
                    case "activeOrders":
                        response = getActiveOrders(analyzableCommand);
                        break;
                    case "deliver":
                        response = deliver(analyzableCommand);
                        break;
                    case "editFood":
                        response = editFood(analyzableCommand);
                        break;
                    case "addFood":
                        response = addFood(analyzableCommand);
                        break;
                    case "editRestaurant":
                        response = editRestaurant(analyzableCommand);
                        break;
                    case "addImage":
                        response = addImage(analyzableCommand);
                        break;
                    case "editImage":
                        response = editImage(analyzableCommand);
                        break;
                    case "editComment":
                        response = editComment(analyzableCommand);
                        break;
                    case "get":
                        response = get(analyzableCommand);
                        break;
                    case "isPhoneNumberUnique":
                        response = isPhoneNumberUnique(analyzableCommand);
                        break;
                    case "getMenu":
                        response = getMenu(analyzableCommand);
                        break;
                    case "removeFood":
                        response = removeFood(analyzableCommand);
                        break;
                    default:
                        log("Invalid command.");
                        break;
                }
                writeString(response == null ? "null" : response);
                endConnection();
                shouldEnd = true;
            } catch (IOException e) {
//                e.printStackTrace();
                shouldEnd = true;
            }
        }
    }

    //login [phoneNumber] [password], response: {owner account object (COMPLETE)} or null
    private String login(String[] ac) {
        if (!database.checkPassword(ac[1], ac[2], false)) {
            log("Login failed with error code 2 for owner %s", ac[1]);
            return getError(2);
        }
        log("Logging in for owner %s", ac[1]);

        var map = jsonToMap(database.getJson(ac[1], false));

        String restaurantID = (String) map.get("restaurantID");
        map.put("restaurant", jsonToObject(database.getJson(restaurantID)));
        map.remove("restaurantID");

        List<String> previousOrdersIDs = (List<String>) map.get("previousOrders");
        final List<Object> previousOrders = new ArrayList<>(previousOrdersIDs.size());
        previousOrdersIDs.forEach((e) -> previousOrders.add(jsonToObject(database.getJson(e))));
        map.put("previousOrders", previousOrders);

        List<String> activeOrdersIDs = (List<String>) jsonToObject(database.getActiveOrdersJson(ac[1]));
        final List<Object> activeOrders = new ArrayList<>(activeOrdersIDs.size());
        activeOrdersIDs.forEach((e) -> activeOrders.add(jsonToObject(database.getJson(e))));
        map.put("activeOrders", activeOrders);

        return toJson(map);
    }

    //signup [phoneNumber] [password] {owner account} [restaurantID] {restaurant object} [menu ID] {menu object}
    private String signUp(String[] ac) {
        if (!database.isPhoneNumberUnique(ac[1])) {
            log("Signup failed with error code 3 for owner %s", ac[1]);
            return getError(3);
        }
        log("Signing up for owner %s with restaurant ID %s and menu ID %s", ac[1], ac[4], ac[6]);

        database.createNewObj(ac[1], ac[2], false, ac[3]);
        database.createNewObj(ac[4], ac[5]);
        database.addOwnerOf(ac[4], ac[1]);
        database.createNewObj(ac[6], ac[7]);
        return String.valueOf(true);
    }

    //serialize [object type], response: id string
    private String serialize(String[] ac) {
        log("Serializing request for object of type %s", ac[1]);
        return database.generateID(ac[1]);
    }

    //activeOrders [phoneNumber], response: {json object with one field (activeOrders)}
    private String getActiveOrders(String[] ac) {
        log("Sending active orders for owner %s", ac[1]);
        List<String> activeOrdersIDs = (List<String>) jsonToObject(database.getActiveOrdersJson(ac[1]));
        final List<Object> activeOrders = new ArrayList<>(activeOrdersIDs.size());
        activeOrdersIDs.forEach((e) -> activeOrders.add(jsonToObject(database.getJson(e))));
        return toJson(activeOrders);
    }

    //deliver [order id] {order object} [phoneNumber] {owner account} {active orders}
    private String deliver(String[] ac) {
        log("Delivering order with ID %s for owner %s", ac[1], ac[3]);
        database.saveChangeByID(ac[1], ac[2]);
        database.saveChangeByID(ac[3], false, ac[4]);
        database.saveActiveOrders(ac[3], ac[5]);
        return String.valueOf(true);
    }

    //editFood [menu id] [food id] {food object}
    private String editFood(String[] ac) {
        log("Editing food with ID %s and menu ID %s", ac[2], ac[1]);
        database.saveChangeByID(ac[1], ac[2], ac[3]);
        return String.valueOf(true);
    }

    //addFood [menu id] {menu object} [food id] {food object}
    private String addFood(String[] ac) {
        log("Adding food with ID %s to menu with ID %s", ac[2], ac[1]);
        database.createNewObj(ac[1], ac[3], ac[4]);
        database.saveChangeByID(ac[1], ac[2]);
        return String.valueOf(true);
    }

    //editRestaurant [restaurant id] {restaurant object}
    private String editRestaurant(String[] ac) {
        log("Editing restaurant with ID %s", ac[1]);
        database.saveChangeByID(ac[1], ac[2]);
        return String.valueOf(true);
    }

    //addImage [image id] [image file]
    private String addImage(String[] analyzableCommand) {
//        TODO
        log("Unimplemented command (addImage)");
        return null;
    }

    //editImage [image id] [image file]
    private String editImage(String[] analyzableCommand) {
//        TODO
        log("Unimplemented command (editImage)");
        return null;
    }

    //editComment [comment id] {comment object}
    private String editComment(String[] ac) {
        log("Editing comment with ID %s", ac[1]);
        database.saveChangeByID(ac[1], ac[2]);
        return String.valueOf(true);
    }

    //get [object id]
    private String get(String[] ac) {
        log("Getting object with ID %s", ac[1]);
        String json =  database.getJson(ac[1]);
        return json == null ? getError(1) : json;
    }

    //isPhoneNumberUnique [phoneNumber]
    private String isPhoneNumberUnique(String[] ac) {
        String result = String.valueOf(database.isPhoneNumberUnique(ac[1]));
        log("Checked the uniqueness of %s. The result is %s", ac[1], result);
        return result;
    }

    //getMenu [menu ID]
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

    //removeFood [menuID] [foodID] {menu object}
    private String removeFood(String[] ac) {
        log("Removing food with ID %s from menu with ID %s", ac[2], ac[1]);
        database.removeFood(ac[1], ac[2]);
        database.saveChangeByID(ac[1], ac[3]);
        return String.valueOf(true);
    }
}
