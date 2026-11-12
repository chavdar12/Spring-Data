package com.example.exerciseSpringDataAdvancedQuerying.service;

import com.example.exerciseSpringDataAdvancedQuerying.model.entity.AgeRestriction;
import com.example.exerciseSpringDataAdvancedQuerying.model.entity.Book;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface BookService {
    void seedBooks() throws IOException;

    List<Book> findAllBooksAfterYear(int year);

    List<String> findAllAuthorsWithBooksWithReleaseDateBeforeYear(int year);

    List<String> findAllBooksByAuthorFirstAndLastNameOrderByReleaseDate(String firstName, String lastName);

    List<String> findAllBookTitlesWithAgeRestriction(AgeRestriction ageRestriction);

    List<String> findAllGoldBookTitlesWithLessThan5000Copies();

    List<String> findAllBookTitlesWithPriceLessThan5OrMoreThan40();

    List<String> findNotReleasedBookTitlesBeforeYear(int year);

    List<String> findAllBooksReleasedBefore(LocalDate date);

    List<String> findAllBookTitlesContaining(String part);

    List<String> findAllBooksByAuthorLastNameStartingWith(String prefix);

    long countBooksWithTitleLongerThan(int length);

    List<String> findBookSummariesByTitle(String title);

    long increaseCopiesOfBooksReleasedAfter(LocalDate date, int amount);

    int removeBooksWithCopiesLessThan(int copies);

    int countBooksByAuthor(String firstName, String lastName);
}
