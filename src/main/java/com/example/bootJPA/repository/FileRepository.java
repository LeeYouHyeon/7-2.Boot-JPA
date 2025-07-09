package com.example.bootJPA.repository;

import com.example.bootJPA.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, String> {
  List<File> findByBno(long bno);
}
