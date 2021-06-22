import java.util.function.Predicate;

public class RestaurantPredicate {
    private String name;

    public Predicate<Restaurant> generate() {
        return new Predicate<Restaurant>() {
            @Override
            public boolean test(Restaurant restaurant) {
                return restaurant.name.contains(name);
            }
        };
    }
}
