package com.example.exersiceSpringDataIntro.repositories;

import com.example.exersiceSpringDataIntro.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}