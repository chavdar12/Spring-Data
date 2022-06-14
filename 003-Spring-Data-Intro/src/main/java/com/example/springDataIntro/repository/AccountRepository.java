package com.example.springDataIntro.repository;

import com.example.springDataIntro.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}