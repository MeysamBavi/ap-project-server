package models;

import java.util.ArrayList;

public abstract class Account {
    String phoneNumber;
    ArrayList<Order> previousOrders;
    ArrayList<Order> activeOrders;
    public Account(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
        this.previousOrders = new ArrayList<>();
        this.activeOrders = new ArrayList<>();
    }
}
