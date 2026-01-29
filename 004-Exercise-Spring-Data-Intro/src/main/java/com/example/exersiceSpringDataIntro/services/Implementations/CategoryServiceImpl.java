package com.example.exersiceSpringDataIntro.services.Implementations;

import com.example.exersiceSpringDataIntro.models.Category;
import com.example.exersiceSpringDataIntro.repositories.CategoryRepository;
import com.example.exersiceSpringDataIntro.services.CategoryService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CategoryServiceImpl implements CategoryService {

    public static final String CATEGORIES_FILE_PATH = "src/main/resources/files/categories.txt";

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void seedCategories() throws IOException {
        if (categoryRepository.count() > 0) {
            return;
        }

        Files.readAllLines(Path.of(CATEGORIES_FILE_PATH)).stream().filter(row -> !row.isBlank()).forEach(categoryName -> {

            Category category = new Category(categoryName);

            categoryRepository.save(category);
        });
    }

    @Override
    public Set<Category> getRandomCategories() {
        Set<Category> categories = new HashSet<>();
        int rand = ThreadLocalRandom.current().nextInt(1, 3);

        long count = categoryRepository.count();

        for (int i = 0; i < rand; i++) {
            long randomId = ThreadLocalRandom.current().nextLong(1, count + 1);

            Category category = categoryRepository.findById((long) rand).orElse(null);

            categories.add(category);
        }
        return categories;
    }
}
