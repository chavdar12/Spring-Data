package com.example.exercisejsonprocessing.cardealer.repository;

import com.example.exercisejsonprocessing.cardealer.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}
