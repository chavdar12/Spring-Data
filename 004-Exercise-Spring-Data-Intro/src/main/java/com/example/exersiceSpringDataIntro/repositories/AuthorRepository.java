package com.example.exersiceSpringDataIntro.repositories;

import com.example.exersiceSpringDataIntro.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuthorRepository extends JpaRepository<Author, Long> {
}