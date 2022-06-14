package com.example.springDataAdvancedQuerying.services;

import com.example.springDataAdvancedQuerying.entities.Label;
import com.example.springDataAdvancedQuerying.entities.Shampoo;
import com.example.springDataAdvancedQuerying.entities.Size;

import java.util.List;

public interface ShampooServices {
    List<Shampoo> findAllBySizeOrderById(Size size);

    List<Shampoo> findAllBySizeOrLabelIdOrderByPrice(Size size, Label label_id);

}
