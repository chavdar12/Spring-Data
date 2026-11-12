package com.example.exercisejsonprocessing.cardealer.repository;

import com.example.exercisejsonprocessing.cardealer.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findAllByMakeOrderByModelAscTravelledDistanceDesc(String make);
}
