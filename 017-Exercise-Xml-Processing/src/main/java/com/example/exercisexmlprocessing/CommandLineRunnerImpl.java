package com.example.exercisexmlprocessing;

import com.example.exercisexmlprocessing.cardealer.service.CarDealerService;
import com.example.exercisexmlprocessing.productshop.service.ProductShopService;
import com.example.exercisexmlprocessing.util.XmlIO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Seeds both databases and writes every query result to the output directory. */
@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    private final ProductShopService productShop;
    private final CarDealerService carDealer;
    private final XmlIO xml;

    public CommandLineRunnerImpl(ProductShopService productShop, CarDealerService carDealer, XmlIO xml) {
        this.productShop = productShop;
        this.carDealer = carDealer;
        this.xml = xml;
    }

    @Override
    public void run(String... args) {
        productShop.seed();
        carDealer.seed();

        System.out.println("Written:");
        System.out.println(xml.write("products-in-range.xml", xml.toXml("products", productShop.productsInRange())));
        System.out.println(xml.write("users-sold-products.xml", xml.toXml("users", productShop.successfullySoldProducts())));
        System.out.println(xml.write("categories-by-products.xml", xml.toXml("categories", productShop.categoriesByProductsCount())));
        System.out.println(xml.write("users-and-products.xml", xml.toXml(productShop.usersAndProducts())));

        System.out.println(xml.write("ordered-customers.xml", xml.toXml("customers", carDealer.orderedCustomers())));
        System.out.println(xml.write("toyota-cars.xml", xml.toXml("cars", carDealer.toyotaCars())));
        System.out.println(xml.write("local-suppliers.xml", xml.toXml("suppliers", carDealer.localSuppliers())));
        System.out.println(xml.write("cars-and-parts.xml", xml.toXml("cars", carDealer.carsWithParts())));
        System.out.println(xml.write("customers-total-sales.xml", xml.toXml("customers", carDealer.totalSalesByCustomer())));
        System.out.println(xml.write("sales-discounts.xml", xml.toXml("sales", carDealer.salesWithDiscount())));
    }
}
