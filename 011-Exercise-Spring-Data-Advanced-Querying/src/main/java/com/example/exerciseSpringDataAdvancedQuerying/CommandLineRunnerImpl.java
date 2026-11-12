package com.example.exerciseSpringDataAdvancedQuerying;

import com.example.exerciseSpringDataAdvancedQuerying.model.entity.AgeRestriction;
import com.example.exerciseSpringDataAdvancedQuerying.service.AuthorService;
import com.example.exerciseSpringDataAdvancedQuerying.service.BookService;
import com.example.exerciseSpringDataAdvancedQuerying.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final BookService bookService;
    private final BufferedReader bufferedReader;
    private final DataSource dataSource;

    public CommandLineRunnerImpl(CategoryService categoryService, AuthorService authorService,
                                 BookService bookService, BufferedReader bufferedReader, DataSource dataSource) {
        this.categoryService = categoryService;
        this.authorService = authorService;
        this.bookService = bookService;
        this.bufferedReader = bufferedReader;
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        seedData();
        installStoredProcedure();

        System.out.println("Task number (1-14):");
        String task = bufferedReader.readLine();
        if (task == null) {
            return;
        }
        switch (task.trim()) {
            case "1" -> bookService.findAllBookTitlesWithAgeRestriction(
                    AgeRestriction.valueOf(ask("Age restriction:").toUpperCase(Locale.ROOT))).forEach(System.out::println);
            case "2" -> bookService.findAllGoldBookTitlesWithLessThan5000Copies().forEach(System.out::println);
            case "3" -> bookService.findAllBookTitlesWithPriceLessThan5OrMoreThan40().forEach(System.out::println);
            case "4" -> bookService.findNotReleasedBookTitlesBeforeYear(Integer.parseInt(ask("Year:"))).forEach(System.out::println);
            case "5" -> bookService.findAllBooksReleasedBefore(
                    LocalDate.parse(ask("Date (dd-MM-yyyy):"), DateTimeFormatter.ofPattern("dd-MM-yyyy"))).forEach(System.out::println);
            case "6" -> authorService.findAllAuthorNamesWithFirstNameEndingWith(ask("Ends with:")).forEach(System.out::println);
            case "7" -> bookService.findAllBookTitlesContaining(ask("Contains:")).forEach(System.out::println);
            case "8" -> bookService.findAllBooksByAuthorLastNameStartingWith(ask("Author last name starts with:")).forEach(System.out::println);
            case "9" -> {
                int length = Integer.parseInt(ask("Title length:"));
                System.out.printf("There are %d books with longer title than %d symbols%n",
                        bookService.countBooksWithTitleLongerThan(length), length);
            }
            case "10" -> authorService.getAllAuthorsOrderByTotalCopies().forEach(System.out::println);
            case "11" -> bookService.findBookSummariesByTitle(ask("Title:")).forEach(System.out::println);
            case "12" -> {
                LocalDate date = LocalDate.parse(ask("Date (dd MMM yyyy):"), DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH));
                System.out.println(bookService.increaseCopiesOfBooksReleasedAfter(date, Integer.parseInt(ask("Copies:"))));
            }
            case "13" -> System.out.printf("%d books were deleted%n", bookService.removeBooksWithCopiesLessThan(Integer.parseInt(ask("Copies:"))));
            case "14" -> {
                String[] name = ask("Author full name:").split("\\s+");
                System.out.printf("%s %s has written %d books%n", name[0], name[1], bookService.countBooksByAuthor(name[0], name[1]));
            }
            default -> System.out.println("Unknown task");
        }
    }

    private String ask(String prompt) throws IOException {
        System.out.println(prompt);
        return bufferedReader.readLine().trim();
    }

    private void installStoredProcedure() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("usp_get_books_count_by_author.sql"));
        populator.setSeparator("$$");
        populator.execute(dataSource);
    }

    private void seedData() throws IOException {
        categoryService.seedCategories();
        authorService.seedAuthors();
        bookService.seedBooks();
    }
}
