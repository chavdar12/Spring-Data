package com.example.exerciseSpringDataAutoMappingObjects.services.impl;

import com.example.exerciseSpringDataAutoMappingObjects.models.dto.GameAddDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.entities.Game;
import com.example.exerciseSpringDataAutoMappingObjects.models.entities.User;
import com.example.exerciseSpringDataAutoMappingObjects.repositories.GameRepository;
import com.example.exerciseSpringDataAutoMappingObjects.repositories.OrderRepository;
import com.example.exerciseSpringDataAutoMappingObjects.repositories.UserRepository;
import com.example.exerciseSpringDataAutoMappingObjects.services.GameService;
import com.example.exerciseSpringDataAutoMappingObjects.services.UserService;
import com.example.exerciseSpringDataAutoMappingObjects.utilities.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class GameServiceImpl implements GameService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ModelMapper mapper;
    private final ValidationUtil validationUtil;

    public GameServiceImpl(GameRepository gameRepository, UserRepository userRepository, OrderRepository orderRepository,
                           UserService userService, ModelMapper mapper, ValidationUtil validationUtil) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.mapper = mapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public void addGame(GameAddDto gameAddDto) {
        if (!isAdmin() || !isValid(gameAddDto)) {
            return;
        }
        if (gameRepository.findByTitle(gameAddDto.getTitle()).isPresent()) {
            System.out.println("Game already exists");
            return;
        }
        gameRepository.save(mapper.map(gameAddDto, Game.class));
        System.out.printf("Added %s%n", gameAddDto.getTitle());
    }

    @Override
    public void editGame(long id, Map<String, String> values) {
        if (!isAdmin()) {
            return;
        }
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            System.out.println("No such game");
            return;
        }

        // validate the edited game through the same rules as AddGame
        GameAddDto edited = mapper.map(game, GameAddDto.class);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String value = entry.getValue();
            switch (entry.getKey()) {
                case "title" -> edited.setTitle(value);
                case "price" -> edited.setPrice(new BigDecimal(value));
                case "size" -> edited.setSize(new BigDecimal(value));
                case "trailer" -> edited.setTrailer(new GameAddDto(null, null, null, value, null, null, null).getTrailer());
                case "thumbnailURL", "imageThumbnail" -> edited.setImageThumbnail(value);
                case "description" -> edited.setDescription(value);
                case "releaseDate" -> edited.setReleaseDate(LocalDate.parse(value, DATE));
                default -> {
                    System.out.println("Unknown field " + entry.getKey());
                    return;
                }
            }
        }
        if (!isValid(edited)) {
            return;
        }
        mapper.map(edited, game);
        gameRepository.save(game);
        System.out.printf("Edited %s%n", game.getTitle());
    }

    @Override
    @Transactional
    public void deleteGame(long id) {
        if (!isAdmin()) {
            return;
        }
        Game game = gameRepository.findById(id).orElse(null);
        if (game == null) {
            System.out.println("No such game");
            return;
        }
        // the join tables reference the game, so unlink it first
        userRepository.findAllByGamesContaining(game).forEach(user -> user.getGames().remove(game));
        orderRepository.findAllByGamesContaining(game).forEach(order -> order.getGames().remove(game));
        gameRepository.delete(game);
        System.out.printf("Deleted %s%n", game.getTitle());
    }

    @Override
    public void printAllGames() {
        gameRepository.findAll().forEach(game -> System.out.printf("%s %.2f%n", game.getTitle(), game.getPrice()));
    }

    @Override
    public void printGameDetails(String title) {
        gameRepository.findByTitle(title).ifPresentOrElse(game -> System.out.printf(
                "Title: %s%nPrice: %.2f%nDescription: %s%nRelease date: %s%n",
                game.getTitle(), game.getPrice(), game.getDescription(), game.getReleaseDate().format(DATE)),
                () -> System.out.println("No such game"));
    }

    private boolean isAdmin() {
        User user = userService.getLoggedInUser();
        if (user == null || !Boolean.TRUE.equals(user.getAdmin())) {
            System.out.println("Only a logged in administrator can manage games");
            return false;
        }
        return true;
    }

    private boolean isValid(GameAddDto dto) {
        var violations = validationUtil.violation(dto);
        violations.stream().map(ConstraintViolation::getMessage).sorted().forEach(System.out::println);
        return violations.isEmpty();
    }
}
