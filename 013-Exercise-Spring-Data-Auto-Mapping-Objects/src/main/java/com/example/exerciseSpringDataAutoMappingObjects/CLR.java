package com.example.exerciseSpringDataAutoMappingObjects;

import com.example.exerciseSpringDataAutoMappingObjects.models.dto.GameAddDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserLoginDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserRegisterDto;
import com.example.exerciseSpringDataAutoMappingObjects.services.GameService;
import com.example.exerciseSpringDataAutoMappingObjects.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CLR implements CommandLineRunner {

    private final BufferedReader reader;
    private final UserService userService;
    private final GameService gameService;

    public CLR(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Enter commands: ");
        String line;
        // stop at end of input instead of looping forever on a null line
        while ((line = reader.readLine()) != null) {
            String[] command = line.split("\\|");
            try {
                execute(command);
            } catch (NumberFormatException | DateTimeParseException e) {
                System.out.println("Invalid command");
            }
        }
    }

    private void execute(String[] command) {
        switch (command[0]) {
            case "RegisterUser" -> {
                if (command.length < 5) {
                    System.out.println("Invalid command");
                    return;
                }
                userService.registerUser(new UserRegisterDto(command[1], command[2], command[3], command[4]));
            }
            case "LoginUser" -> {
                if (command.length < 3) {
                    System.out.println("Invalid command");
                    return;
                }
                userService.loginUser(new UserLoginDto(command[1], command[2]));
            }
            case "Logout" -> userService.logout();
            case "AddGame" -> {
                if (command.length < 8) {
                    System.out.println("Invalid command");
                    return;
                }
                gameService.addGame(new GameAddDto(command[1], new BigDecimal(command[2]), new BigDecimal(command[3]),
                        command[4], command[5], command[6], LocalDate.parse(command[7], DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
            }
            case "EditGame" -> {
                if (command.length < 3) {
                    System.out.println("Invalid command");
                    return;
                }
                Map<String, String> values = new LinkedHashMap<>();
                Arrays.stream(command).skip(2).map(pair -> pair.split("=", 2))
                        .forEach(pair -> values.put(pair[0], pair.length > 1 ? pair[1] : ""));
                gameService.editGame(Long.parseLong(command[1]), values);
            }
            case "DeleteGame" -> {
                if (command.length < 2) {
                    System.out.println("Invalid command");
                    return;
                }
                gameService.deleteGame(Long.parseLong(command[1]));
            }
            case "AllGames" -> gameService.printAllGames();
            case "DetailGame" -> {
                if (command.length < 2) {
                    System.out.println("Invalid command");
                    return;
                }
                gameService.printGameDetails(command[1]);
            }
            case "OwnedGames" -> userService.printOwnedGames();
            case "AddItem" -> {
                if (command.length < 2) {
                    System.out.println("Invalid command");
                    return;
                }
                userService.addItem(command[1]);
            }
            case "RemoveItem" -> {
                if (command.length < 2) {
                    System.out.println("Invalid command");
                    return;
                }
                userService.removeItem(command[1]);
            }
            case "BuyItem" -> userService.buyItems();
            default -> System.out.println("Invalid command");
        }
    }
}
