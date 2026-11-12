package com.example.exercisejsonprocessing.cardealer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Shapes of suppliers.json, parts.json, cars.json and customers.json. */
public final class SeedDtos {

    private SeedDtos() {
    }

    public record SupplierSeed(@NotBlank String name, boolean isImporter) {
    }

    public record PartSeed(@NotBlank String name, @NotNull @Positive BigDecimal price, @PositiveOrZero Integer quantity) {
    }

    public record CarSeed(@NotBlank String make, @NotBlank String model, @NotNull @PositiveOrZero Long travelledDistance) {
    }

    public record CustomerSeed(@NotBlank String name, @NotNull LocalDateTime birthDate, boolean isYoungDriver) {
    }
}
