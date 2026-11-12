package com.example.exercisexmlprocessing.productshop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/** Seed shapes of users.xml, products.xml, categories.xml and the four Products Shop query results. */
public final class XmlDtos {

    private XmlDtos() {
    }

    @XmlRootElement(name = "user")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class UserSeed {
        @XmlAttribute(name = "first-name")
        public String firstName;
        @XmlAttribute(name = "last-name")
        @NotNull
        @Size(min = 3)
        public String lastName;
        @XmlAttribute
        public Integer age;
    }

    @XmlRootElement(name = "product")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ProductSeed {
        @NotNull
        @Size(min = 3)
        public String name;
        @NotNull
        @Positive
        public BigDecimal price;
    }

    @XmlRootElement(name = "category")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CategorySeed {
        @NotNull
        @Size(min = 3, max = 15)
        public String name;
    }

    /** Query 1 */
    @XmlRootElement(name = "product")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ProductInRange {
        public String name;
        public BigDecimal price;
        public String seller;

        public ProductInRange() {
        }

        public ProductInRange(String name, BigDecimal price, String seller) {
            this.name = name;
            this.price = price;
            this.seller = seller;
        }
    }

    /** Query 2 */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SoldProduct {
        @XmlAttribute(name = "buyer-first-name")
        public String buyerFirstName;
        @XmlAttribute(name = "buyer-last-name")
        public String buyerLastName;
        public String name;
        public BigDecimal price;

        public SoldProduct() {
        }

        public SoldProduct(String name, BigDecimal price, String buyerFirstName, String buyerLastName) {
            this.name = name;
            this.price = price;
            this.buyerFirstName = buyerFirstName;
            this.buyerLastName = buyerLastName;
        }
    }

    @XmlRootElement(name = "user")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class UserWithSoldProducts {
        @XmlAttribute(name = "first-name")
        public String firstName;
        @XmlAttribute(name = "last-name")
        public String lastName;
        @XmlElementWrapper(name = "sold-products")
        @XmlElement(name = "product")
        public List<SoldProduct> soldProducts;

        public UserWithSoldProducts() {
        }

        public UserWithSoldProducts(String firstName, String lastName, List<SoldProduct> soldProducts) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.soldProducts = soldProducts;
        }
    }

    /** Query 3 */
    @XmlRootElement(name = "category")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CategoryStats {
        @XmlAttribute
        public String name;
        @XmlAttribute(name = "products-count")
        public long productsCount;
        @XmlAttribute(name = "average-price")
        public BigDecimal averagePrice;
        @XmlAttribute(name = "total-revenue")
        public BigDecimal totalRevenue;

        public CategoryStats() {
        }

        public CategoryStats(String name, long productsCount, BigDecimal averagePrice, BigDecimal totalRevenue) {
            this.name = name;
            this.productsCount = productsCount;
            this.averagePrice = averagePrice;
            this.totalRevenue = totalRevenue;
        }
    }

    /** Query 4 */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ProductNamePrice {
        @XmlAttribute
        public String name;
        @XmlAttribute
        public BigDecimal price;

        public ProductNamePrice() {
        }

        public ProductNamePrice(String name, BigDecimal price) {
            this.name = name;
            this.price = price;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SoldProducts {
        @XmlAttribute
        public int count;
        @XmlElement(name = "product")
        public List<ProductNamePrice> products;

        public SoldProducts() {
        }

        public SoldProducts(List<ProductNamePrice> products) {
            this.count = products.size();
            this.products = products;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class UserDetails {
        @XmlAttribute(name = "first-name")
        public String firstName;
        @XmlAttribute(name = "last-name")
        public String lastName;
        @XmlAttribute
        public Integer age;
        @XmlElement(name = "sold-products")
        public SoldProducts soldProducts;

        public UserDetails() {
        }

        public UserDetails(String firstName, String lastName, Integer age, SoldProducts soldProducts) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.soldProducts = soldProducts;
        }
    }

    @XmlRootElement(name = "users")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class UsersAndProducts {
        @XmlAttribute
        public int count;
        @XmlElement(name = "user")
        public List<UserDetails> users;

        public UsersAndProducts() {
        }

        public UsersAndProducts(List<UserDetails> users) {
            this.count = users.size();
            this.users = users;
        }
    }
}
