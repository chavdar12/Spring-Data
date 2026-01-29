package com.example.exerciseSpringDataAdvancedQuerying.service;

import com.example.exerciseSpringDataAdvancedQuerying.model.entity.Author;

import java.io.IOException;
import java.util.List;

public interface AuthorService {
    void seedAuthors() throws IOException;

    Author getRandomAuthor();

    List<String> getAllAuthorsOrderByCountOfTheirBooks();
}
