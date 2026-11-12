package com.example.exercisexmlprocessing.cardealer.dto;

import com.example.exercisexmlprocessing.util.LocalDateTimeAdapter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Seed shapes of suppliers.xml, parts.xml, cars.xml, customers.xml and the six Car Dealer query results. */
public final class XmlDtos {

    private XmlDtos() {
    }

    @XmlRootElement(name = "supplier")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SupplierSeed {
        @XmlAttribute
        @NotBlank
        public String name;
        @XmlAttribute(name = "is-importer")
        public boolean isImporter;
    }

    @XmlRootElement(name = "part")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PartSeed {
        @XmlAttribute
        @NotBlank
        public String name;
        @XmlAttribute
        @NotNull
        @Positive
        public BigDecimal price;
        @XmlAttribute
        @PositiveOrZero
        public Integer quantity;
    }

    @XmlRootElement(name = "car")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CarSeed {
        @NotBlank
        public String make;
        @NotBlank
        public String model;
        @XmlElement(name = "travelled-distance")
        @NotNull
        @PositiveOrZero
        public Long travelledDistance;
    }

    @XmlRootElement(name = "customer")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CustomerSeed {
        @XmlAttribute
        @NotBlank
        public String name;
        @XmlElement(name = "birth-date")
        @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
        @NotNull
        public LocalDateTime birthDate;
        @XmlElement(name = "is-young-driver")
        public boolean isYoungDriver;
    }

    /** Query 1 */
    @XmlRootElement(name = "customer")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class OrderedCustomer {
        public Long id;
        public String name;
        @XmlElement(name = "birth-date")
        @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
        public LocalDateTime birthDate;
        @XmlElement(name = "is-young-driver")
        public boolean isYoungDriver;

        public OrderedCustomer() {
        }

        public OrderedCustomer(Long id, String name, LocalDateTime birthDate, boolean isYoungDriver) {
            this.id = id;
            this.name = name;
            this.birthDate = birthDate;
            this.isYoungDriver = isYoungDriver;
        }
    }

    /** Query 2 */
    @XmlRootElement(name = "car")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ToyotaCar {
        @XmlAttribute
        public Long id;
        @XmlAttribute
        public String make;
        @XmlAttribute
        public String model;
        @XmlAttribute(name = "travelled-distance")
        public Long travelledDistance;

        public ToyotaCar() {
        }

        public ToyotaCar(Long id, String make, String model, Long travelledDistance) {
            this.id = id;
            this.make = make;
            this.model = model;
            this.travelledDistance = travelledDistance;
        }
    }

    /** Query 3 */
    @XmlRootElement(name = "supplier")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class LocalSupplier {
        @XmlAttribute
        public Long id;
        @XmlAttribute
        public String name;
        @XmlAttribute(name = "parts-count")
        public int partsCount;

        public LocalSupplier() {
        }

        public LocalSupplier(Long id, String name, int partsCount) {
            this.id = id;
            this.name = name;
            this.partsCount = partsCount;
        }
    }

    /** Query 4 */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PartInfo {
        @XmlAttribute
        public String name;
        @XmlAttribute
        public BigDecimal price;

        public PartInfo() {
        }

        public PartInfo(String name, BigDecimal price) {
            this.name = name;
            this.price = price;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CarInfo {
        @XmlAttribute
        public String make;
        @XmlAttribute
        public String model;
        @XmlAttribute(name = "travelled-distance")
        public Long travelledDistance;

        public CarInfo() {
        }

        public CarInfo(String make, String model, Long travelledDistance) {
            this.make = make;
            this.model = model;
            this.travelledDistance = travelledDistance;
        }
    }

    @XmlRootElement(name = "car")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CarWithParts extends CarInfo {
        @XmlElementWrapper(name = "parts")
        @XmlElement(name = "part")
        public List<PartInfo> parts;

        public CarWithParts() {
        }

        public CarWithParts(String make, String model, Long travelledDistance, List<PartInfo> parts) {
            super(make, model, travelledDistance);
            this.parts = parts;
        }
    }

    /** Query 5 */
    @XmlRootElement(name = "customer")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CustomerTotal {
        @XmlAttribute(name = "full-name")
        public String fullName;
        @XmlAttribute(name = "bought-cars")
        public int boughtCars;
        @XmlAttribute(name = "spent-money")
        public BigDecimal spentMoney;

        public CustomerTotal() {
        }

        public CustomerTotal(String fullName, int boughtCars, BigDecimal spentMoney) {
            this.fullName = fullName;
            this.boughtCars = boughtCars;
            this.spentMoney = spentMoney;
        }
    }

    /** Query 6 */
    @XmlRootElement(name = "sale")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SaleWithDiscount {
        public CarInfo car;
        @XmlElement(name = "customer-name")
        public String customerName;
        public BigDecimal discount;
        public BigDecimal price;
        @XmlElement(name = "price-with-discount")
        public BigDecimal priceWithDiscount;

        public SaleWithDiscount() {
        }

        public SaleWithDiscount(CarInfo car, String customerName, BigDecimal discount, BigDecimal price, BigDecimal priceWithDiscount) {
            this.car = car;
            this.customerName = customerName;
            this.discount = discount;
            this.price = price;
            this.priceWithDiscount = priceWithDiscount;
        }
    }
}
