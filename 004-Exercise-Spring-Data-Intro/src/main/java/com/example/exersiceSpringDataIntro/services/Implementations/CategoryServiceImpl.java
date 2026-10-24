package com.example.exersiceSpringDataIntro.services.Implementations;

import com.example.exersiceSpringDataIntro.models.Category;
import com.example.exersiceSpringDataIntro.repositories.CategoryRepository;
import com.example.exersiceSpringDataIntro.services.CategoryService;
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

    public static final String CATEGORIES_FILE_PATH = "files/categories.txt";

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
        int rand = ThreadLocalRandom.current().nextInt(1, 3);

        long count = categoryRepository.count();

        for (int i = 0; i < rand; i++) {
            long randomIndex = ThreadLocalRandom.current().nextLong(count);

            categoryRepository.findAll(PageRequest.of((int) randomIndex, 1)).stream().findFirst().ifPresent(categories::add);
        }
        return categories;
    }
}
