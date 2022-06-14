package com.example.springDataIntro.repository;

import com.example.springDataIntro.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}