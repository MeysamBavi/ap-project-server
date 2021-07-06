package models;

import java.util.function.Predicate;

public class RestaurantPredicate {
    private String name;
    private String category;
    public RestaurantPredicate(String name, String category)
    {
        this.name = name;
        this.category = category;
    }
    public Predicate<Restaurant> generate() {
        return new Predicate<Restaurant>() {
            @Override
            public boolean test(Restaurant restaurant) {
                if (name != null && !restaurant.name.contains(name)) {
                    return false;
                }
                if (category != null && !restaurant.foodCategories.contains(category)) {
                    return false;
                }
                return true;
            }
        };
    }
}
