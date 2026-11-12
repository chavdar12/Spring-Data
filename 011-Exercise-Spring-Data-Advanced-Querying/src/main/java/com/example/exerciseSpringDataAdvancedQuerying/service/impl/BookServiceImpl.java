package com.example.exerciseSpringDataAdvancedQuerying.service.impl;

import com.example.exerciseSpringDataAdvancedQuerying.model.entity.*;
import com.example.exerciseSpringDataAdvancedQuerying.repository.BookRepository;
import com.example.exerciseSpringDataAdvancedQuerying.service.AuthorService;
import com.example.exerciseSpringDataAdvancedQuerying.service.BookService;
import com.example.exerciseSpringDataAdvancedQuerying.service.CategoryService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private static final String BOOKS_FILE_PATH = "files/books.txt";

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final CategoryService categoryService;

    public BookServiceImpl(BookRepository bookRepository, AuthorService authorService,
                           CategoryService categoryService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.categoryService = categoryService;
    }

    @Override
    public void seedBooks() throws IOException {
        if (bookRepository.count() > 0) {
            return;
        }

        new ClassPathResource(BOOKS_FILE_PATH).getContentAsString(StandardCharsets.UTF_8).lines().filter(row -> !row.isBlank()).forEach(row -> {
            String[] bookInfo = row.split("\\s+");

            Book book = createBookFromInfo(bookInfo);

            bookRepository.save(book);
        });
    }

    @Override
    public List<Book> findAllBooksAfterYear(int year) {
        return bookRepository.findAllByReleaseDateAfter(LocalDate.of(year, 12, 31));
    }

    @Override
    public List<String> findAllAuthorsWithBooksWithReleaseDateBeforeYear(int year) {
        return bookRepository.findAllByReleaseDateBefore(LocalDate.of(year, 1, 1)).stream().map(book -> String.format("%s %s", book.getAuthor().getFirstName(), book.getAuthor().getLastName())).distinct().collect(Collectors.toList());
    }

    @Override
    public List<String> findAllBooksByAuthorFirstAndLastNameOrderByReleaseDate(String firstName, String lastName) {
        return bookRepository.findAllByAuthor_FirstNameAndAuthor_LastNameOrderByReleaseDateDescTitle(firstName,
                                                                                                     lastName).stream().map(book -> String.format("%s %s %d", book.getTitle(), book.getReleaseDate(),
                                                                                                                                                  book.getCopies())).collect(Collectors.toList());
    }

    @Override
    public List<String> findAllBookTitlesWithAgeRestriction(AgeRestriction ageRestriction) {
        return bookRepository.findAllByAgeRestriction(ageRestriction).stream().map(Book::getTitle).collect(Collectors.toList());
    }

    @Override
    public List<String> findAllGoldBookTitlesWithLessThan5000Copies() {
        return bookRepository.findAllByEditionTypeAndCopiesLessThan(EditionType.GOLD, 5000).stream().map(Book::getTitle).collect(Collectors.toList());
    }

    @Override
    public List<String> findAllBookTitlesWithPriceLessThan5OrMoreThan40() {
        return bookRepository.findAllByPriceLessThanOrPriceGreaterThan(BigDecimal.valueOf(5), BigDecimal.valueOf(40)).stream().map(book -> String.format("%s - $ %.2f", book.getTitle(), book.getPrice())).collect(Collectors.toList());
    }

    @Override
    public List<String> findNotReleasedBookTitlesBeforeYear(int year) {
        LocalDate lower = LocalDate.of(year, 1, 1);
        LocalDate upper = LocalDate.of(year, 12, 31);

        return bookRepository.findAllByReleaseDateBeforeOrReleaseDateAfter(lower, upper).stream().map(Book::getTitle).collect(Collectors.toList());
    }

    @Override
    public List<String> findAllBooksReleasedBefore(LocalDate date) {
        return bookRepository.findAllByReleaseDateBefore(date).stream()
                .map(book -> "%s %s %.2f".formatted(book.getTitle(), book.getEditionType(), book.getPrice()))
                .toList();
    }

    @Override
    public List<String> findAllBookTitlesContaining(String part) {
        return bookRepository.findAllByTitleContainingIgnoreCase(part).stream().map(Book::getTitle).toList();
    }

    @Override
    public List<String> findAllBooksByAuthorLastNameStartingWith(String prefix) {
        return bookRepository.findAllByAuthor_LastNameStartingWithIgnoreCase(prefix).stream()
                .map(book -> "%s (%s %s)".formatted(book.getTitle(), book.getAuthor().getFirstName(), book.getAuthor().getLastName()))
                .toList();
    }

    @Override
    public long countBooksWithTitleLongerThan(int length) {
        return bookRepository.countByTitleLongerThan(length);
    }

    @Override
    public List<String> findBookSummariesByTitle(String title) {
        return bookRepository.findAllByTitle(title).stream()
                .map(b -> "%s %s %s %.2f".formatted(b.getTitle(), b.getEditionType(), b.getAgeRestriction(), b.getPrice()))
                .toList();
    }

    @Override
    @Transactional
    public long increaseCopiesOfBooksReleasedAfter(LocalDate date, int amount) {
        return (long) bookRepository.increaseCopiesOfBooksReleasedAfter(date, amount) * amount;
    }

    @Override
    @Transactional
    public int removeBooksWithCopiesLessThan(int copies) {
        // entity delete (not a bulk JPQL delete) so the books_categories rows go too
        List<Book> books = bookRepository.findAllByCopiesLessThan(copies);
        bookRepository.deleteAll(books);
        return books.size();
    }

    @Override
    public int countBooksByAuthor(String firstName, String lastName) {
        return bookRepository.countBooksByAuthorProcedure(firstName, lastName);
    }

    private Book createBookFromInfo(String[] bookInfo) {
        EditionType editionType = EditionType.values()[Integer.parseInt(bookInfo[0])];
        LocalDate releaseDate = LocalDate.parse(bookInfo[1], DateTimeFormatter.ofPattern("d/M/yyyy"));
        Integer copies = Integer.parseInt(bookInfo[2]);
        BigDecimal price = new BigDecimal(bookInfo[3]);
        AgeRestriction ageRestriction = AgeRestriction.values()[Integer.parseInt(bookInfo[4])];
        String title = Arrays.stream(bookInfo).skip(5).collect(Collectors.joining(" "));

        Author author = authorService.getRandomAuthor();
        Set<Category> categories = categoryService.getRandomCategories();

        return new Book(editionType, releaseDate, copies, price, ageRestriction, title, author, categories);
    }
}
