package com.example.exercisejsonprocessing.productshop.service;

import com.example.exercisejsonprocessing.productshop.dto.QueryDtos.*;
import com.example.exercisejsonprocessing.productshop.dto.SeedDtos.CategorySeed;
import com.example.exercisejsonprocessing.productshop.dto.SeedDtos.ProductSeed;
import com.example.exercisejsonprocessing.productshop.dto.SeedDtos.UserSeed;
import com.example.exercisejsonprocessing.productshop.entity.Category;
import com.example.exercisejsonprocessing.productshop.entity.Product;
import com.example.exercisejsonprocessing.productshop.entity.User;
import com.example.exercisejsonprocessing.productshop.repository.CategoryRepository;
import com.example.exercisejsonprocessing.productshop.repository.ProductRepository;
import com.example.exercisejsonprocessing.productshop.repository.UserRepository;
import com.example.exercisejsonprocessing.util.JsonIO;
import com.example.exercisejsonprocessing.util.ValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ProductShopService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final JsonIO json;
    private final ValidationUtil validation;
    private final Random random;

    public ProductShopService(UserRepository userRepository, ProductRepository productRepository,
                              CategoryRepository categoryRepository, JsonIO json, ValidationUtil validation, Random random) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.json = json;
        this.validation = validation;
        this.random = random;
    }

    /** Seeds users, categories and products, giving each product a random seller, maybe a buyer, and 1-3 categories. */
    @Transactional
    public void seed() {
        if (userRepository.count() > 0) {
            return;
        }

        for (UserSeed seed : json.read("files/users.json", UserSeed[].class)) {
            if (validation.isValid(seed)) {
                User user = new User();
                user.setFirstName(seed.firstName());
                user.setLastName(seed.lastName());
                user.setAge(seed.age());
                userRepository.save(user);
            }
        }
        for (CategorySeed seed : json.read("files/categories.json", CategorySeed[].class)) {
            if (validation.isValid(seed)) {
                Category category = new Category();
                category.setName(seed.name());
                categoryRepository.save(category);
            }
        }

        List<User> users = userRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        for (ProductSeed seed : json.read("files/products.json", ProductSeed[].class)) {
            if (!validation.isValid(seed)) {
                continue;
            }
            Product product = new Product();
            product.setName(seed.name());
            product.setPrice(seed.price());
            product.setSeller(pick(users));
            // about a third of the products stay unsold
            User buyer = random.nextInt(3) == 0 ? null : pick(users);
            product.setBuyer(buyer == product.getSeller() ? null : buyer);
            for (int i = random.nextInt(3); i >= 0; i--) {
                product.getCategories().add(pick(categories));
            }
            productRepository.save(product);
            // keep the inverse sides in sync for queries in this same session
            product.getSeller().getSoldProducts().add(product);
            if (product.getBuyer() != null) {
                product.getBuyer().getBoughtProducts().add(product);
            }
            product.getCategories().forEach(c -> c.getProducts().add(product));
        }
    }

    private <T> T pick(List<T> items) {
        return items.get(random.nextInt(items.size()));
    }

    /** Query 1: products priced 500-1000 that nobody bought, cheapest first. */
    @Transactional(readOnly = true)
    public List<ProductInRange> productsInRange() {
        return productRepository.findAllByPriceBetweenAndBuyerIsNullOrderByPrice(BigDecimal.valueOf(500), BigDecimal.valueOf(1000))
                .stream()
                .map(p -> new ProductInRange(p.getName(), p.getPrice(), fullName(p.getSeller())))
                .toList();
    }

    /** Query 2: users with at least one product sold to a buyer. */
    @Transactional(readOnly = true)
    public List<UserWithSoldProducts> successfullySoldProducts() {
        return userRepository.findAllWithSoldProducts().stream()
                .map(u -> new UserWithSoldProducts(u.getFirstName(), u.getLastName(), sold(u).stream()
                        .map(p -> new SoldProduct(p.getName(), p.getPrice(), p.getBuyer().getFirstName(), p.getBuyer().getLastName()))
                        .toList()))
                .toList();
    }

    /** Query 3: categories by number of products. */
    @Transactional(readOnly = true)
    public List<CategoryStats> categoriesByProductsCount() {
        return categoryRepository.findCategoryStats().stream()
                .map(row -> new CategoryStats((String) row[0], (Long) row[1],
                        BigDecimal.valueOf(((Number) row[2]).doubleValue()).setScale(6, RoundingMode.HALF_UP),
                        (BigDecimal) row[3]))
                .toList();
    }

    /** Query 4: sellers by number of sold products, then by last name. */
    @Transactional(readOnly = true)
    public UsersAndProducts usersAndProducts() {
        List<UserDetails> users = userRepository.findAllWithSoldProducts().stream()
                .sorted(Comparator.comparingInt((User u) -> sold(u).size()).reversed().thenComparing(User::getLastName))
                .map(u -> {
                    List<ProductNamePrice> products = sold(u).stream().map(p -> new ProductNamePrice(p.getName(), p.getPrice())).toList();
                    return new UserDetails(u.getFirstName(), u.getLastName(), u.getAge(), new SoldProducts(products.size(), products));
                })
                .toList();
        return new UsersAndProducts(users.size(), users);
    }

    private static List<Product> sold(User user) {
        return user.getSoldProducts().stream()
                .filter(p -> p.getBuyer() != null)
                .sorted(Comparator.comparing(Product::getId))
                .toList();
    }

    private static String fullName(User user) {
        return user.getFirstName() == null ? user.getLastName() : user.getFirstName() + " " + user.getLastName();
    }
}
