package com.example.exercisejsonprocessing;

import com.example.exercisejsonprocessing.cardealer.service.CarDealerService;
import com.example.exercisejsonprocessing.productshop.service.ProductShopService;
import com.example.exercisejsonprocessing.util.JsonIO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Seeds both databases and writes every query result to the output directory. */
@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    private final ProductShopService productShop;
    private final CarDealerService carDealer;
    private final JsonIO json;

    public CommandLineRunnerImpl(ProductShopService productShop, CarDealerService carDealer, JsonIO json) {
        this.productShop = productShop;
        this.carDealer = carDealer;
        this.json = json;
    }

    @Override
    public void run(String... args) {
        productShop.seed();
        carDealer.seed();

        System.out.println("Written:");
        System.out.println(json.write("products-in-range.json", productShop.productsInRange()));
        System.out.println(json.write("users-sold-products.json", productShop.successfullySoldProducts()));
        System.out.println(json.write("categories-by-products.json", productShop.categoriesByProductsCount()));
        System.out.println(json.write("users-and-products.json", productShop.usersAndProducts()));

        System.out.println(json.write("ordered-customers.json", carDealer.orderedCustomers()));
        System.out.println(json.write("toyota-cars.json", carDealer.toyotaCars()));
        System.out.println(json.write("local-suppliers.json", carDealer.localSuppliers()));
        System.out.println(json.write("cars-and-parts.json", carDealer.carsWithParts()));
        System.out.println(json.write("customers-total-sales.json", carDealer.totalSalesByCustomer()));
        System.out.println(json.write("sales-discounts.json", carDealer.salesWithDiscount()));
    }
}
