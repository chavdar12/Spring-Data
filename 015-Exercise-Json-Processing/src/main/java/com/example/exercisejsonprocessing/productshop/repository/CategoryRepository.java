package com.example.exercisejsonprocessing.productshop.repository;

import com.example.exercisejsonprocessing.productshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /** Rows of [name, productsCount, averagePrice, totalRevenue]. */
    @Query("SELECT c.name, COUNT(p), AVG(p.price), SUM(p.price) FROM Category c JOIN c.products p " +
           "GROUP BY c.id, c.name ORDER BY COUNT(p) DESC, c.name")
    List<Object[]> findCategoryStats();
}
