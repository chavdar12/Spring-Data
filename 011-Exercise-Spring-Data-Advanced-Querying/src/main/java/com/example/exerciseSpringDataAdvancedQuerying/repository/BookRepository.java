package com.example.exerciseSpringDataAdvancedQuerying.repository;

import com.example.exerciseSpringDataAdvancedQuerying.model.entity.AgeRestriction;
import com.example.exerciseSpringDataAdvancedQuerying.model.entity.Book;
import com.example.exerciseSpringDataAdvancedQuerying.model.entity.EditionType;
import com.example.exerciseSpringDataAdvancedQuerying.model.entity.BookSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByReleaseDateAfter(LocalDate releaseDateAfter);

    List<Book> findAllByReleaseDateBefore(LocalDate releaseDateBefore);

    List<Book> findAllByAuthor_FirstNameAndAuthor_LastNameOrderByReleaseDateDescTitle(String author_firstName,
                                                                                      String author_lastName);

    List<Book> findAllByAgeRestriction(AgeRestriction ageRestriction);

    List<Book> findAllByEditionTypeAndCopiesLessThan(EditionType editionType, Integer copies);

    List<Book> findAllByPriceLessThanOrPriceGreaterThan(BigDecimal lowerBound, BigDecimal upperBound);

    List<Book> findAllByReleaseDateBeforeOrReleaseDateAfter(LocalDate lower, LocalDate upper);

    List<Book> findAllByTitleContainingIgnoreCase(String part);

    List<Book> findAllByAuthor_LastNameStartingWithIgnoreCase(String prefix);

    @Query("SELECT COUNT(b) FROM Book b WHERE LENGTH(b.title) > :length")
    long countByTitleLongerThan(@Param("length") int length);

    List<BookSummary> findAllByTitle(String title);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Book b SET b.copies = b.copies + :amount WHERE b.releaseDate > :date")
    int increaseCopiesOfBooksReleasedAfter(@Param("date") LocalDate date, @Param("amount") int amount);

    List<Book> findAllByCopiesLessThan(int copies);

    @Query(value = "CALL usp_get_books_count_by_author(:firstName, :lastName)", nativeQuery = true)
    int countBooksByAuthorProcedure(@Param("firstName") String firstName, @Param("lastName") String lastName);
}
