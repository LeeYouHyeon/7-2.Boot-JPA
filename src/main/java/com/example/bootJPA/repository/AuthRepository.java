package com.example.bootJPA.repository;

import com.example.bootJPA.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthRepository extends JpaRepository<AuthUser, Long> {
    List<AuthUser> findByEmail(String email);

    void deleteByEmail(String email);
}
