package com.example.exercisexmlprocessing.cardealer.repository;

import com.example.exercisexmlprocessing.cardealer.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}
