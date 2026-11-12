package com.example.springDataAdvancedQuerying.services;

import com.example.springDataAdvancedQuerying.entities.Ingredient;
import com.example.springDataAdvancedQuerying.repositories.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Service
public class IngredientServicesImpl implements IngredientServices {

    private static final BigDecimal TEN_PERCENT_MORE = new BigDecimal("1.10");

    private final IngredientRepository ingredientRepository;

    public IngredientServicesImpl(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public List<Ingredient> findAllByNameStartingWith(String prefix) {
        return this.ingredientRepository.findAllByNameStartingWithOrderById(prefix);
    }

    @Override
    public List<Ingredient> findAllByNamesOrderByPrice(Collection<String> names) {
        return this.ingredientRepository.findAllByNameInOrderByPrice(names);
    }

    @Override
    @Transactional
    public int deleteByName(String name) {
        this.ingredientRepository.deleteShampooLinksByName(name);
        return this.ingredientRepository.deleteByName(name);
    }

    @Override
    @Transactional
    public int increaseAllPricesBy10Percent() {
        return this.ingredientRepository.multiplyAllPrices(TEN_PERCENT_MORE);
    }

    @Override
    @Transactional
    public int increasePricesBy10Percent(Collection<String> names) {
        return this.ingredientRepository.multiplyPricesByNames(TEN_PERCENT_MORE, names);
    }
}
