package models;

import java.util.ArrayList;
import java.util.Collections;

public class UserAccount extends Account{
    String firstName;
    String lastName;
    private String defaultAddressName;
    ArrayList<String> favRestaurantsIDs;
    ArrayList<String> commentIDs;
    ArrayList<Order> cart;
    public UserAccount(String phoneNumber , String firstName , String lastName , ArrayList<String> favRestaurantsIDs , ArrayList<String>commentIDs)
    {
        super(phoneNumber);
        this.firstName = firstName;
        this.lastName = lastName;
        this.favRestaurantsIDs = new ArrayList<>();
        Collections.copy(this.favRestaurantsIDs , favRestaurantsIDs);
        this.commentIDs = new ArrayList<>();
        Collections.copy(this.commentIDs , commentIDs);
    }
}
