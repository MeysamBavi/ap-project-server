import com.google.gson.Gson;

import java.io.*;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

    //all fields in database are static..


    //first string is for the id and the second one is for the file address

    public static ConcurrentHashMap<String , String> Comments = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String , String> Restaurants = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String , String> Menus = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String , String> UserAccounts = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String , String> OwnerAccounts = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String , String> Images = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String , String> Foods = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String , String> Orders = new ConcurrentHashMap<>();

    //save the new json to the id
    public static void SaveChangeByID(String id , String newJSON)
    {
       String Address = getObjectByID(id);
       SaveFile(id,newJSON,Address);
    }

    //returns the json string saved in the database
    public static String getObjectByID(String id)
    {
        //if we want to make the object we need to use gson parser but i don't see any use in it ::
        Gson gson = new Gson();
        String retStr = "";
        if (id.startsWith("M-"))
        {
            if (Menus.containsKey(id))
            {
                retStr = getJSON(Menus.get(id));
            }
        }
        if (id.startsWith("R-"))
        {
            if (Restaurants.containsKey(id))
            {
                retStr = getJSON(Restaurants.get(id));
            }
        }
        if (id.startsWith("C-"))
        {
            if (Comments.containsKey(id))
            {
                retStr = getJSON(Comments.get(id));
            }
        }
        return retStr;
    }

    //creating a new object based on id and putting it on our hashmap
    public static void CreateNewObj(String id ,String JSON, String Address)
    {
        if (id.startsWith("M-")) {
            Menus.put(id,Address);
        }
        else if (id.startsWith("R-")) {
            Restaurants.put(id, Address);
        }
        else if (id.startsWith("C-")){
            Comments.put(id,Address);
        }else if (id.startsWith("O-")) {
            Orders.put(id,Address);
        }else if (id.startsWith("F-")) {
            Foods.put(id,Address);
        }
        Database.SaveFile(id,JSON,Address);
    }

    //saving a file to the selected address and writing the data inside it (JSON in our case)
    private static void SaveFile(String id , String Data , String Address)
    {
        File f = new File(Address+"\\"+id+".json");
        try {
            FileWriter fw = new FileWriter(Address,false);
            fw.write(Data);
            fw.flush();
        } catch (IOException e) {
            System.out.println("error happened while trying to write data in the file !!");
        }
    }

    //getting the json string inside the json file created
    private static String getJSON(String Address)
    {
        File f = new File(Address);
        StringBuilder retJSON = new StringBuilder((int) f.length());
        try {
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine())
            {
                retJSON.append(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("file was not found");
        }
        return retJSON.toString();
    }


}
