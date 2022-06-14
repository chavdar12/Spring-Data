package com.example.springDataIntro;

import com.example.springdataintro.services.AccountService;
import com.example.springdataintro.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ConsoleRunner implements CommandLineRunner {

    private final UserService userService;
    private final AccountService accountService;

    public ConsoleRunner(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            this.userService.register("Pesho", 25, new BigDecimal(1000));
        } catch (Exception e) {
            e.getStackTrace();
        }

        this.userService.addAccount(new BigDecimal(6908), 1L);

        try {
            this.accountService.withdrawMoney(new BigDecimal(150), 3L);
        } catch (Exception e) {
            e.getStackTrace();
        }

        this.accountService.transferMoney(new BigDecimal(500), 3L);
    }
}
