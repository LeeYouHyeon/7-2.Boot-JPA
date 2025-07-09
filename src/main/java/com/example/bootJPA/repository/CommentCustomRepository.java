package com.example.bootJPA.repository;

import com.example.bootJPA.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentCustomRepository {
  Page<Comment> findByBno(Long bno, Pageable pageable);
}
