package com.example.exerciseSpringDataIntro.services;

import com.example.exerciseSpringDataIntro.models.Category;

import java.io.IOException;
import java.util.Set;

public interface CategoryService {
    void seedCategories() throws IOException;

    Set<Category> getRandomCategories();
}
