package com.example.exerciseSpringDataIntro.services;

import com.example.exerciseSpringDataIntro.models.Book;

import java.io.IOException;
import java.util.List;

public interface BookService {
    void seedBooks() throws IOException;

    List<Book> findAllBooksAfterYear(int year);

    List<String> findAllAuthorsWithBooksWithReleaseDateBeforeYear(int year);

    List<String> findAllBooksByAuthorOrderByReleaseDate(String firstName, String lastName);
}
