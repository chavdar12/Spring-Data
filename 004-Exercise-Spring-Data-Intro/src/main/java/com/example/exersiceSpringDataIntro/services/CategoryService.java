package com.example.exersiceSpringDataIntro.services;

import com.example.exersiceSpringDataIntro.models.Category;

import java.io.IOException;
import java.util.Set;

public interface CategoryService {
    void seedCategories() throws IOException;

    Set<Category> getRandomCategories();
}
