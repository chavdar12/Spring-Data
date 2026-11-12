import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        Map<String, String> overrides = new HashMap<>();
        if (System.getenv("DB_USERNAME") != null) overrides.put("jakarta.persistence.jdbc.user", System.getenv("DB_USERNAME"));
        if (System.getenv("DB_PASSWORD") != null) overrides.put("jakarta.persistence.jdbc.password", System.getenv("DB_PASSWORD"));

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try (EntityManagerFactory factory = Persistence.createEntityManagerFactory("soft_uni", overrides);
             EntityManager em = factory.createEntityManager()) {

            System.out.println("Task number (2-13):");
            int task = Integer.parseInt(reader.readLine().trim());
            String input = switch (task) {
                case 3, 6, 8, 11, 13 -> {
                    System.out.println("Input:");
                    yield reader.readLine();
                }
                default -> "";
            };
            System.out.println(new Engine(em).run(task, input));
        }
    }
}
