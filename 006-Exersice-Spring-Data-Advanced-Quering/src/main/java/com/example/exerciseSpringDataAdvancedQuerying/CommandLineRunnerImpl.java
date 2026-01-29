package com.example.exerciseSpringDataAdvancedQuerying;

import com.example.exerciseSpringDataAdvancedQuerying.model.entity.AgeRestriction;
import com.example.exerciseSpringDataAdvancedQuerying.model.entity.Book;
import com.example.exerciseSpringDataAdvancedQuerying.service.AuthorService;
import com.example.exerciseSpringDataAdvancedQuerying.service.BookService;
import com.example.exerciseSpringDataAdvancedQuerying.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Locale;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final BookService bookService;
    private final BufferedReader bufferedReader;

    public CommandLineRunnerImpl(CategoryService categoryService, AuthorService authorService,
                                 BookService bookService, BufferedReader bufferedReader) {
        this.categoryService = categoryService;
        this.authorService = authorService;
        this.bookService = bookService;
        this.bufferedReader = bufferedReader;
    }

    @Override
    public void run(String... args) throws Exception {
        seedData();

        /*printAllBooksAfterYear(2000);
        printAllAuthorsNamesWithBooksWithReleaseDateBeforeYear(1990);
        printAllAuthorsAndNumberOfTheirBooks();
        printAllBooksByAuthorNameOrderByReleaseDate("George", "Powell");*/

        //bookTitlesByAge();
        //goldenBook();
        //booksByPrice();
        //notReleaseBooks();
    }

    private void notReleaseBooks() throws IOException {
        System.out.println("Enter year: ");
        int date = Integer.parseInt(bufferedReader.readLine());
        bookService.findNotReleasedBookTitlesBeforeYear(date).forEach(System.out::println);
    }

    private void booksByPrice() {
        bookService.findAllBookTitlesWithPriceLessThan5OrMoreThan40().forEach(System.out::println);
    }

    private void goldenBook() {
        bookService.findAllGoldBookTitlesWithLesThan500copies().forEach(System.out::println);
    }

    private void bookTitlesByAge() throws IOException {
        System.out.println("Enter Age Restriction: ");
        AgeRestriction ageRestriction = AgeRestriction.valueOf(bufferedReader.readLine().toUpperCase(Locale.ROOT));

        bookService.findAllBookTitlesWithAgeRestriction(ageRestriction).forEach(System.out::println);
    }

    private void printAllBooksByAuthorNameOrderByReleaseDate(String firstName, String lastName) {
        bookService.findAllBooksByAuthorFirstAndLastNameOrderByReleaseDate(firstName, lastName).forEach(System.out::println);
    }

    private void printAllAuthorsAndNumberOfTheirBooks() {
        authorService.getAllAuthorsOrderByCountOfTheirBooks().forEach(System.out::println);
    }

    private void printAllAuthorsNamesWithBooksWithReleaseDateBeforeYear(int year) {
        bookService.findAllAuthorsWithBooksWithReleaseDateBeforeYear(year).forEach(System.out::println);
    }

    private void printAllBooksAfterYear(int year) {
        bookService.findAllBooksAfterYear(year).stream().map(Book::getTitle).forEach(System.out::println);
    }

    private void seedData() throws IOException {
        categoryService.seedCategories();
        authorService.seedAuthors();
        bookService.seedBooks();
    }
}
