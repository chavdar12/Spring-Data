package com.example.exerciseSpringDataAutoMappingObjects.services.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserLoginDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserRegisterDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.entities.Game;
import com.example.exerciseSpringDataAutoMappingObjects.models.entities.Order;
import com.example.exerciseSpringDataAutoMappingObjects.models.entities.User;
import com.example.exerciseSpringDataAutoMappingObjects.repositories.GameRepository;
import com.example.exerciseSpringDataAutoMappingObjects.repositories.OrderRepository;
import com.example.exerciseSpringDataAutoMappingObjects.repositories.UserRepository;
import com.example.exerciseSpringDataAutoMappingObjects.services.UserService;
import com.example.exerciseSpringDataAutoMappingObjects.utilities.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolation;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final ValidationUtil validationUtill;
    private final GameRepository gameRepository;
    private final OrderRepository orderRepository;
    private final Set<Game> cart = new LinkedHashSet<>();
    private User loggedInUser;

    public UserServiceImpl(UserRepository userRepository, GameRepository gameRepository, OrderRepository orderRepository,
                           ModelMapper mapper, ValidationUtil validationUtill) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.orderRepository = orderRepository;
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

        // duplicate emails made findByEmailAndPassword fail with a non-unique result on login
        if (userRepository.existsByEmail(userRegisterDto.getEmail())) {
            System.out.println("Email already registered");
            return;
        }

        User user = mapper.map(userRegisterDto, User.class);
        user.setAdmin(userRepository.count() == 0); // the first registered user is the administrator
        userRepository.save(user);
        System.out.printf("%s was registered%n", userRegisterDto.getFullName());
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
        System.out.printf("Successfully logged in %s%n", user.getFullName());
    }

    @Override
    public void logout() {
        if (loggedInUser == null) {
            System.out.println("Cannot logout. No user was logged in");
        } else {
            System.out.printf("User %s successfully logged out%n", loggedInUser.getFullName());
            loggedInUser = null;
            cart.clear();
        }
    }

    @Override
    public User getLoggedInUser() {
        return loggedInUser;
    }

    @Override
    @Transactional(readOnly = true)
    public void printOwnedGames() {
        if (requireLogin()) {
            userRepository.findById(loggedInUser.getId()).orElseThrow().getGames().stream()
                    .map(Game::getTitle).sorted().forEach(System.out::println);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void addItem(String title) {
        if (!requireLogin()) {
            return;
        }
        Game game = gameRepository.findByTitle(title).orElse(null);
        if (game == null) {
            System.out.println("No such game");
        } else if (userRepository.findById(loggedInUser.getId()).orElseThrow().getGames().contains(game)) {
            System.out.printf("You already own %s%n", title);
        } else if (!cart.add(game)) {
            System.out.printf("%s is already in the cart%n", title);
        } else {
            System.out.printf("%s added to cart.%n", title);
        }
    }

    @Override
    public void removeItem(String title) {
        if (requireLogin()) {
            System.out.println(cart.removeIf(game -> game.getTitle().equals(title))
                    ? title + " removed from cart."
                    : title + " is not in the cart");
        }
    }

    @Override
    @Transactional
    public void buyItems() {
        if (!requireLogin()) {
            return;
        }
        if (cart.isEmpty()) {
            System.out.println("The cart is empty");
            return;
        }
        User buyer = userRepository.findById(loggedInUser.getId()).orElseThrow();
        Order order = new Order();
        order.setBuyer(buyer);
        order.getGames().addAll(cart);
        orderRepository.save(order);
        buyer.getGames().addAll(cart);

        System.out.println("Successfully bought games:");
        cart.forEach(game -> System.out.printf(" -%s%n", game.getTitle()));
        cart.clear();
    }

    private boolean requireLogin() {
        if (loggedInUser == null) {
            System.out.println("No user is logged in");
            return false;
        }
        return true;
    }
}