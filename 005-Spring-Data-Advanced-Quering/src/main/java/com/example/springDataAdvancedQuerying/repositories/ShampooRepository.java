package com.example.springDataAdvancedQuerying.repositories;

import com.example.springDataAdvancedQuerying.entities.Shampoo;
import com.example.springDataAdvancedQuerying.entities.Size;

import java.util.List;

public interface ShampooRepository extends BaseRepository<Shampoo> {
    List<Shampoo> findAllBySizeOrderById(Size size);

    List<Shampoo> findAllBySizeOrLabelIdOrderByPrice(Size size, Long labelIDd);
}