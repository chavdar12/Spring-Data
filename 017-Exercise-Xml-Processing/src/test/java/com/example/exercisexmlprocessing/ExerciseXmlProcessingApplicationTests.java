package com.example.exercisexmlprocessing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/** Runs the whole app on H2: seeds both databases and writes all ten result files. */
@SpringBootTest(properties = "app.output-dir=build/test-output")
@AutoConfigureTestDatabase
class ExerciseXmlProcessingApplicationTests {

    @Test
    void writesEveryQueryResult() {
        for (String file : new String[]{"products-in-range.xml", "users-sold-products.xml", "categories-by-products.xml",
                "users-and-products.xml", "ordered-customers.xml", "toyota-cars.xml", "local-suppliers.xml",
                "cars-and-parts.xml", "customers-total-sales.xml", "sales-discounts.xml"}) {
            assertThat(Path.of("build/test-output", file)).exists().satisfies(p -> assertThat(Files.size(p)).isGreaterThan(2));
        }
    }
}
