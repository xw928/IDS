import java.util.HashMap;
import java.util.Set;

public class test {
    public static void main(String[] args) {
        HashMap<String, Object[]> baseline = new HashMap<>();
        baseline.put("login", new Object[]{3, 1.5, "this is value"});
        baseline.put("logout", new Object[]{4, 2.0, "another value"});
        baseline.put("jesus", new Object[]{"D", 0, null, 3, 4.0, 1.5});

        // Retrieve all keys
        Set<String> keys = baseline.keySet();
        System.out.println(keys);

        // Print all keys
        for (String key : keys) {
            System.out.println("Key: " + key);
            System.out.println(baseline.get(key)[1]);
        }
        System.out.println(baseline.get("jesus")[5]);
    }
}
