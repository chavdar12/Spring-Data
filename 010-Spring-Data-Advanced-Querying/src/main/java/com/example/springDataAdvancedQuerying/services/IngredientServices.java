package com.example.springDataAdvancedQuerying.services;

import com.example.springDataAdvancedQuerying.entities.Ingredient;

import java.util.Collection;
import java.util.List;

public interface IngredientServices {
    List<Ingredient> findAllByNameStartingWith(String prefix);

    List<Ingredient> findAllByNamesOrderByPrice(Collection<String> names);

    int deleteByName(String name);

    int increaseAllPricesBy10Percent();

    int increasePricesBy10Percent(Collection<String> names);
}
