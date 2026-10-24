package com.example.exersiceSpringDataIntro.services;

import com.example.exersiceSpringDataIntro.models.Book;
import com.example.exersiceSpringDataIntro.models.EditionType;
import com.example.exersiceSpringDataIntro.repositories.AuthorRepository;
import com.example.exersiceSpringDataIntro.repositories.BookRepository;
import com.example.exersiceSpringDataIntro.repositories.CategoryRepository;
import com.example.exersiceSpringDataIntro.services.Implementations.AuthorServiceImpl;
import com.example.exersiceSpringDataIntro.services.Implementations.BookServiceImpl;
import com.example.exersiceSpringDataIntro.services.Implementations.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/** Seeds from the fixture files in src/test/resources/files. */
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

    @BeforeEach
    void seed() throws IOException {
        categoryService.seedCategories();
        authorService.seedAuthors();
        bookService.seedBooks();
    }

    @Test
    void seedingReadsFixturesSkipsBlankLinesAndIsIdempotent() throws IOException {
        seed();

        assertThat(categoryRepository.count()).isEqualTo(4);
        assertThat(authorRepository.count()).isEqualTo(1);
        assertThat(bookRepository.findAll()).hasSize(5).allSatisfy(book -> {
            assertThat(book.getAuthor().getLastName()).isEqualTo("Powell");
            assertThat(book.getCategories()).isNotEmpty().doesNotContainNull();
        });
    }

    @Test
    void seedingParsesBookColumns() {
        Book book = bookRepository.findAll().stream().filter(b -> b.getTitle().equals("Last Day Of The Century"))
                .findFirst().orElseThrow();

        assertThat(book.getEditionType()).isEqualTo(EditionType.GOLD);
        assertThat(book.getReleaseDate()).isEqualTo(LocalDate.of(2000, 12, 31));
        assertThat(book.getCopies()).isEqualTo(300);
        assertThat(book.getPrice()).isEqualByComparingTo("41.00");
    }

    @Test
    void randomCategoriesAreExistingCategories() {
        for (int i = 0; i < 20; i++) {
            assertThat(categoryService.getRandomCategories()).hasSizeBetween(1, 2).doesNotContainNull();
        }
    }

    @Test
    void booksAfterYearExcludeThatYear() {
        assertThat(bookService.findAllBooksAfterYear(2000)).extracting(Book::getTitle).containsExactly("New Century");
    }

    @Test
    void authorsWithBooksBeforeYearAreDistinct() {
        assertThat(bookService.findAllAuthorsWithBooksWithReleaseDateBeforeYear(1990)).containsExactly("George Powell");
        assertThat(bookService.findAllAuthorsWithBooksWithReleaseDateBeforeYear(1985)).isEmpty();
    }
}
