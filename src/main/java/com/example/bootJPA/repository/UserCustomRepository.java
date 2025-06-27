package com.example.bootJPA.repository;

import com.example.bootJPA.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCustomRepository {
    Page<User> searchUser(String type, String keyword, Pageable pageable);
}
