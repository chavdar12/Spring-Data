package com.example.springDataAdvancedQuerying.services;

import com.example.springDataAdvancedQuerying.entities.Label;
import com.example.springDataAdvancedQuerying.entities.Shampoo;
import com.example.springDataAdvancedQuerying.entities.Size;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface ShampooServices {
    List<Shampoo> findAllBySizeOrderById(Size size);

    List<Shampoo> findAllBySizeOrLabelIdOrderByPrice(Size size, Label label_id);

    List<Shampoo> findAllByPriceHigherThan(BigDecimal price);

    long countByPriceLowerThan(BigDecimal price);

    List<Shampoo> findAllByIngredientNames(Collection<String> names);

    List<Shampoo> findAllWithIngredientCountLessThan(int count);
}
