package com.example.exerciseSpringDataIntro.repositories;

import com.example.exerciseSpringDataIntro.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}