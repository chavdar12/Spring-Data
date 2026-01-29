package com.example.exerciseSpringDataAdvancedQuerying.service;

import com.example.exerciseSpringDataAdvancedQuerying.model.entity.AgeRestriction;
import com.example.exerciseSpringDataAdvancedQuerying.model.entity.Book;

import java.io.IOException;
import java.util.List;

public interface BookService {
    void seedBooks() throws IOException;

    List<Book> findAllBooksAfterYear(int year);

    List<String> findAllAuthorsWithBooksWithReleaseDateBeforeYear(int year);

    List<String> findAllBooksByAuthorFirstAndLastNameOrderByReleaseDate(String firstName, String lastName);

    List<String> findAllBookTitlesWithAgeRestriction(AgeRestriction ageRestriction);

    List<String> findAllGoldBookTitlesWithLesThan500copies();

    List<String> findAllBookTitlesWithPriceLessThan5OrMoreThan40();

    List<String> findNotReleasedBookTitlesBeforeYear(int year);
}
