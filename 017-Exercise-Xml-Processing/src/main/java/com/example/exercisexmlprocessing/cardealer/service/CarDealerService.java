package com.example.exercisexmlprocessing.cardealer.service;

import com.example.exercisexmlprocessing.cardealer.dto.XmlDtos.*;
import com.example.exercisexmlprocessing.cardealer.entity.*;
import com.example.exercisexmlprocessing.cardealer.repository.*;
import com.example.exercisexmlprocessing.util.XmlIO;
import com.example.exercisexmlprocessing.util.ValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
public class CarDealerService {

    /** Discounts the task allows, as fractions of the price. */
    static final List<BigDecimal> DISCOUNTS = List.of("0", "0.05", "0.10", "0.15", "0.20", "0.30", "0.40", "0.50")
            .stream().map(BigDecimal::new).toList();

    private final SupplierRepository supplierRepository;
    private final PartRepository partRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final SaleRepository saleRepository;
    private final XmlIO xml;
    private final ValidationUtil validation;
    private final Random random;

    public CarDealerService(SupplierRepository supplierRepository, PartRepository partRepository, CarRepository carRepository,
                            CustomerRepository customerRepository, SaleRepository saleRepository, XmlIO xml,
                            ValidationUtil validation, Random random) {
        this.supplierRepository = supplierRepository;
        this.partRepository = partRepository;
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
        this.saleRepository = saleRepository;
        this.xml = xml;
        this.validation = validation;
        this.random = random;
    }

    /** Seeds everything; parts get a random supplier, cars 3-5 random parts, each customer buys one random car. */
    @Transactional
    public void seed() {
        if (supplierRepository.count() > 0) {
            return;
        }

        for (SupplierSeed seed : xml.readList("files/suppliers.xml", SupplierSeed.class)) {
            if (validation.isValid(seed)) {
                Supplier supplier = new Supplier();
                supplier.setName(seed.name);
                supplier.setIsImporter(seed.isImporter);
                supplierRepository.save(supplier);
            }
        }
        List<Supplier> suppliers = supplierRepository.findAll();
        for (PartSeed seed : xml.readList("files/parts.xml", PartSeed.class)) {
            if (validation.isValid(seed)) {
                Part part = new Part();
                part.setName(seed.name);
                part.setPrice(seed.price);
                part.setQuantity(seed.quantity);
                part.setSupplier(pick(suppliers));
                partRepository.save(part);
                part.getSupplier().getParts().add(part);
            }
        }
        List<Part> parts = partRepository.findAll();
        for (CarSeed seed : xml.readList("files/cars.xml", CarSeed.class)) {
            if (validation.isValid(seed)) {
                Car car = new Car();
                car.setMake(seed.make);
                car.setModel(seed.model);
                car.setTravelledDistance(seed.travelledDistance);
                int partCount = 3 + random.nextInt(3);
                while (car.getParts().size() < partCount) {
                    car.getParts().add(pick(parts));
                }
                carRepository.save(car);
            }
        }
        List<Car> cars = carRepository.findAll();
        for (CustomerSeed seed : xml.readList("files/customers.xml", CustomerSeed.class)) {
            if (!validation.isValid(seed)) {
                continue;
            }
            Customer customer = new Customer();
            customer.setName(seed.name);
            customer.setBirthDate(seed.birthDate);
            customer.setIsYoungDriver(seed.isYoungDriver);
            customerRepository.save(customer);

            Sale sale = new Sale();
            sale.setCustomer(customer);
            sale.setCar(pick(cars));
            sale.setDiscount(pick(DISCOUNTS));
            saleRepository.save(sale);
            customer.getSales().add(sale);
        }
    }

    private <T> T pick(List<T> items) {
        return items.get(random.nextInt(items.size()));
    }

    /** Query 1: customers by birth date, experienced drivers first on equal dates. */
    @Transactional(readOnly = true)
    public List<OrderedCustomer> orderedCustomers() {
        return customerRepository.findAllByOrderByBirthDateAscIsYoungDriverAsc().stream()
                .map(c -> new OrderedCustomer(c.getId(), c.getName(), c.getBirthDate(), c.getIsYoungDriver()))
                .toList();
    }

    /** Query 2: Toyota cars by model, then by travelled distance descending. */
    @Transactional(readOnly = true)
    public List<ToyotaCar> toyotaCars() {
        return carRepository.findAllByMakeOrderByModelAscTravelledDistanceDesc("Toyota").stream()
                .map(c -> new ToyotaCar(c.getId(), c.getMake(), c.getModel(), c.getTravelledDistance()))
                .toList();
    }

    /** Query 3: suppliers that do not import parts. */
    @Transactional(readOnly = true)
    public List<LocalSupplier> localSuppliers() {
        return supplierRepository.findAllByIsImporterFalseOrderById().stream()
                .map(s -> new LocalSupplier(s.getId(), s.getName(), s.getParts().size()))
                .toList();
    }

    /** Query 4: every car with its parts. */
    @Transactional(readOnly = true)
    public List<CarWithParts> carsWithParts() {
        return carRepository.findAll().stream()
                .map(c -> new CarWithParts(c.getMake(), c.getModel(), c.getTravelledDistance(), c.getParts().stream()
                        .sorted(Comparator.comparing(Part::getId))
                        .map(p -> new PartInfo(p.getName(), p.getPrice()))
                        .toList()))
                .toList();
    }

    /** Query 5: customers who bought a car, by money spent (after discount), then by cars bought. */
    @Transactional(readOnly = true)
    public List<CustomerTotal> totalSalesByCustomer() {
        return customerRepository.findAllBySalesIsNotEmpty().stream()
                .map(c -> new CustomerTotal(c.getName(), c.getSales().size(),
                        c.getSales().stream().map(CarDealerService::priceWithDiscount).reduce(BigDecimal.ZERO, BigDecimal::add)))
                .sorted(Comparator.comparing((CustomerTotal t) -> t.spentMoney).reversed()
                        .thenComparing(Comparator.comparingInt((CustomerTotal t) -> t.boughtCars).reversed()))
                .toList();
    }

    /** Query 6: every sale with and without its discount. */
    @Transactional(readOnly = true)
    public List<SaleWithDiscount> salesWithDiscount() {
        return saleRepository.findAll().stream()
                .map(s -> new SaleWithDiscount(carInfo(s.getCar()), s.getCustomer().getName(), s.getDiscount(),
                        price(s.getCar()), priceWithDiscount(s)))
                .toList();
    }

    static BigDecimal price(Car car) {
        return car.getParts().stream().map(Part::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    static BigDecimal priceWithDiscount(Sale sale) {
        return price(sale.getCar()).multiply(BigDecimal.ONE.subtract(sale.getDiscount())).setScale(2, RoundingMode.HALF_UP);
    }

    private static CarInfo carInfo(Car car) {
        return new CarInfo(car.getMake(), car.getModel(), car.getTravelledDistance());
    }
}
