package com.example.springDataAdvancedQuerying;

import com.example.springDataAdvancedQuerying.entities.Size;
import com.example.springDataAdvancedQuerying.repositories.LabelRepository;
import com.example.springDataAdvancedQuerying.services.IngredientServices;
import com.example.springDataAdvancedQuerying.services.ShampooServices;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class CLR implements CommandLineRunner {

    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private final DataSeeder dataSeeder;
    private final ShampooServices shampooServices;
    private final IngredientServices ingredientServices;
    private final LabelRepository labelRepository;

    public CLR(DataSeeder dataSeeder, ShampooServices shampooServices, IngredientServices ingredientServices,
               LabelRepository labelRepository) {
        this.dataSeeder = dataSeeder;
        this.shampooServices = shampooServices;
        this.ingredientServices = ingredientServices;
        this.labelRepository = labelRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        dataSeeder.seed();

        System.out.println("Task number (1-11):");
        String task = reader.readLine();
        if (task == null) {
            return;
        }
        switch (task.trim()) {
            case "1" -> shampooServices.findAllBySizeOrderById(readSize()).forEach(System.out::println);
            case "2" -> {
                Size size = readSize();
                System.out.println("Label id:");
                labelRepository.findById(Long.parseLong(reader.readLine().trim())).ifPresentOrElse(
                        label -> shampooServices.findAllBySizeOrLabelIdOrderByPrice(size, label).forEach(System.out::println),
                        () -> System.out.println("No such label"));
            }
            case "3" -> shampooServices.findAllByPriceHigherThan(readPrice()).forEach(System.out::println);
            case "4" -> {
                System.out.println("Starting letters:");
                ingredientServices.findAllByNameStartingWith(reader.readLine().trim())
                        .forEach(i -> System.out.println(i.getName()));
            }
            case "5" -> ingredientServices.findAllByNamesOrderByPrice(readNames())
                    .forEach(i -> System.out.println(i.getName()));
            case "6" -> System.out.println(shampooServices.countByPriceLowerThan(readPrice()));
            case "7" -> shampooServices.findAllByIngredientNames(readNames()).forEach(s -> System.out.println(s.getBrand()));
            case "8" -> {
                System.out.println("Ingredient count:");
                shampooServices.findAllWithIngredientCountLessThan(Integer.parseInt(reader.readLine().trim()))
                        .forEach(s -> System.out.println(s.getBrand()));
            }
            case "9" -> {
                System.out.println("Ingredient name:");
                System.out.println(ingredientServices.deleteByName(reader.readLine().trim()) + " ingredient(s) deleted");
            }
            case "10" -> System.out.println(ingredientServices.increaseAllPricesBy10Percent() + " ingredient prices increased");
            case "11" -> System.out.println(ingredientServices.increasePricesBy10Percent(readNames()) + " ingredient prices increased");
            default -> System.out.println("Unknown task");
        }
    }

    private Size readSize() throws IOException {
        System.out.println("Size (SMALL, MEDIUM, LARGE):");
        return Size.valueOf(reader.readLine().trim().toUpperCase());
    }

    private BigDecimal readPrice() throws IOException {
        System.out.println("Price:");
        return new BigDecimal(reader.readLine().trim());
    }

    private List<String> readNames() throws IOException {
        System.out.println("Ingredient names, comma separated:");
        return Arrays.stream(reader.readLine().split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
}
