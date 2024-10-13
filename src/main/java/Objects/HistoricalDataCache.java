package Objects;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class HistoricalDataCache {

    private static final ConcurrentHashMap<String, ArrayList<Float>> cache = new ConcurrentHashMap<>();
    
    // Add Historical Time Series to Memory
    public static void put(String key, ArrayList<Float> value) {
        cache.put(key, value);
    }
    // Get Historical Time Series from Memory
    public static ArrayList<Float> get(String key) {
        return cache.get(key);
    }
    // Remove Object from memory
    public static void remove(String key) {
        cache.remove(key);
    }
}