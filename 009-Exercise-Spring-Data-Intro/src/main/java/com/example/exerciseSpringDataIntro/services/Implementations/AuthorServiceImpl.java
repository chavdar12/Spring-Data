package com.example.exerciseSpringDataIntro.services.Implementations;

import com.example.exerciseSpringDataIntro.models.Author;
import com.example.exerciseSpringDataIntro.repositories.AuthorRepository;
import com.example.exerciseSpringDataIntro.services.AuthorService;
import org.springframework.data.domain.PageRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthorServiceImpl implements AuthorService {

    public static final String AUTHORS_FILE_PATH = "files/authors.txt";

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public void seedAuthors() throws IOException {
        if (authorRepository.count() > 0) {
            return;
        }

        new ClassPathResource(AUTHORS_FILE_PATH).getContentAsString(StandardCharsets.UTF_8).lines().filter(row -> !row.isBlank()).forEach(row -> {

            String[] fullName = row.split("\\s+");

            Author author = new Author(fullName[0], fullName[1]);

            authorRepository.save(author);
        });
    }

    @Override
    public List<String> getAllAuthorsOrderByCountOfTheirBooks() {
        return authorRepository.findAllWithBookCount().stream()
                .map(row -> "%s %s %d".formatted(row[0], row[1], (Long) row[2]))
                .toList();
    }

    @Override
    public Author getRandomAuthor() {
        // pick by position: ids are not guaranteed to be 1..count
        long randomIndex = ThreadLocalRandom.current().nextLong(authorRepository.count());

        return authorRepository.findAll(PageRequest.of((int) randomIndex, 1)).stream().findFirst().orElse(null);
    }
}
