import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Database {
    private File mainDirectory;
    private File ordersDirectory;
    private File restaurantsDirectory;
    private File menusDirectory;
    private File commentsDirectory;
    private File userAccountsDirectory;
    private File ownerAccountsDirectory;
    private File discountsDirectory;
    private File loginDataFile;
    private Map<String, String> loginData;
    private File ownerOfFile;
    private Map<String, String> ownerOf; //restaurantID to phoneNumber of owner
    private final Map<String, Semaphore> locks;
    private final int MAX_PERMITS = 10;
    private Gson gson = new Gson();
    private Type mapType = new TypeToken<Map<String, Object>>(){}.getType();

    public Database(String directory) {
        File dir = new File(directory);
        dir.mkdir();
        if (!dir.isDirectory()) {
            throw new RuntimeException("Not a directory.");
        }
        mainDirectory = dir;
        this.locks = new ConcurrentHashMap<>();
        createSubDirectories();
        load(); // MUST be after createSubDirectories
    }

    // creates sub directories. if they already exist, nothing happens.
    private void createSubDirectories() {
        ordersDirectory = new File(mainDirectory.getAbsolutePath() + File.separator + "orders");
        ordersDirectory.mkdir();
        restaurantsDirectory = new File(mainDirectory.getAbsolutePath() + File.separator + "restaurants");
        restaurantsDirectory.mkdir();
        menusDirectory = new File(mainDirectory.getAbsolutePath() + File.separator + "menus");
        menusDirectory.mkdir();
        commentsDirectory = new File(mainDirectory.getAbsolutePath() + File.separator + "comments");
        commentsDirectory.mkdir();
        userAccountsDirectory = new File(mainDirectory.getAbsolutePath() + File.separator + "user-accounts");
        userAccountsDirectory.mkdir();
        ownerAccountsDirectory = new File(mainDirectory.getAbsolutePath() + File.separator + "owner-accounts");
        ownerAccountsDirectory.mkdir();
        discountsDirectory = new File(mainDirectory.getAbsolutePath() + File.separator + "discounts");
        discountsDirectory.mkdir();
        loginDataFile = new File(mainDirectory.getAbsolutePath() + File.separator + "loginData.json");
        ownerOfFile = new File(mainDirectory.getAbsolutePath() + File.separator + "ownerOf.json");
    }

    private void load() {
        try {
            loginDataFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loginData = gson.fromJson(readFileToString(loginDataFile.toPath()), Map.class);
        if (loginData == null) {
            loginData = new ConcurrentHashMap<>();
        }
        try {
            ownerOfFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ownerOf = new ConcurrentHashMap<>();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> ownerOfMap = gson.fromJson(readFileToString(ownerOfFile.toPath()), type);
        if (ownerOfMap == null) return;
        for (String key : ownerOfMap.keySet()) {
            ownerOf.put(key, ownerOfMap.get(key));
        }
    }

   public String generateID(String typeOfObject) {
       String prefix = getPrefix(typeOfObject);
       StringBuilder result = new StringBuilder();
       result.append(prefix).append("-");
       Random random = new Random();
       String rand1 = Integer.toString(random.nextInt(0x10000),16).toUpperCase();
       if (rand1.length() < 4) {
//           result.insert(0, "0".repeat(4 - rand1.length()));
           rand1 = "0".repeat(4 - rand1.length()) + rand1;
       }
       result.append(rand1).append("-");
       String rand2 = Integer.toString(random.nextInt(0x10000),16).toUpperCase(Locale.ROOT);
       if (rand2.length() < 4) {
//           result.insert(0, "0".repeat(4 - rand2.length()));
           rand2 = "0".repeat(4 - rand2.length()) + rand2;
       }
       result.append(rand2);
       return result.toString();
   }

   private String getPrefix(String type) {
        switch (type) {
            case "restaurant":
                return "R";
            case "order":
                return "O";
            case "comment":
                return "C";
            case "foodMenu":
                return "M";
            case "food":
                return "F";
        }
        return "X";
   }

    // returns json string to caller (probably server). server can modify object with gson or directly send it to client.
    public String getJson(String id) {
        String directory = "";
        switch (id.substring(0, 2)) {
            case "O-":
                directory = ordersDirectory.getAbsolutePath() + File.separator + id + ".json";
                return getOrder(Paths.get(directory));
            case "R-":
                directory = restaurantsDirectory.getAbsolutePath();
                break;
            case "M-":
                directory = menusDirectory.getAbsolutePath() + File.separator + id;
                break;
            case "C-":
                directory = commentsDirectory.getAbsolutePath();
                break;
            default:
                return null;
        }
        return readFileToString(Paths.get(directory + File.separator + id + ".json"));
    }

    private String getOrder(Path path) {
        Map<String, Object> order = gson.fromJson(readFileToString(path), mapType);
        order.put("restaurant", gson.fromJson(getJson((String) order.get("restaurantID")), Object.class));
        return gson.toJson(order);
    }

    public String getJson(String phoneNumber, boolean isUser) {
        if (isUser) {
            return readFileToString(Paths.get(
                    userAccountsDirectory.getAbsolutePath() +
                            File.separator +
                            phoneNumber +
                            ".json"
            ));
        }
        return readFileToString(Paths.get(
                ownerAccountsDirectory.getAbsolutePath() +
                        File.separator +
                        phoneNumber +
                        File.separator +
                        phoneNumber +
                        ".json"
        ));
    }

    public String getActiveOrdersJson(String phoneNumber) {
        return readFileToString(Paths.get(
                ownerAccountsDirectory.getAbsolutePath() +
                        File.separator +
                        phoneNumber +
                        File.separator +
                        "activeOrders.json"
        ));
    }

    public String getJson(String menuID, String foodID) {
        return readFileToString(Paths.get(menusDirectory.getAbsolutePath() + File.separator + menuID + File.separator + foodID + ".json"));
    }

    //save the new json to the id
    public void saveChangeByID(String id, String newJSON) {
        String directory = "";
        switch (id.substring(0, 2)) {
            case "O-":
                directory = ordersDirectory.getAbsolutePath();
                break;
            case "R-":
                directory = restaurantsDirectory.getAbsolutePath();
                break;
            case "M-":
                directory = menusDirectory.getAbsolutePath() + File.separator + id;
                File directory2 = new File(directory);
                directory2.mkdir();
                break;
            case "C-":
                directory = commentsDirectory.getAbsolutePath();
                break;
            default:
                return;
        }
        writeFileFromString(Paths.get(directory + File.separator + id + ".json"), newJSON);
    }

    public void saveChangeByID(String phoneNumber, boolean isUser, String newJSON) {
        if (isUser) {
            writeFileFromString(Paths.get(
                    userAccountsDirectory.getAbsolutePath() +
                            File.separator +
                            phoneNumber +
                            ".json"
            ), newJSON);
            return;
        }
        writeFileFromString(Paths.get(
                ownerAccountsDirectory.getAbsolutePath() +
                        File.separator +
                        phoneNumber +
                        File.separator +
                        phoneNumber +
                        ".json"
        ), newJSON);
    }

    public void saveActiveOrders(String phoneNumber, String newJSON) {
        writeFileFromString(Paths.get(
                ownerAccountsDirectory.getAbsolutePath() +
                        File.separator +
                        phoneNumber +
                        File.separator +
                        "activeOrders.json"
        ), newJSON);
    }

    public void saveChangeByID(String menuID, String foodID, String newJSON) {
        writeFileFromString(Paths.get(menusDirectory.getAbsolutePath() + File.separator + menuID + File.separator + foodID + ".json"), newJSON);
    }


    //creating a new object based on id and saving it in our database
    public void createNewObj(String id, String JSON) {
        String newJSONDirectory = "";
        switch (id.substring(0 , 2))
        {
            case "O-":
                newJSONDirectory = ordersDirectory.getAbsolutePath();
                break;
            case "R-":
                newJSONDirectory = restaurantsDirectory.getAbsolutePath();
                break;
            case "M-":
                newJSONDirectory = menusDirectory.getAbsolutePath() + File.separator + id;
                File directory = new File(newJSONDirectory);
                directory.mkdir();
                break;
            case "C-":
                newJSONDirectory = commentsDirectory.getAbsolutePath();
                break;
        }
        writeFileFromString(Paths.get(newJSONDirectory + File.separator + id + ".json"), JSON);
    }

    //for signup
    public void createNewObj(String phoneNumber, String password, boolean isUser, String JSON) {
        loginData.put(phoneNumber, password);
        writeFileFromString(loginDataFile.toPath(), gson.toJson(loginData));
        Path path;
        if (isUser) {
            path = Paths.get(userAccountsDirectory.getAbsolutePath() + File.separator + phoneNumber + ".json");
        } else {
            File dir = new File(ownerAccountsDirectory.getAbsolutePath() + File.separator + phoneNumber);
            dir.mkdir();
            path = Paths.get(dir.getAbsolutePath() +  File.separator + phoneNumber + ".json");
            writeFileFromString(Paths.get(dir.getAbsolutePath() + File.separator + "activeOrders.json"), "[]");
        }
        writeFileFromString(path, JSON);
    }

    public void createNewObj(String menuID, String foodID, String JSON) {
        writeFileFromString(
                Paths.get(menusDirectory.getAbsolutePath() + File.separator + menuID + File.separator + foodID + ".json"),
                JSON
        );
    }

    public void removeFood(String menuID, String foodID) {
        deleteFile(Paths.get(menusDirectory.getAbsolutePath() + File.separator + menuID + File.separator + foodID + ".json"));
    }

    public String getDiscountJson(String code) {
        return readFileToString(Paths.get(discountsDirectory.getAbsolutePath() + File.separator + code + ".json"));
    }

    public void removeDiscount(String code) {
        deleteFile(Paths.get(discountsDirectory.getAbsolutePath() + File.separator + code + ".json"));
    }

    public void addOwnerOf(String restaurantID, String phoneNumber) {
        ownerOf.put(restaurantID, phoneNumber);
        writeFileFromString(ownerOfFile.toPath(), gson.toJson(ownerOf));
    }

    public void addOrderToOwnerFile(String restaurantID, String orderID) {
        String ownerPhoneNumber = ownerOf.get(restaurantID);
        List<String> activeOrderIDs = (List<String>) gson.fromJson(getActiveOrdersJson(ownerPhoneNumber), Object.class);
        activeOrderIDs.add(orderID);
        saveActiveOrders(ownerPhoneNumber, gson.toJson(activeOrderIDs));
    }

    public void addCommentToRestaurantFile(String restaurantID, String commentID) {
        Map<String, Object> restaurant = gson.fromJson(getJson(restaurantID), Map.class);
        List<String> commentIDs = (List<String>) restaurant.get("commentIDs");
        commentIDs.add(commentID);
        restaurant.put("commentIDs", commentIDs);
        saveChangeByID(restaurantID, gson.toJson(restaurant));
    }

    //if doesn't exist, this returns null
    private String readFileToString(Path path) {
        String result = null;
        Semaphore semaphore = getFileLock(path);
        try {
            semaphore.acquire();
            result = Files.readString(path, StandardCharsets.US_ASCII);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
        return result;
    }

    //a util function to write a string to a file
    //make sure that path exists, otherwise nothing happens
    private void writeFileFromString(Path path, String data) {
        Semaphore semaphore = getFileLock(path);
        try {
            semaphore.acquire(MAX_PERMITS);
            Files.writeString(path, data);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release(MAX_PERMITS);
        }
    }

    private void deleteFile(Path path) {
        Semaphore semaphore = getFileLock(path);
        try {
            semaphore.acquire(MAX_PERMITS);
            Files.delete(path);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release(MAX_PERMITS);
        }
    }

    private Semaphore getFileLock(Path path) {
        String absolutePath = path.toAbsolutePath().toString();
        Semaphore semaphore = locks.get(absolutePath);
        if (semaphore == null) {
            semaphore = new Semaphore(MAX_PERMITS, true);
            locks.put(absolutePath, semaphore);
        }
        return semaphore;
    }

    public void search(SearchQuery<Restaurant> searchQuery) {
        File[] files = restaurantsDirectory.listFiles();
        assert files != null;
        for (File file : files) {
            Restaurant restaurant = gson.fromJson(readFileToString(file.toPath()), Restaurant.class);
            searchQuery.feed(restaurant);
        }
        searchQuery.finish();
    }

    public String getAllRestaurants(int count) {
        File[] files = restaurantsDirectory.listFiles();
        assert files != null;
        List<Restaurant> all = new ArrayList<>();
        for (File file : files) {
            Restaurant restaurant = gson.fromJson(readFileToString(file.toPath()), Restaurant.class);
            all.add(restaurant);
            count--;
            if (count <= 0) break;
        }
        return gson.toJson(all);
    }

    public boolean checkPassword(String phoneNumber, String password) {
        return password.equals(loginData.get(phoneNumber));
    }

    public boolean isPhoneNumberUnique(String phoneNumber) {
        File[] files = userAccountsDirectory.listFiles();
        assert files != null;
        for (File f : files) {
            if (f.getName().contains(phoneNumber)) return false;
        }
        files = ownerAccountsDirectory.listFiles();
        assert files != null;
        for (File f : files) {
            if (f.getName().contains(phoneNumber)) return false;
        }
        return true;
    }

}
