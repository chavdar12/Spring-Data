package com.example.springDataIntro.services;

import com.example.springDataIntro.models.Account;
import com.example.springDataIntro.models.User;
import com.example.springDataIntro.repository.AccountRepository;
import com.example.springDataIntro.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    public UserServiceImpl(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public void register(String username, Integer age, BigDecimal initialAmount) {

        var user = new User();
        user.setUsername(username);
        user.setAge(age);

        this.userRepository.save(user);

        this.saveAccount(initialAmount, user);
    }

    @Override
    public void addAccount(BigDecimal amount, Long id) {
        User user = this.userRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        this.saveAccount(amount, user);

    }

    private void saveAccount(BigDecimal amount, User user) {
        var account = new Account();

        account.setBalance(amount);
        account.setUser(user);
        this.accountRepository.save(account);
    }
}
