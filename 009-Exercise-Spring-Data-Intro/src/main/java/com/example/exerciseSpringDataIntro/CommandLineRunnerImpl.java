package com.example.exerciseSpringDataIntro;

import com.example.exerciseSpringDataIntro.models.Book;
import com.example.exerciseSpringDataIntro.services.AuthorService;
import com.example.exerciseSpringDataIntro.services.BookService;
import com.example.exerciseSpringDataIntro.services.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final BookService bookService;

    public CommandLineRunnerImpl(CategoryService categoryService, AuthorService authorService,
                                 BookService bookService) {
        this.categoryService = categoryService;
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @Override
    public void run(String... args) throws Exception {
        seedData();

        System.out.println("1. Books after 2000:");
        printAllBooksAfter2000(2000);
        System.out.println("2. Authors with books before 1990:");
        printAllAuthorNamesWithBooksWithReleaseDateBefore1990(1990);
        System.out.println("3. Authors by number of books:");
        authorService.getAllAuthorsOrderByCountOfTheirBooks().forEach(System.out::println);
        System.out.println("4. Books by George Powell:");
        bookService.findAllBooksByAuthorOrderByReleaseDate("George", "Powell").forEach(System.out::println);
    }

    private void printAllAuthorNamesWithBooksWithReleaseDateBefore1990(int year) {
        bookService.findAllAuthorsWithBooksWithReleaseDateBeforeYear(year).forEach(System.out::println);
    }

    private void printAllBooksAfter2000(int year) {
        bookService.findAllBooksAfterYear(year).stream().map(Book::getTitle).forEach(System.out::println);
    }

    private void seedData() throws IOException {
        categoryService.seedCategories();
        authorService.seedAuthors();
        bookService.seedBooks();
    }
}
