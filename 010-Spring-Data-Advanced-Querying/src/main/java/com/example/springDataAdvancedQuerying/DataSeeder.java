package com.example.springDataAdvancedQuerying;

import com.example.springDataAdvancedQuerying.repositories.ShampooRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/** Loads the lab data once, into an empty database. */
@Component
public class DataSeeder {

    private final ShampooRepository shampooRepository;
    private final DataSource dataSource;

    public DataSeeder(ShampooRepository shampooRepository, DataSource dataSource) {
        this.shampooRepository = shampooRepository;
        this.dataSource = dataSource;
    }

    public void seed() {
        if (shampooRepository.count() == 0) {
            new ResourceDatabasePopulator(new ClassPathResource("shampoo-company-insert.sql")).execute(dataSource);
        }
    }
}
