package com.example.exercisejsonprocessing.cardealer.dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Shapes of the six Car Dealer query results. */
public final class QueryDtos {

    private QueryDtos() {
    }

    public record CarInfo(String make, String model, Long travelledDistance) {
    }

    /** Query 1 */
    public record CustomerSale(CarInfo car, BigDecimal discount) {
    }

    public record OrderedCustomer(@SerializedName("Id") Long id, String name, LocalDateTime birthDate, boolean isYoungDriver,
                                  List<CustomerSale> sales) {
    }

    /** Query 2 */
    public record ToyotaCar(@SerializedName("Id") Long id, String make, String model, Long travelledDistance) {
    }

    /** Query 3 */
    public record LocalSupplier(@SerializedName("Id") Long id, String name, int partsCount) {
    }

    /** Query 4 */
    public record PartInfo(String name, BigDecimal price) {
    }

    public record CarWithParts(CarInfo car, List<PartInfo> parts) {
    }

    /** Query 5 */
    public record CustomerTotal(String fullName, int boughtCars, BigDecimal spentMoney) {
    }

    /** Query 6 */
    public record SaleWithDiscount(CarInfo car, String customerName, BigDecimal discount, BigDecimal price,
                                   BigDecimal priceWithDiscount) {
    }
}
