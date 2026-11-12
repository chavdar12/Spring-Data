package com.example.exerciseSpringDataAdvancedQuerying.service.impl;

import com.example.exerciseSpringDataAdvancedQuerying.model.entity.Category;
import com.example.exerciseSpringDataAdvancedQuerying.repository.CategoryRepository;
import com.example.exerciseSpringDataAdvancedQuerying.service.CategoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final String CATEGORIES_FILE_PATH = "files/categories.txt";

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void seedCategories() throws IOException {
        if (categoryRepository.count() > 0) {
            return;
        }

        new ClassPathResource(CATEGORIES_FILE_PATH).getContentAsString(StandardCharsets.UTF_8).lines().filter(row -> !row.isBlank()).forEach(categoryName -> {
            Category category = new Category(categoryName);

            categoryRepository.save(category);
        });
    }

    @Override
    public Set<Category> getRandomCategories() {
        Set<Category> categories = new HashSet<>();
        int randInt = ThreadLocalRandom.current().nextInt(1, 3);
        long catRepoCount = categoryRepository.count();

        for (int i = 0; i < randInt; i++) {
            long randomIndex = ThreadLocalRandom.current().nextLong(catRepoCount);

            categoryRepository.findAll(PageRequest.of((int) randomIndex, 1)).stream().findFirst().ifPresent(categories::add);
        }

        return categories;

    }
}
