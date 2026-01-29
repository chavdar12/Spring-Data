package com.example.exerciseSpringDataAutoMappingObjects.services;

import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserLoginDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserRegisterDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void registerUser(UserRegisterDto userRegisterDto);

    void loginUser(UserLoginDto userLoginDto);

    void logout();
}
