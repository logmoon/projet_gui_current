package projet_gui.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for storing temporary data that needs to be shared between controllers
 */
public class DataStore {
    private static final Map<String, Object> data = new HashMap<>();
    
    /**
     * Store a value with the given key
     * @param key The key to store the value under
     * @param value The value to store
     */
    public static void set(String key, Object value) {
        data.put(key, value);
    }
    
    /**
     * Get a value by its key
     * @param key The key to retrieve the value for
     * @return The stored value, or null if no value exists for the key
     */
    public static Object get(String key) {
        return data.get(key);
    }
    
    /**
     * Get a value by its key and cast it to the expected type
     * @param <T> The expected type of the value
     * @param key The key to retrieve the value for
     * @param clazz The class of the expected type
     * @return The stored value cast to the expected type, or null if no value exists for the key
     */
    public static <T> T get(String key, Class<T> clazz) {
        Object value = data.get(key);
        if (value != null && clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return null;
    }
    
    /**
     * Remove a value by its key
     * @param key The key to remove
     * @return The removed value, or null if no value existed for the key
     */
    public static Object remove(String key) {
        return data.remove(key);
    }
    
    /**
     * Clear all stored data
     */
    public static void clear() {
        data.clear();
    }
}