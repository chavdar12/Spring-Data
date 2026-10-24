package com.example.exerciseSpringDataAdvancedQuerying.service;

import com.example.exerciseSpringDataAdvancedQuerying.model.entity.AgeRestriction;
import com.example.exerciseSpringDataAdvancedQuerying.model.entity.Author;
import com.example.exerciseSpringDataAdvancedQuerying.model.entity.Book;
import com.example.exerciseSpringDataAdvancedQuerying.model.entity.EditionType;
import com.example.exerciseSpringDataAdvancedQuerying.repository.AuthorRepository;
import com.example.exerciseSpringDataAdvancedQuerying.repository.BookRepository;
import com.example.exerciseSpringDataAdvancedQuerying.repository.CategoryRepository;
import com.example.exerciseSpringDataAdvancedQuerying.service.impl.AuthorServiceImpl;
import com.example.exerciseSpringDataAdvancedQuerying.service.impl.BookServiceImpl;
import com.example.exerciseSpringDataAdvancedQuerying.service.impl.CategoryServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({AuthorServiceImpl.class, BookServiceImpl.class, CategoryServiceImpl.class})
class ServicesTest {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private BookService bookService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private EntityManager entityManager;

    private Author author(String firstName, String lastName) {
        return authorRepository.save(new Author(firstName, lastName));
    }

    private void book(String title, Author author, String releaseDate, int copies, String price, EditionType edition,
                      AgeRestriction age) {
        bookRepository.save(new Book(edition, LocalDate.parse(releaseDate), copies, new BigDecimal(price), age, title,
                author, Set.of()));
    }

    /** George has 3 books, Ann has 1. */
    private void library() {
        Author george = author("George", "Powell");
        Author ann = author("Ann", "Lee");
        book("Old Classic", george, "1985-02-06", 120, "12.50", EditionType.NORMAL, AgeRestriction.MINOR);
        book("Cheap Thriller", george, "1989-07-15", 80, "3.99", EditionType.PROMO, AgeRestriction.TEEN);
        book("Gold Rush", george, "2000-06-01", 4999, "41.00", EditionType.GOLD, AgeRestriction.ADULT);
        book("Gold Plenty", ann, "2001-01-02", 5000, "19.90", EditionType.GOLD, AgeRestriction.ADULT);
        // make Author.books reflect the saved books
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void seedingReadsFixturesAndAssignsExistingAuthorAndCategories() throws IOException {
        categoryService.seedCategories();
        authorService.seedAuthors();
        bookService.seedBooks();
        bookService.seedBooks();

        assertThat(categoryRepository.count()).isEqualTo(4);
        assertThat(bookRepository.findAll()).hasSize(5).allSatisfy(book -> {
            assertThat(book.getAuthor()).isNotNull();
            assertThat(book.getCategories()).isNotEmpty().doesNotContainNull();
        });
    }

    @Test
    void booksAfterYearExcludeThatYear() {
        library();
        assertThat(bookService.findAllBooksAfterYear(2000)).extracting(Book::getTitle).containsExactly("Gold Plenty");
    }

    @Test
    void authorsWithBooksBeforeYearAreDistinct() {
        library();
        assertThat(bookService.findAllAuthorsWithBooksWithReleaseDateBeforeYear(1990)).containsExactly("George Powell");
    }

    @Test
    void authorsOrderedByBookCount() {
        library();
        assertThat(authorService.getAllAuthorsOrderByCountOfTheirBooks()).containsExactly("George Powell 3", "Ann Lee 1");
    }

    @Test
    void booksByAuthorNewestFirst() {
        library();
        assertThat(bookService.findAllBooksByAuthorFirstAndLastNameOrderByReleaseDate("George", "Powell")).containsExactly(
                "Gold Rush 2000-06-01 4999", "Cheap Thriller 1989-07-15 80", "Old Classic 1985-02-06 120");
    }

    @Test
    void titlesByAgeRestriction() {
        library();
        assertThat(bookService.findAllBookTitlesWithAgeRestriction(AgeRestriction.ADULT))
                .containsExactlyInAnyOrder("Gold Rush", "Gold Plenty");
    }

    @Test
    void goldenBooksWithLessThan5000Copies() {
        library();
        assertThat(bookService.findAllGoldBookTitlesWithLessThan5000Copies()).containsExactly("Gold Rush");
    }

    @Test
    void booksCheaperThan5OrPricierThan40() {
        library();
        assertThat(bookService.findAllBookTitlesWithPriceLessThan5OrMoreThan40()).containsExactlyInAnyOrder(
                String.format("Cheap Thriller - $ %.2f", 3.99), String.format("Gold Rush - $ %.2f", 41.00));
    }

    @Test
    void booksNotReleasedInYear() {
        library();
        assertThat(bookService.findNotReleasedBookTitlesBeforeYear(2000))
                .containsExactlyInAnyOrder("Old Classic", "Cheap Thriller", "Gold Plenty");
    }
}
