package models;

import java.util.Date;
import java.util.HashMap;

public class Order {
    String code;
    boolean isRequested;
    boolean isDelivered;
    Date time;
    HashMap<FoodData,Integer> items;

    Restaurant restaurant;
}
