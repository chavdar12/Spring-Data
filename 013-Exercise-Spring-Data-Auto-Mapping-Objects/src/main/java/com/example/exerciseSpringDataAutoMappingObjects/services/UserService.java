package com.example.exerciseSpringDataAutoMappingObjects.services;

import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserLoginDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserRegisterDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.entities.User;

public interface UserService {
    void registerUser(UserRegisterDto userRegisterDto);

    void loginUser(UserLoginDto userLoginDto);

    void logout();

    /** The logged-in user, or null. */
    User getLoggedInUser();

    void printOwnedGames();

    void addItem(String title);

    void removeItem(String title);

    void buyItems();
}
