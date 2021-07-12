package models;

import java.util.List;

public class Restaurant {
    public String ID;
    public String name;
    public String menuID;
    public Address address;
    public double areaOfDispatch;
    public double score;
    public double numberOfComments;
    public List<String> commentIDs;
    public List<String> foodCategories;
}
