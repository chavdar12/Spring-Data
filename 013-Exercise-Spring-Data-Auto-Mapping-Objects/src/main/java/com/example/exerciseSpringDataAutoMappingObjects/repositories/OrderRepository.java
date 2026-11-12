package com.example.exerciseSpringDataAutoMappingObjects.repositories;

import com.example.exerciseSpringDataAutoMappingObjects.models.entities.Game;
import com.example.exerciseSpringDataAutoMappingObjects.models.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByGamesContaining(Game game);
}
