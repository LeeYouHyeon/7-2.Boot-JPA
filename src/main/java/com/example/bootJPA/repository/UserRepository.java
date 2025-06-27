package com.example.bootJPA.repository;

import com.example.bootJPA.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String>, UserCustomRepository {
}
