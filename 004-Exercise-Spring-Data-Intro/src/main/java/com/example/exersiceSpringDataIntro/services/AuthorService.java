package com.example.exersiceSpringDataIntro.services;

import com.example.exersiceSpringDataIntro.models.Author;

import java.io.IOException;

public interface AuthorService {
    void seedAuthors() throws IOException;

    Author getRandomAuthor();
}
