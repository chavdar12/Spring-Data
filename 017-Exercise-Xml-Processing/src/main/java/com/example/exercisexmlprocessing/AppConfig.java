package com.example.exercisexmlprocessing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class AppConfig {

    /** Random relations while seeding; tests swap in a seeded one. */
    @Bean
    public Random random() {
        return new Random();
    }
}
