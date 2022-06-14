package com.example.exersiceSpringDataIntro;

import com.example.exersiceSpringDataIntro.models.Book;
import com.example.exersiceSpringDataIntro.services.AuthorService;
import com.example.exersiceSpringDataIntro.services.BookService;
import com.example.exersiceSpringDataIntro.services.CategoryService;
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

        printAllAuthorNamesWithBooksWithReleaseDateBefore1990(1990);

        // printAllBooksAfter2000(2000);
    }

    private void printAllAuthorNamesWithBooksWithReleaseDateBefore1990(int year) {
        bookService.findAllAuthorsWithBooksWithReleaseDateBeforeYear(year);
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
