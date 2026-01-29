package com.example.exersiceSpringDataIntro.services.Implementations;

import com.example.exersiceSpringDataIntro.models.Author;
import com.example.exersiceSpringDataIntro.repositories.AuthorRepository;
import com.example.exersiceSpringDataIntro.services.AuthorService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthorServiceImpl implements AuthorService {

    public static final String AUTHORS_FILE_PATH = "src/main/resources/files/authors.txt";

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public void seedAuthors() throws IOException {
        if (authorRepository.count() > 0) {
            return;
        }

        Files.readAllLines(Path.of(AUTHORS_FILE_PATH)).stream().filter(row -> !row.isBlank()).forEach(row -> {

            String[] fullName = row.split("\\s+");

            Author author = new Author(fullName[0], fullName[1]);

            authorRepository.save(author);
        });
    }

    @Override
    public Author getRandomAuthor() {
        long randomId = ThreadLocalRandom.current().nextLong(1, authorRepository.count() + 1);

        return authorRepository.findById(randomId).orElse(null);
    }
}
