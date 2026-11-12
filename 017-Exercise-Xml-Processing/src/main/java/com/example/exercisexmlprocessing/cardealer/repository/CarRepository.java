package com.example.exercisexmlprocessing.cardealer.repository;

import com.example.exercisexmlprocessing.cardealer.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findAllByMakeOrderByModelAscTravelledDistanceDesc(String make);
}
