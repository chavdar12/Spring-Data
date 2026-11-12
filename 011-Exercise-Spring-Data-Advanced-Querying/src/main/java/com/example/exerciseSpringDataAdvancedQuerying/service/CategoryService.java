package com.example.exerciseSpringDataAdvancedQuerying.service;

import com.example.exerciseSpringDataAdvancedQuerying.model.entity.Category;

import java.io.IOException;
import java.util.Set;

public interface CategoryService {
    void seedCategories() throws IOException;

    Set<Category> getRandomCategories();
}
