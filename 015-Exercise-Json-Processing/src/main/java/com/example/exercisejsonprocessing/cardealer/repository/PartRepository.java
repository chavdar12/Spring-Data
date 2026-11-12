package com.example.exercisejsonprocessing.cardealer.repository;

import com.example.exercisejsonprocessing.cardealer.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<Part, Long> {
}
