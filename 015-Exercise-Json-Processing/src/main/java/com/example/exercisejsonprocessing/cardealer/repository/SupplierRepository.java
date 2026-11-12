package com.example.exercisejsonprocessing.cardealer.repository;

import com.example.exercisejsonprocessing.cardealer.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findAllByIsImporterFalseOrderById();
}
