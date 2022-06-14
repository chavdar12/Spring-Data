package com.example.exerciseSpringDataAutoMappingObjects.services.impl;

import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserLoginDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserRegisterDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.entities.User;
import com.example.exerciseSpringDataAutoMappingObjects.repositories.UserRepository;
import com.example.exerciseSpringDataAutoMappingObjects.services.UserService;
import com.example.exerciseSpringDataAutoMappingObjects.utilities.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final ValidationUtil validationUtill;
    private User loggedInUser;

    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper, ValidationUtil validationUtill) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.validationUtill = validationUtill;
    }

    @Override
    public void registerUser(UserRegisterDto userRegisterDto) {
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
            System.out.println("Wrong confirm password");
            return;
        }

        var violations = validationUtill.violation(userRegisterDto);

        if (!violations.isEmpty()) {
            violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);
            return;
        }

        userRepository.save(mapper.map(userRegisterDto, User.class));
    }

    @Override
    public void loginUser(UserLoginDto userLoginDto) {
        var violations = validationUtill.violation(userLoginDto);

        if (!violations.isEmpty()) {
            violations.stream().map(ConstraintViolation::getMessage).forEach(System.out::println);

            return;
        }

        var user =
                userRepository.findByEmailAndPassword(userLoginDto.getEmail(), userLoginDto.getPassword()).orElse(null);

        if (user == null) {
            System.out.println("Incorrect username/password");

            return;
        }

        loggedInUser = user;
    }

    @Override
    public void logout() {
        if (loggedInUser == null) {
            System.out.println("Cannot logout. No user was logged in");
        } else {
            loggedInUser = null;
        }
    }
}