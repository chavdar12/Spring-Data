package com.example.exercisejsonprocessing.productshop.repository;

import com.example.exercisejsonprocessing.productshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT DISTINCT u FROM User u JOIN u.soldProducts p WHERE p.buyer IS NOT NULL ORDER BY u.lastName, u.firstName")
    List<User> findAllWithSoldProducts();
}
