package com.example.springDataAdvancedQuerying.services;

import com.example.springDataAdvancedQuerying.entities.Label;
import com.example.springDataAdvancedQuerying.entities.Shampoo;
import com.example.springDataAdvancedQuerying.entities.Size;
import com.example.springDataAdvancedQuerying.repositories.ShampooRepository;
import org.springframework.stereotype.Service;

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
}
