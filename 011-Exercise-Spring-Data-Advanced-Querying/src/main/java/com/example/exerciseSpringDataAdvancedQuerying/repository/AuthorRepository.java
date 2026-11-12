package com.example.exerciseSpringDataAdvancedQuerying.repository;

import com.example.exerciseSpringDataAdvancedQuerying.model.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a FROM Author a ORDER BY size(a.books) DESC")
    List<Author> findAllByBooksSizeDESC();

    List<Author> findAllByFirstNameEndingWith(String suffix);

    /** Rows of [firstName, lastName, totalCopies], most copies first. */
    @Query("SELECT a.firstName, a.lastName, COALESCE(SUM(b.copies), 0) FROM Author a LEFT JOIN a.books b " +
           "GROUP BY a.id, a.firstName, a.lastName ORDER BY COALESCE(SUM(b.copies), 0) DESC")
    List<Object[]> findAllWithTotalCopies();
}
