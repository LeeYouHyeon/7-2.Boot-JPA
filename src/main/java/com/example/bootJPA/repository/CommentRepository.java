package com.example.bootJPA.repository;

import com.example.bootJPA.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByBno(Long bno, Pageable pageable);
}
