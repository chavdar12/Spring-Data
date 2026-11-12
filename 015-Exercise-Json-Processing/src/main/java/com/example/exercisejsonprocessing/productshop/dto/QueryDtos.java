package com.example.exercisejsonprocessing.productshop.dto;

import java.math.BigDecimal;
import java.util.List;

/** Shapes of the four Products Shop query results. */
public final class QueryDtos {

    private QueryDtos() {
    }

    /** Query 1 */
    public record ProductInRange(String name, BigDecimal price, String seller) {
    }

    /** Query 2 */
    public record SoldProduct(String name, BigDecimal price, String buyerFirstName, String buyerLastName) {
    }

    public record UserWithSoldProducts(String firstName, String lastName, List<SoldProduct> soldProducts) {
    }

    /** Query 3 */
    public record CategoryStats(String category, long productsCount, BigDecimal averagePrice, BigDecimal totalRevenue) {
    }

    /** Query 4 */
    public record ProductNamePrice(String name, BigDecimal price) {
    }

    public record SoldProducts(int count, List<ProductNamePrice> products) {
    }

    public record UserDetails(String firstName, String lastName, Integer age, SoldProducts soldProducts) {
    }

    public record UsersAndProducts(int usersCount, List<UserDetails> users) {
    }
}
