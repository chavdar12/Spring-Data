package com.example.exerciseSpringDataIntro.repositories;

import com.example.exerciseSpringDataIntro.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface AuthorRepository extends JpaRepository<Author, Long> {

    /** Rows of [firstName, lastName, bookCount], most books first. */
    @Query("SELECT a.firstName, a.lastName, COUNT(b) FROM Author a LEFT JOIN Book b ON b.author = a " +
           "GROUP BY a.id, a.firstName, a.lastName ORDER BY COUNT(b) DESC, a.id")
    List<Object[]> findAllWithBookCount();
}
