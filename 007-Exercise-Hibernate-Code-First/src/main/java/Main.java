import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Creates the schema of one exercise database: gringotts, sales, university, hospital or billspayment. */
public class Main {

    static final List<String> UNITS = List.of("gringotts", "sales", "university", "hospital", "billspayment");

    public static void main(String[] args) {
        String unit = args.length > 0 ? args[0] : "gringotts";
        if (!UNITS.contains(unit)) {
            System.out.println("Choose one of " + UNITS);
            return;
        }

        Map<String, String> overrides = new HashMap<>();
        if (System.getenv("DB_USERNAME") != null) overrides.put("jakarta.persistence.jdbc.user", System.getenv("DB_USERNAME"));
        if (System.getenv("DB_PASSWORD") != null) overrides.put("jakarta.persistence.jdbc.password", System.getenv("DB_PASSWORD"));

        try (EntityManagerFactory ignored = Persistence.createEntityManagerFactory(unit, overrides)) {
            System.out.println("Schema for " + unit + " is up to date.");
        }
    }
}
