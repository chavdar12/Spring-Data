package com.example.exercisejsonprocessing;

import com.example.exercisejsonprocessing.cardealer.dto.QueryDtos.CustomerTotal;
import com.example.exercisejsonprocessing.cardealer.dto.QueryDtos.SaleWithDiscount;
import com.example.exercisejsonprocessing.cardealer.repository.*;
import com.example.exercisejsonprocessing.cardealer.service.CarDealerService;
import com.example.exercisejsonprocessing.productshop.dto.QueryDtos.*;
import com.example.exercisejsonprocessing.productshop.repository.CategoryRepository;
import com.example.exercisejsonprocessing.productshop.repository.ProductRepository;
import com.example.exercisejsonprocessing.productshop.repository.UserRepository;
import com.example.exercisejsonprocessing.productshop.service.ProductShopService;
import com.example.exercisejsonprocessing.util.JsonIO;
import com.example.exercisejsonprocessing.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ProductShopService.class, CarDealerService.class, JsonIO.class, ValidationUtil.class, QueriesTest.SeededRandom.class})
class QueriesTest {

    @TestConfiguration
    static class SeededRandom {
        @Bean
        Random random() {
            return new Random(42);
        }
    }

    @Autowired
    private ProductShopService productShop;
    @Autowired
    private CarDealerService carDealer;
    @Autowired
    private JsonIO json;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private PartRepository partRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SaleRepository saleRepository;

    @BeforeEach
    void seed() {
        productShop.seed();
        carDealer.seed();
        productShop.seed(); // idempotent
        carDealer.seed();
    }

    @Test
    void seedsEveryRecordOfTheDatasets() {
        assertThat(userRepository.count()).isEqualTo(56);
        assertThat(categoryRepository.count()).isEqualTo(11);
        assertThat(productRepository.count()).isEqualTo(200);
        assertThat(supplierRepository.count()).isEqualTo(31);
        assertThat(partRepository.count()).isEqualTo(131);
        assertThat(carRepository.count()).isEqualTo(358);
        assertThat(customerRepository.count()).isEqualTo(30);
        assertThat(saleRepository.count()).isEqualTo(30);
    }

    @Test
    void productsInRangeAreUnsoldAndCheapestFirst() {
        assertThat(productShop.productsInRange()).isNotEmpty()
                .allSatisfy(p -> assertThat(p.price()).isBetween(BigDecimal.valueOf(500), BigDecimal.valueOf(1000)))
                .isSortedAccordingTo(Comparator.comparing(ProductInRange::price));
        assertThat(productRepository.findAllByPriceBetweenAndBuyerIsNullOrderByPrice(BigDecimal.valueOf(500), BigDecimal.valueOf(1000)))
                .allSatisfy(p -> assertThat(p.getBuyer()).isNull());
    }

    @Test
    void soldProductsAlwaysHaveABuyerAndUsersAreByName() {
        assertThat(productShop.successfullySoldProducts()).isNotEmpty()
                .isSortedAccordingTo(Comparator.comparing(UserWithSoldProducts::lastName).thenComparing(UserWithSoldProducts::firstName,
                        Comparator.nullsFirst(Comparator.naturalOrder())))
                .allSatisfy(u -> assertThat(u.soldProducts()).isNotEmpty().allSatisfy(p -> assertThat(p.buyerLastName()).isNotNull()));
    }

    @Test
    void categoryStatsAddUp() {
        var stats = productShop.categoriesByProductsCount();
        assertThat(stats).isSortedAccordingTo(Comparator.comparingLong(CategoryStats::productsCount).reversed());
        assertThat(stats).allSatisfy(c -> assertThat(c.averagePrice().multiply(BigDecimal.valueOf(c.productsCount())))
                .isCloseTo(c.totalRevenue(), org.assertj.core.data.Offset.offset(new BigDecimal("0.01"))));
    }

    @Test
    void usersAndProductsCountsMatch() {
        UsersAndProducts result = productShop.usersAndProducts();
        assertThat(result.usersCount()).isEqualTo(result.users().size()).isPositive();
        assertThat(result.users()).isSortedAccordingTo(Comparator.comparingInt((UserDetails u) -> u.soldProducts().count()).reversed())
                .allSatisfy(u -> assertThat(u.soldProducts().products()).hasSize(u.soldProducts().count()));
    }

    @Test
    void carDealerQueries() {
        assertThat(carDealer.orderedCustomers()).hasSize(30).allSatisfy(c -> assertThat(c.sales()).hasSize(1));
        assertThat(carDealer.toyotaCars()).isNotEmpty().allSatisfy(c -> assertThat(c.make()).isEqualTo("Toyota"));
        assertThat(carDealer.localSuppliers()).isNotEmpty()
                .allSatisfy(s -> assertThat(supplierRepository.findById(s.id()).orElseThrow().getIsImporter()).isFalse());
        assertThat(carDealer.carsWithParts()).hasSize(358).allSatisfy(c -> assertThat(c.parts()).hasSizeBetween(3, 5));
        assertThat(carDealer.totalSalesByCustomer()).hasSize(30)
                .isSortedAccordingTo(Comparator.comparing(CustomerTotal::spentMoney).reversed());
        assertThat(carDealer.salesWithDiscount()).hasSize(30).allSatisfy((SaleWithDiscount s) -> assertThat(s.priceWithDiscount())
                .isEqualByComparingTo(s.price().multiply(BigDecimal.ONE.subtract(s.discount())).setScale(2, RoundingMode.HALF_UP)));
    }

    @Test
    void jsonUsesTheTaskFieldNames() {
        assertThat(json.toJson(carDealer.toyotaCars().getFirst())).contains("\"Id\":", "\"make\": \"Toyota\"", "\"travelledDistance\":");
        assertThat(json.toJson(carDealer.orderedCustomers().getFirst())).containsPattern("\"birthDate\": \"\\d{4}-\\d{2}-\\d{2}T");
    }
}
