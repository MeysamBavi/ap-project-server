package util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;
import models.Restaurant;
import models.RestaurantPredicate;

public class JsonUtil {

    private final static Gson gson = new Gson();
    private static final Type type1 = new TypeToken<Map<String, Object>>(){}.getType();
    private static final Type type2 = new TypeToken<Map<String, String>>(){}.getType();

    public static Map<String, Object> jsonToMap(String json) {
        return gson.fromJson(json, type1);
    }

    public static Map<String, String> jsonToMapString(String json) {
        return gson.fromJson(json, type2);
    }

    public static Object jsonToObject(String json) {
        return gson.fromJson(json, Object.class);
    }

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    public static RestaurantPredicate jsonToRestaurantPredicate(String json) {
        return gson.fromJson(json, RestaurantPredicate.class);
    }

    public static Restaurant jsonToRestaurant(String json) {
        return gson.fromJson(json, Restaurant.class);
    }
}
