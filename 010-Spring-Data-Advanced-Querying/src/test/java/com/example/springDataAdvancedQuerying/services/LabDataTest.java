package com.example.springDataAdvancedQuerying.services;

import com.example.springDataAdvancedQuerying.DataSeeder;
import com.example.springDataAdvancedQuerying.entities.Ingredient;
import com.example.springDataAdvancedQuerying.entities.Shampoo;
import com.example.springDataAdvancedQuerying.entities.Size;
import com.example.springDataAdvancedQuerying.repositories.IngredientRepository;
import com.example.springDataAdvancedQuerying.repositories.LabelRepository;
import com.example.springDataAdvancedQuerying.repositories.ShampooRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Every lab task against the real lab data in shampoo-company-insert.sql. */
@DataJpaTest
@Import({DataSeeder.class, ShampooServicesImpl.class, IngredientServicesImpl.class})
class LabDataTest {

    @Autowired
    private DataSeeder dataSeeder;
    @Autowired
    private ShampooServices shampoos;
    @Autowired
    private IngredientServices ingredients;
    @Autowired
    private ShampooRepository shampooRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private LabelRepository labelRepository;

    @BeforeEach
    void seed() {
        dataSeeder.seed();
        dataSeeder.seed(); // idempotent
    }

    private static List<String> lines(List<Shampoo> result) {
        return result.stream().map(Shampoo::toString).toList();
    }

    @Test
    void seedLoadsAllRows() {
        assertThat(shampooRepository.count()).isEqualTo(25);
        assertThat(ingredientRepository.count()).isEqualTo(14);
    }

    @Test
    void task1BySize() {
        assertThat(lines(shampoos.findAllBySizeOrderById(Size.MEDIUM))).containsExactly(
                "Nature Moments Mediterranean Olive Oil & Aloe Vera MEDIUM 6.50lv.", "Volume & Fullness Lavender MEDIUM 5.50lv.",
                "Rose Shine & Hydration MEDIUM 6.50lv.", "Color Protection & Radiance MEDIUM 6.75lv.",
                "Heavenly Long Long-Hair MEDIUM 7.50lv.", "Sea Buckthorn Vital MEDIUM 6.50lv.",
                "Fresh it Up! MEDIUM 7.65lv.", "Nectar Nutrition MEDIUM 6.85lv.");
    }

    @Test
    void task2BySizeOrLabel() {
        assertThat(lines(shampoos.findAllBySizeOrLabelIdOrderByPrice(Size.MEDIUM, labelRepository.findById(10L).orElseThrow())))
                .hasSize(10)
                .startsWith("Volume & Fullness Lavender MEDIUM 5.50lv.", "Intense-Charm Brunette SMALL 5.50lv.")
                .endsWith("Fresh it Up! MEDIUM 7.65lv.", "Active-Caffeine SMALL 7.80lv.");
    }

    @Test
    void task3PriceHigherThan() {
        assertThat(lines(shampoos.findAllByPriceHigherThan(new BigDecimal("5"))))
                .hasSize(23)
                .startsWith("Superfruit Nutrition LARGE 13.80lv.", "Cotton Fresh LARGE 13.60lv.");
    }

    @Test
    void task4And5Ingredients() {
        assertThat(ingredients.findAllByNameStartingWith("M")).extracting(Ingredient::getName)
                .containsExactly("Macadamia Oil", "Mineral-Collagen", "Micro-Crystals");
        assertThat(ingredients.findAllByNamesOrderByPrice(List.of("Lavender", "Herbs", "Apple"))).extracting(Ingredient::getName)
                .first().isEqualTo("Apple");
    }

    @Test
    void task6CountCheaperThan() {
        assertThat(shampoos.countByPriceLowerThan(new BigDecimal("8.50"))).isEqualTo(15);
    }

    @Test
    void task7And8ShampoosByIngredients() {
        assertThat(lines(shampoos.findAllByIngredientNames(List.of("Berry", "Mineral-Collagen"))))
                .hasSize(12)
                .startsWith("Nature Moments Mediterranean Olive Oil & Aloe Vera MEDIUM 6.50lv.");
        assertThat(lines(shampoos.findAllWithIngredientCountLessThan(2))).containsExactly(
                "Moroccan Argan Oil & Macadamia SMALL 5.50lv.", "Volume & Fullness Lavender MEDIUM 5.50lv.",
                "Sea Buckthorn Vital MEDIUM 6.50lv.", "Active-Caffeine SMALL 7.80lv.");
    }

    @Test
    void task9DeleteIngredientUsedByShampoos() {
        assertThat(ingredients.deleteByName("Apple")).isEqualTo(1);
        assertThat(ingredientRepository.count()).isEqualTo(13);
        assertThat(shampoos.findAllByIngredientNames(List.of("Apple"))).isEmpty();
    }

    @Test
    void task10And11IncreasePrices() {
        assertThat(ingredients.increaseAllPricesBy10Percent()).isEqualTo(14);
        assertThat(ingredients.increasePricesBy10Percent(List.of("Apple", "Nettle"))).isEqualTo(2);

        // 0.50 * 1.1 = 0.55, then * 1.1 = 0.605, stored with scale 2
        assertThat(ingredients.findAllByNamesOrderByPrice(List.of("Apple"))).singleElement()
                .satisfies(apple -> assertThat(apple.getPrice()).isEqualByComparingTo("0.61"));
        assertThat(ingredients.findAllByNamesOrderByPrice(List.of("Berry"))).singleElement()
                .satisfies(berry -> assertThat(berry.getPrice()).isEqualByComparingTo("0.55"));
    }
}
