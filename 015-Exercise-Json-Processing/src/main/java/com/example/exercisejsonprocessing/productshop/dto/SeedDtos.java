package com.example.exercisejsonprocessing.productshop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Shapes of users.json, products.json and categories.json. */
public final class SeedDtos {

    private SeedDtos() {
    }

    public record UserSeed(String firstName, @NotNull @Size(min = 3) String lastName, Integer age) {
    }

    public record ProductSeed(@NotNull @Size(min = 3) String name, @NotNull @Positive BigDecimal price) {
    }

    public record CategorySeed(@NotNull @Size(min = 3, max = 15) String name) {
    }
}
