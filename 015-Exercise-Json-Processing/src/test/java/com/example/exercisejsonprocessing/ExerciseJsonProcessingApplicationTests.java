package com.example.exercisejsonprocessing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/** Runs the whole app on H2: seeds both databases and writes all ten result files. */
@SpringBootTest(properties = "app.output-dir=build/test-output")
@AutoConfigureTestDatabase
class ExerciseJsonProcessingApplicationTests {

    @Test
    void writesEveryQueryResult() {
        for (String file : new String[]{"products-in-range.json", "users-sold-products.json", "categories-by-products.json",
                "users-and-products.json", "ordered-customers.json", "toyota-cars.json", "local-suppliers.json",
                "cars-and-parts.json", "customers-total-sales.json", "sales-discounts.json"}) {
            assertThat(Path.of("build/test-output", file)).exists().satisfies(p -> assertThat(Files.size(p)).isGreaterThan(2));
        }
    }
}
