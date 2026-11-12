package com.example.springDataIntro.services;

import java.math.BigDecimal;

public interface UserService {
    void register(String username, Integer age, BigDecimal initialAmount) throws IllegalArgumentException;

    void addAccount(BigDecimal amount, Long id);
}
