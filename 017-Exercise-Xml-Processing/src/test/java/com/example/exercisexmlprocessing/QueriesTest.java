package com.example.exercisexmlprocessing;

import com.example.exercisexmlprocessing.cardealer.dto.XmlDtos.CustomerTotal;
import com.example.exercisexmlprocessing.cardealer.repository.*;
import com.example.exercisexmlprocessing.cardealer.service.CarDealerService;
import com.example.exercisexmlprocessing.productshop.dto.XmlDtos.ProductInRange;
import com.example.exercisexmlprocessing.productshop.dto.XmlDtos.UsersAndProducts;
import com.example.exercisexmlprocessing.productshop.repository.CategoryRepository;
import com.example.exercisexmlprocessing.productshop.repository.ProductRepository;
import com.example.exercisexmlprocessing.productshop.repository.UserRepository;
import com.example.exercisexmlprocessing.productshop.service.ProductShopService;
import com.example.exercisexmlprocessing.util.ValidationUtil;
import com.example.exercisexmlprocessing.util.XmlIO;
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
@Import({ProductShopService.class, CarDealerService.class, XmlIO.class, ValidationUtil.class, QueriesTest.SeededRandom.class})
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
    private XmlIO xml;
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
    void seedsEveryRecordOfTheXmlDatasets() {
        assertThat(userRepository.count()).isEqualTo(56);
        assertThat(categoryRepository.count()).isEqualTo(11);
        assertThat(productRepository.count()).isEqualTo(200);
        assertThat(supplierRepository.count()).isEqualTo(31);
        assertThat(partRepository.count()).isEqualTo(131);
        assertThat(carRepository.count()).isEqualTo(358);
        assertThat(customerRepository.count()).isEqualTo(30);
        assertThat(saleRepository.count()).isEqualTo(30);
        assertThat(userRepository.findAll()).anySatisfy(u -> assertThat(u.getFirstName()).isEqualTo("Eugene"));
    }

    @Test
    void productShopQueries() {
        assertThat(productShop.productsInRange()).isNotEmpty()
                .allSatisfy(p -> assertThat(p.price).isBetween(BigDecimal.valueOf(500), BigDecimal.valueOf(1000)))
                .isSortedAccordingTo(Comparator.comparing((ProductInRange p) -> p.price));
        assertThat(productShop.successfullySoldProducts()).isNotEmpty()
                .allSatisfy(u -> assertThat(u.soldProducts).isNotEmpty().allSatisfy(p -> assertThat(p.buyerLastName).isNotNull()));
        assertThat(productShop.categoriesByProductsCount()).hasSize(11);
        UsersAndProducts users = productShop.usersAndProducts();
        assertThat(users.count).isEqualTo(users.users.size()).isPositive();
    }

    @Test
    void carDealerQueries() {
        assertThat(carDealer.orderedCustomers()).hasSize(30);
        assertThat(carDealer.toyotaCars()).isNotEmpty().allSatisfy(c -> assertThat(c.make).isEqualTo("Toyota"));
        assertThat(carDealer.localSuppliers()).isNotEmpty();
        assertThat(carDealer.carsWithParts()).hasSize(358).allSatisfy(c -> assertThat(c.parts).hasSizeBetween(3, 5));
        assertThat(carDealer.totalSalesByCustomer()).hasSize(30)
                .isSortedAccordingTo(Comparator.comparing((CustomerTotal t) -> t.spentMoney).reversed());
        assertThat(carDealer.salesWithDiscount()).allSatisfy(s -> assertThat(s.priceWithDiscount)
                .isEqualByComparingTo(s.price.multiply(BigDecimal.ONE.subtract(s.discount)).setScale(2, RoundingMode.HALF_UP)));
    }

    @Test
    void xmlUsesTheTaskShapes() {
        assertThat(xml.toXml("cars", carDealer.toyotaCars())).contains("<cars>").containsPattern("<car id=\"\\d+\" make=\"Toyota\"");
        assertThat(xml.toXml(productShop.usersAndProducts())).containsPattern("<users count=\"\\d+\">")
                .contains("<sold-products count=");
        assertThat(xml.toXml("users", productShop.successfullySoldProducts())).contains("<sold-products>", "buyer-first-name=");
        assertThat(xml.toXml("cars", carDealer.carsWithParts())).contains("<parts>", "<part name=");
        assertThat(xml.toXml("customers", carDealer.orderedCustomers())).containsPattern("<birth-date>\\d{4}-\\d{2}-\\d{2}T");
    }
}
