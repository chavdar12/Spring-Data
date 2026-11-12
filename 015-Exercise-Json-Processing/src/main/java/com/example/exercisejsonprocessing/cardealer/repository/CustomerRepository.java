package com.example.exercisejsonprocessing.cardealer.repository;

import com.example.exercisejsonprocessing.cardealer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findAllByOrderByBirthDateAscIsYoungDriverAsc();

    List<Customer> findAllBySalesIsNotEmpty();
}
