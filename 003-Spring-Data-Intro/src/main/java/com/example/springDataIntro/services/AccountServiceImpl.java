package com.example.springDataIntro.services;

import com.example.springDataIntro.models.Account;
import com.example.springDataIntro.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public void withdrawMoney(BigDecimal amount, Long id) {
        Account account = this.getAccount(id);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException();
        }

        account.setBalance(account.getBalance().subtract(amount));

        this.accountRepository.save(account);
    }

    @Override
    public void transferMoney(BigDecimal amount, Long id) {
        Account account = this.getAccount(id);

        account.setBalance(account.getBalance().add(amount));
        this.accountRepository.save(account);
    }

    private Account getAccount(Long id) {
        return this.accountRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }
}
