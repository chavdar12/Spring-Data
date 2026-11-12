package com.example.exerciseSpringDataAutoMappingObjects.services;

import com.example.exerciseSpringDataAutoMappingObjects.models.dto.GameAddDto;

import java.util.Map;

public interface GameService {
    void addGame(GameAddDto gameAddDto);

    /** values: field name to new value, e.g. price=80.00 */
    void editGame(long id, Map<String, String> values);

    void deleteGame(long id);

    void printAllGames();

    void printGameDetails(String title);
}
