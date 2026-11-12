package com.example.springDataAdvancedQuerying.services;

import com.example.springDataAdvancedQuerying.entities.Label;
import com.example.springDataAdvancedQuerying.entities.Shampoo;
import com.example.springDataAdvancedQuerying.entities.Size;
import com.example.springDataAdvancedQuerying.repositories.ShampooRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Service
public class ShampooServicesImpl implements ShampooServices {

    private final ShampooRepository shampooRepository;

    public ShampooServicesImpl(ShampooRepository shampooRepository) {
        this.shampooRepository = shampooRepository;
    }


    @Override
    public List<Shampoo> findAllBySizeOrderById(Size size) {
        return this.shampooRepository.findAllBySizeOrderById(size);
    }

    @Override
    public List<Shampoo> findAllBySizeOrLabelIdOrderByPrice(Size size, Label label_id) {
        return this.shampooRepository.findAllBySizeOrLabelIdOrderByPrice(size, label_id.getId());
    }

    @Override
    public List<Shampoo> findAllByPriceHigherThan(BigDecimal price) {
        return this.shampooRepository.findAllByPriceGreaterThanOrderByPriceDesc(price);
    }

    @Override
    public long countByPriceLowerThan(BigDecimal price) {
        return this.shampooRepository.countByPriceLessThan(price);
    }

    @Override
    public List<Shampoo> findAllByIngredientNames(Collection<String> names) {
        return this.shampooRepository.findAllByIngredientNames(names);
    }

    @Override
    public List<Shampoo> findAllWithIngredientCountLessThan(int count) {
        return this.shampooRepository.findAllWithIngredientCountLessThan(count);
    }
}
