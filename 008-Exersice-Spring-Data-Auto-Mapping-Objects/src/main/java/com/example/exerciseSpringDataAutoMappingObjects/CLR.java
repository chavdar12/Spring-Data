package com.example.exerciseSpringDataAutoMappingObjects;

import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserLoginDto;
import com.example.exerciseSpringDataAutoMappingObjects.models.dto.UserRegisterDto;
import com.example.exerciseSpringDataAutoMappingObjects.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class CLR implements CommandLineRunner {

    private final BufferedReader reader;
    private final UserService userService;

    public CLR(UserService userService) {
        this.userService = userService;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Enter commands: ");
        String line;
        // stop at end of input instead of looping forever on a null line
        while ((line = reader.readLine()) != null) {
            String[] command = line.split("\\|");
            switch (command[0]) {
                case "RegisterUser":
                    if (command.length < 5) {
                        System.out.println("Invalid command");
                        break;
                    }
                    this.userService.registerUser(new UserRegisterDto(command[1], command[2], command[3], command[4]));
                    break;

                case "LoginUser":
                    if (command.length < 3) {
                        System.out.println("Invalid command");
                        break;
                    }
                    this.userService.loginUser(new UserLoginDto(command[1], command[2]));
                    break;

                case "Logout":
                    this.userService.logout();
                    break;

                default:
                    System.out.println("Invalid command");
            }
        }
    }
}
