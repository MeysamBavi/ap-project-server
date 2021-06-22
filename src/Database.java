import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Database {
    private File mainDirectory;
    private File ordersDirectory;
    private File restaurantsDirectory;
    private File menusDirectory;
    private File commentsDirectory;
    private File userAccountsDirectory;
    private File ownerAccountsDirectory;
    private File discountsDirectory;

    public Database(String directory) {
        File dir = new File(directory);
        dir.mkdir();
        if (!dir.isDirectory()) {
            throw new RuntimeException("Not a directory.");
        }
        mainDirectory = dir;
        createSubDirectories();
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
    }

    // returns json string to caller (probably server). server can modify object with gson or directly send it to client.
    public String getJson(String id) {
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
                break;
            case "C-":
                directory = commentsDirectory.getAbsolutePath();
                break;
            default:
                return null;
        }
        return readFileToString(Paths.get(directory + File.separator + id + ".json"));
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

    //save the new json to the id
    public void saveChangeByID(String id , String newJSON) {
//       String Address = getObjectByID(id);
//       SaveFile(id,newJSON,Address);
        //TODO
    }

    //creating a new object based on id and saving it in our database
    public void createNewObj(String id, String JSON) {
        String newJSONDirectory = "";
        switch (id.substring(0 , 2))
        {
            case "O-":
                newJSONDirectory = ordersDirectory.getAbsolutePath() + File.separator + id + ".json";
                break;
            case "R-":
                newJSONDirectory = restaurantsDirectory.getAbsolutePath() + File.separator + id + ".json";
                break;
            case "M-":
                newJSONDirectory = menusDirectory.getAbsolutePath() + File.separator + id + File.separator + id + ".json";
                break;
            case "C-":
                newJSONDirectory = commentsDirectory.getAbsolutePath() + File.separator + id + ".json";
                break;
        }
        writeFileFromString(Paths.get(newJSONDirectory), JSON);
    }

    public void createNewObj(String phoneNumber, boolean isUser, String JSON) {
        Path path;
        if (isUser) {
            path = Paths.get(userAccountsDirectory.getAbsolutePath() + File.separator + phoneNumber + ".json");
        } else {
            path = Paths.get(ownerAccountsDirectory.getAbsolutePath() + File.separator + phoneNumber + File.separator + phoneNumber + ".json");
        }
        writeFileFromString(path, JSON);
    }

    public String getDiscountJson(String code) {
        return readFileToString(Paths.get(discountsDirectory.getAbsolutePath() + File.separator + code + ".json"));
    }

    public void removeDiscount(String code) {
        // TODO
    }

    private static String readFileToString(Path path) {
        try {
            return Files.readString(path, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //a util function to write a string to a file
    private static void writeFileFromString(Path path, String data) {
        //TODO lock the file with lock system
        try {
            Files.writeString(path, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
