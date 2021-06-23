import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OwnerHandler extends ClientHandler {

    OwnerHandler(Socket socket, Database database, DataInputStream dis, DataOutputStream dos) {
        super(socket, database, dis, dos);
    }

    //list of commands:
    //serialize [object type], response: id string
    //activeOrders, response: {json object with one field (activeOrders)}
    //deliver [order id] {owner account}
    //editFood [menu id] [food id] {food object}
    //addFood [menu id] [food id] {food object}
    //editRestaurant [restaurant id] {restaurant object}
    //editMenu [menu id] [menu object]
    //addImage [image id] [image file]
    //editImage [image id] [image file]
    //editComment [comment id] {comment object}


    @Override
    public void run() {
        boolean shouldEnd = false;
        while (!shouldEnd) {
            try {
                String command = readString();
                String[] analyzableCommand = command.split(separator);
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
                    case "editMenu":
                        response = editMenu(analyzableCommand);
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
                }
                writeString(response == null ? "null" : response);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    endConnection();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                shouldEnd = true;
            }
        }
    }

    //login [phoneNumber] [password], response: {owner account object (COMPLETE)} or null
    private String login(String[] ac) {
        if (!database.checkPassword(ac[1], ac[2])) {
            return getError(2);
        }
        var map = jsonToMap(database.getJson(ac[1], false));

        String restaurantID = (String) map.get("restaurantID");
        map.put("restaurant", jsonToObject(database.getJson(restaurantID)));
        map.remove("restaurantID");

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

    //signup [phoneNumber] [password] {owner account} {restaurant object}
    private String signUp(String[] ac) {
        return null;
    }

    private String serialize(String[] analyzableCommand) {
        return null;
    }

    private String getActiveOrders(String[] analyzableCommand) {
        return null;
    }

    private String deliver(String[] analyzableCommand) {
        return null;
    }

    private String editFood(String[] analyzableCommand) {
        return null;
    }

    private String addFood(String[] analyzableCommand) {
        return null;
    }

    private String editRestaurant(String[] analyzableCommand) {
        return null;
    }

    private String editMenu(String[] analyzableCommand) {
        return null;
    }

    private String addImage(String[] analyzableCommand) {
        return null;
    }

    private String editImage(String[] analyzableCommand) {
        return null;
    }

    private String editComment(String[] analyzableCommand) {
        return null;
    }
}
