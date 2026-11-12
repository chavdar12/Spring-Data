package com.example.exerciseSpringDataAutoMappingObjects.repositories;

import com.example.exerciseSpringDataAutoMappingObjects.models.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByTitle(String title);
}
